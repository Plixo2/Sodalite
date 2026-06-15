package io.github.plixo2.sodalite.resource;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.init.Init;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/// Resource set manages the lifecycle of multiple resources.
///
/// - [ResourceSet#ofConfined()] should be used in a `try-with-resources`
///  block for manually managed resources.
/// - [ResourceSet#ofAuto()] should be used for gc-managed resources.
/// - [ResourceSet#global()] should be used for resources that live until
/// the end of the application (Until [Init#quit] is called).
///
///
/// [Events#pollEvent]/[Events#pollEvents] should be called periodically to ensure
/// cleanup of gc-managed resources.
///
/// [Init#quit] should be always called on application exit.
///
/// @see Resource
/// @see AutoResourceSet
/// @see GlobalResourceSet
/// @see ConfinedResourceSet
public sealed interface ResourceSet
        extends
            AutoCloseable,
            SegmentAllocator
        permits
            AutoResourceSet,
            ConfinedResourceSet,
            GlobalResourceSet
{

    /// Resource set automatically managed by the garbage collector.
    ///
    /// Resources registered to this set will be freed when the owner object is garbage collected.
    /// Make sure to call [Events#pollEvent]/[Events#pollEvents] or [PendingFrees#drain()]
    /// from the main thread periodically to free the pending resources.
    ///
    /// Resources registered to this resource set can be released in any order,
    /// so they should not reference each other.
    ///
    /// @threadSafety This resource set is thread-safe,
    ///               but all the resources will be freed on the main thread.
    static ResourceSet ofAuto() {
        return AutoResourceSet.create();
    }

    /// Global resource set for resources that live until the end of the application.
    ///
    /// Resources registered to this resource set will be released in the reverse order
    /// of registration when [Init#quit] is called.
    /// @threadSafety This resource set is thread-safe,
    ///               but all the resources will be freed on the main thread on application exit.
    static ResourceSet global() {
        interface Holder {
            ResourceSet INSTANCE = GlobalResourceSet.create();
        }
        return Holder.INSTANCE;
    }

    /// Confined resource set.
    /// Registered resources will be freed when the resource set is closed.
    ///
    /// Resources registered to this resource set will be released in the reverse
    /// order.
    ///
    /// @threadSafety Dont access the returned resource set from multiple threads simultaneously
    @CheckReturnValue
    static ResourceSet ofConfined() {
        return ConfinedResourceSet.create();
    }

    /// Returns a new arena that is closed when `parent` is closed, but can be closed independently.
    ///
    /// Resources registered to this resource set will be released in the reverse
    /// order.
    ///
    /// @param parent the parent resource set.
    /// @threadSafety Dont access the returned resource set from multiple threads simultaneously
    static ResourceSet ofConfined(ResourceSet parent) {
        return ConfinedResourceSet.create(parent);
    }

    /// Register a resource to this resource set.
    ///
    /// The `resource` parameter should never reference the object passed as `owner`,
    /// otherwise the owner will never be garbage collected,
    /// causing a leak for auto resource sets.
    ///
    /// @param owner the owner of the resource
    /// @param resource the resource to be freed.
    void register(ResourceObject owner, Resource resource);

    default void register(ResourceObject owner) {
        register(owner, () -> {});
    }

    // Never close the arena manually, it will be closed when the resource set is closed.
    @CanIgnoreReturnValue
    Arena arena();

    @Override
    MemorySegment allocate(long byteSize, long byteAlignment);

    /// @throws UnsupportedOperationException if this resource set does not support manual closing
    ///                                       (i.e. global, auto or manual resource set)
    @Override
    void close();
}
