package io.github.plixo2.sodalite.examples.bunnymark;

import io.github.plixo2.sodalite.category.gpu.*;

import io.github.plixo2.sodalite.resource.ResourceSet;
import org.joml.Vector4f;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;

public class BunnyStorage {
    private static final int INITIAL_CAPACITY = 1024;
    private static final long BUNNY_BYTE_SIZE = Bunny.LAYOUT.byteSize();


    private final ResourceSet resourceSet;
    private final Device device;

    private BoundContainer container;

    public BunnyStorage(
            ResourceSet resources,
            Device device
    ) {
        this.resourceSet = resources;
        this.device = device;
        this.container = new BoundContainer(resources, device, INITIAL_CAPACITY);
    }

    public void add(int count, Vector4f color) {
        while (!this.container.hasCapacity(count)) {
            this.container = this.container.transfer(this.resourceSet, this.device);
        }

        for (int i = 0; i < count; i++) {
             this.container.sprites[this.container.count++] = new Bunny(color, 32, 32);
        }
    }

    public void update(boolean paralell, Vector4f viewport) {
        var sprites = this.container.sprites;
        var count = this.container.count;

        forEachRange(paralell, count, (start, end) -> {
            for (int i = start; i < end; i++) {
                sprites[i].update(viewport);
            }
        });
    }

    public void upload(boolean paralell, Device device, CopyPass copyPass) {
        var container = this.container;
        var sprites = container.sprites;
        var count = this.container.count;
        if (count == 0) {
            return; // cannot upload zero bytes
        }

        try (var mapped = container.transferBuffer.map(device, Cycle.TRUE)) {
            var memory = mapped.memory();
            forEachRange(paralell, count, (start, end) -> {
                for (int i = start; i < end; i++) {
                    sprites[i].put(memory, i * BUNNY_BYTE_SIZE);
                }
            });
        }

        var byteCount = count * BUNNY_BYTE_SIZE;
        copyPass.upload(container.transferBuffer, container.buffer, byteCount, Cycle.TRUE);
    }

    public int count() {
        return this.container.count;
    }
    public Buffer currentBuffer() {
        return this.container.buffer;
    }

    private static class BoundContainer {

        private final int capacity;
         private Bunny[] sprites;
        private int count = 0;

        private final ResourceSet owner;
        private final TransferBuffer transferBuffer;
        private final Buffer buffer;


        private BoundContainer(ResourceSet parent, Device device, int capacity) {
            this.capacity = capacity;
             this.sprites = new Bunny[capacity];

            this.owner = ResourceSet.ofConfined(parent);

            var byteCount = capacity * BUNNY_BYTE_SIZE;
            this.transferBuffer = device.createTransferBuffer(
                    this.owner,
                    TransferBufferUsage.UPLOAD,
                    byteCount
            );
            this.buffer = device.createBuffer(
                    this.owner,
                    BufferUsageFlags.VERTEX,
                    byteCount
            );
        }

        private boolean hasCapacity(int count) {
            return this.count + count <= this.capacity;
        }

        /// Transfers the bunnis and grows the buffer.
        /// The data associated with this BoundContainer will be freed after this call.
        ///
        /// @return the new BoundContainer, with the same bunnies, but with a larger capacity.
        private BoundContainer transfer(ResourceSet parent, Device device) {
            var newCapacity = Math.max(64, this.capacity * 2);
            var newContainer = new BoundContainer(parent, device, newCapacity);

            System.arraycopy(this.sprites, 0, newContainer.sprites, 0, this.count);
            newContainer.count = this.count;

            this.sprites = null;
            this.owner.close();

            return newContainer;
        }


    }

    private static final int PARALLEL_THRESHOLD = 16_384;
    private static final int WORKER_COUNT = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
    private static final ExecutorService UPDATE_EXECUTOR = Executors.newFixedThreadPool(WORKER_COUNT, task -> {
        var thread = new Thread(task, "bunnymark-update");
        thread.setDaemon(true);
        return thread;
    });

    private static void forEachRange(boolean paralell, int count, BiConsumer<Integer, Integer> rangeConsumer) {
        if (!paralell || count < PARALLEL_THRESHOLD || WORKER_COUNT <= 1) {
            rangeConsumer.accept(0, count);
            return;
        }

        var taskCount = Math.min(WORKER_COUNT, count);
        var chunkSize = (count + taskCount - 1) / taskCount;
        var futures = new Future<?>[taskCount];

        for (int worker = 0; worker < taskCount; worker++) {
            var start = worker * chunkSize;
            var end = Math.min(start + chunkSize, count);
            futures[worker] = UPDATE_EXECUTOR.submit(() -> rangeConsumer.accept(start, end));
        }

        await(futures);
    }

    private static void await(Future<?>[] futures) {
        for (var future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while processing bunnies", e);
            } catch (ExecutionException e) {
                var cause = e.getCause();
                if (cause instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                }
                if (cause instanceof Error error) {
                    throw error;
                }
                throw new RuntimeException(cause);
            }
        }
    }
}
