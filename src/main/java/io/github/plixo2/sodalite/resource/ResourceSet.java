package io.github.plixo2.sodalite.resource;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.plixo2.sodalite.category.events.EventConsumer;
import io.github.plixo2.sodalite.category.events.Events;
import io.github.plixo2.sodalite.category.init.Init;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.ref.Cleaner;

/// Resource set manages the lifecycle of multiple resources.
///
/// - {@link ResourceSet#ofConfined()} should be used in a `try-with-resources`
///  block for manually managed resources.
/// - {@link ResourceSet#ofAuto()} should be used for gc-managed resources.
/// - {@link ResourceSet#global()} should be used for resources that
/// should only be freed when {@link Init#quit} is called.
///
/// {@link Events#pollEvent}/{@link Events#pollEvents} should be called periodically to ensure
/// cleanup of gc-managed resources.
///
/// @see Resource
/// @see AutoResourceSet
/// @see GlobalResourceSet
/// @see ConfinedResourceSet
public interface ResourceSet extends AutoCloseable, SegmentAllocator {

    /// Resource set automatically managed by the garbage collector.
    ///
    /// Resources registered to this set will be freed when the owner object is garbage collected.
    /// Make sure to call {@link Events#pollEvent}/{@link Events#pollEvents} periodically to
    /// free the pending resources.
    ///
    /// Objects registered to this resource set can be released in any order, so they should not reference each other.
    static ResourceSet ofAuto() {
        return AutoResourceSet.create();
    }

    /// Global resource set for resources that live until the end of the application.
    ///
    /// Resources registered to this resource set will be released in the reverse order
    /// of registration when {@link Init#quit} is called.
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
    @CheckReturnValue
    static ResourceSet ofConfined() {
        return ConfinedResourceSet.create();
    }

    /// Returns a new arena that is closed when `parent` is closed, but can be closed independently.
    static ResourceSet ofConfined(ResourceSet parent) {
        return ConfinedResourceSet.create(parent);
    }

    /// Register a resource to this resource set.
    /// @param owner the owner of the resource
    /// @param resource the resource to be freed. It should never
    ///                 reference the object passed as owner
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
    ///                                       (i.e. global or auto resource set)
    @Override
    void close();
}
