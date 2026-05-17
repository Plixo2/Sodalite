package io.github.plixo2.sodalite.resource;

import io.github.plixo2.sodalite.category.events.Events;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.ref.Cleaner;

/// Resource set manages the lifecycle of multiple resources.
///
/// {@link ResourceSet#ofConfined()} should be used in a `try-with-resources`
/// block for manually managed resources. \
/// {@link ResourceSet#ofAuto()} should be used for gc-managed resources. \
/// {@link ResourceSet#global()} should be used for resources that should never be freed.
///
/// {@link Events#pollEvent()} should be called periodically to ensure
/// cleanup of gc-managed resources.
///
/// @see Resource
/// @see AutoResourceSet
/// @see GlobalResourceSet
/// @see ConfinedResourceSet
public interface ResourceSet extends AutoCloseable, SegmentAllocator {

    /// Resource set automatically managed by the garbage collector.
    /// Resources registered to this set will be freed when the owner object is garbage collected.
    static ResourceSet ofAuto() {
        interface CleanerHolder {
            Cleaner CLEANER = Cleaner.create();
        }
        return AutoResourceSet.create(CleanerHolder.CLEANER);
    }

    /// Global resource set for resources that should never be freed.
    /// It will not keep any reference to the registered resource or the owner
    static ResourceSet global() {
        interface Holder {
            ResourceSet INSTANCE = GlobalResourceSet.create();
        }
        return Holder.INSTANCE;
    }

    /// Single threaded, confined resource set.
    /// Registered resources will be freed when the resource set is closed
    static ResourceSet ofConfined() {
        return ConfinedResourceSet.create(Thread.currentThread());
    }

    /// Register a resource to this resource set.
    /// @param owner the owner of the resource
    /// @param resource the resource to be freed. It should never
    ///                 reference the object passed as owner
    void register(ResourceObject owner, Resource resource);


    default void register(ResourceObject owner) {
        register(owner, () -> {});
    }


    @Override
    MemorySegment allocate(long byteSize, long byteAlignment);

    /// @throws UnsupportedOperationException if this resource set does not support manual closing
    ///                                       (i.e. global or auto resource set)
    @Override
    void close();
}
