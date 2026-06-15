
<br>

<div align="center">


<img src="Logo.svg" alt="Logo" width="300">

<h3 align="center">High-Level Java Bindings for SDL</h2>

</div>

See [porting progress](Progress.md)

<br>

See the full list of examples [here](examples/src/main/java/io/github/plixo2/sodalite/examples),
including explanations.

<details>

<summary>Minimal Example</summary>

Minimal GPU example using `SDL_gpu`, [originally written in C by Hamdy Elzanqali](https://hamdy-elzanqali.medium.com/let-there-be-triangles-sdl-gpu-edition-bd82cf2ef615)


```java
void main() {
    try (var appLifeResources = ResourceSet.ofConfined()) {
        var window = Video.createWindow(
                appLifeResources,
                "GPU Clear Demo",
                960, 540,
                WindowFlags.RESIZABLE
        );
        var device = GPU.createDevice(
                appLifeResources,
                ShaderFormat.SPIRV | ShaderFormat.DXIL | ShaderFormat.MSL,
                false,
                PreferredDriver.OPTIMAL
        );
        var eventConsumer = new EventConsumer() {
            boolean running = true;
            @Override
            public void onWindowCloseRequested(long timestamp, int windowID) {
                if (window.id() == windowID) {
                    this.running = false;
                }
            }
        };

        try (var _ = device.claimWindow(window)) {
            while (eventConsumer.running) {
                Events.pollEvents(eventConsumer);
                try (var commandBuffer = device.acquireCommandBuffer()) {
                    var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(window);
                    if (swapchain == null) {
                        return;
                    }
                    var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                            swapchain,
                            new Vector4f(0.1f, 0.2f, 0.3f, 1.0f),
                            Cycle.FALSE
                    );
                    try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                        // just clear
                    }
                }
            }
        }
    } finally {
        Init.quit();
    }
}

```

</details>


## What is Sodalite?

Sodalite is a high-level Java library built on top of SDL bindings, designed to make SDL feel 
natural in Java rather than like a thin layer over C.
It handles native resource lifetimes, error checking, and low-level memory details automatically.

Sodalite wraps SDL's integer constants into proper enums and `@MagicConstant` bit flags,
and models types using java classes and ADTs (sealed interfaces and records).
It also ships the raw Jextract-generated bindings alongside the high-level API, 
so you can drop down to them directly when you need full control.

## Why use Sodalite instead of direct bindings?

Working directly with SDL bindings in Java means managing memory manually,
checking every return values, calling cleanup functions in the right order, 
and passing raw `MemorySegment` pointers everywhere.
Sodalite replaces all of that with an idiomatic Java API:


- **No error-checking boilerplate** 

    every SDL call is checked automatically, and failures throw exceptions with the `SDL_GetError()` message attached.

- **Typed API**

    enums and `@MagicConstant` annotations replace untyped integer constants; ADTs model SDL types instead of raw pointers.

- **Opinionated but safe defaults** 
  
    common pitfalls (wrong cleanup order, leaked handles, unchecked return values) are handled for you by design.

- **No manual memory management**

    `ResourceSet` tracks native resources and releases them automatically:
  - `ofConfined()` for deterministic cleanup in a  try-with-resource block
  - `ofAuto()` lets the GC collect resources
  - `global()` covers resources that live for the entire application lifetime


The difference in practice:

**With direct bindings:**

```java
MemorySegment deviceSegment;
try (var arena = Arena.ofConfined()) {
    deviceSegment = SDL_CreateGPUDevice(
            SDL_GPU_SHADERFORMAT_SPIRV(),
            false,
            arena.allocateFrom("vulkan")
    );
}
if (deviceSegment.address() == 0) {
    throw new RuntimeException(SDL_GetError().getString(0));
}
var claimOK = SDL_ClaimWindowForGPUDevice(deviceSegment, windowSegment);
if (!claimOK) {
    SDL_DestroyGPUDevice(deviceSegment);
    throw new RuntimeException(SDL_GetError().getString(0));
}
var commandBufferSegment = SDL_AcquireGPUCommandBuffer(deviceSegment);
if (commandBufferSegment.address() == 0) {
    SDL_ReleaseWindowFromGPUDevice(deviceSegment, windowSegment);
    throw new RuntimeException(SDL_GetError().getString(0));
}
try {
    try {
        // use command buffer
    } finally {
        var submitOK = SDL_SubmitGPUCommandBuffer(commandBufferSegment);
        if (!submitOK) {
            throw new RuntimeException(SDL_GetError().getString(0));
        }
    }
} finally {
    SDL_ReleaseWindowFromGPUDevice(deviceSegment, windowSegment);
    SDL_DestroyGPUDevice(deviceSegment);
}
```

**With Sodalite:**
```java
try (var appLifeResources = ResourceSet.ofConfined()) {
    var device = GPU.createDevice(
            appLifeResources,
            ShaderFormat.SPIRV,
            false,
            PreferredDriver.VULKAN
    );
    try (var _ = device.claimWindow(window)) {
        try (var commandBuffer = device.acquireCommandBuffer()) {
            // use command buffer
        }
    }
}
```


[//]: # ()
[//]: # (## What is Sodalite?)

[//]: # (- Idiomatic & fully typed API.)

[//]: # (- Use of @MagicConstant for bit flags & Enums instead of untyped integer constants)

[//]: # (- Additional safety checks)

[//]: # (- Automatic SDL checks under the hood, throwing exceptions with SDL_GetError&#40;&#41; messages when something goes wrong)

[//]: # (- Opinionated design decisions)

[//]: # (- `ResourceSet` to manage native resources)

[//]: # (- Use of ADT's &#40;sealed interfaces + records&#41; to model types)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (## Why use Sodalite instead of direct bindings?)

[//]: # (- Could use SDL bindings generated by Jextract or from from JWJGL)

[//]: # (- Uses the modern `MemorySegment` under the hood instead of `ByteBuffer` or `long`)

[//]: # (- No manual memory management or pointer arithmetic)

[//]: # (- Sodalite also ships the generated Jextract bindings, )

[//]: # (so you can use them directly if you want to. )

[//]: # (These bindings also include generated constructors and initializers for c-structs, )

[//]: # (as well as making constants public to use them in switch statements.)

[//]: # ()
[//]: # (Lets compare the code for creating a GPU device and acquiring a command buffer:)

[//]: # ()
[//]: # (With Sodelite:)

[//]: # ()
[//]: # (```java)

[//]: # (try &#40;var appLifeResources = ResourceSet.ofConfined&#40;&#41;&#41; {)

[//]: # (    var device = GPU.createDevice&#40;)

[//]: # (            appLifeResources,)

[//]: # (            ShaderFormat.SPIRV,)

[//]: # (            false,)

[//]: # (            PreferredDriver.VULKAN)

[//]: # (    &#41;;)

[//]: # (    try &#40;var _ = device.claimWindow&#40;window&#41;&#41; {)

[//]: # (        try &#40;var commandBuffer = device.acquireCommandBuffer&#40;&#41;&#41; {)

[//]: # (            // use command buffer)

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (With direct bindings:)

[//]: # ()
[//]: # (```java)

[//]: # (MemorySegment deviceSegment;)

[//]: # (try &#40;var arena = Arena.ofConfined&#40;&#41;&#41; {)

[//]: # (    deviceSegment = SDL_CreateGPUDevice&#40;)

[//]: # (            SDL_GPU_SHADERFORMAT_SPIRV&#40;&#41;,)

[//]: # (            false,)

[//]: # (            arena.allocateFrom&#40;"vulkan"&#41;)

[//]: # (    &#41;;)

[//]: # (})

[//]: # (if &#40;deviceSegment.address&#40;&#41; == 0&#41; {)

[//]: # (    throw new RuntimeException&#40;SDL_GetError&#40;&#41;.getString&#40;0&#41;&#41;;)

[//]: # (})

[//]: # (var claimOK = SDL_ClaimWindowForGPUDevice&#40;deviceSegment, windowSegment&#41;;)

[//]: # (if &#40;!claimOK&#41; {)

[//]: # (    SDL_DestroyGPUDevice&#40;deviceSegment&#41;;)

[//]: # (    throw new RuntimeException&#40;SDL_GetError&#40;&#41;.getString&#40;0&#41;&#41;;)

[//]: # (})

[//]: # (var commandBufferSegment = SDL_AcquireGPUCommandBuffer&#40;deviceSegment&#41;;)

[//]: # (if &#40;commandBufferSegment.address&#40;&#41; == 0&#41; {)

[//]: # (    SDL_ReleaseWindowFromGPUDevice&#40;deviceSegment, windowSegment&#41;;)

[//]: # (    throw new RuntimeException&#40;SDL_GetError&#40;&#41;.getString&#40;0&#41;&#41;;)

[//]: # (})

[//]: # (try {)

[//]: # (    try {)

[//]: # (        // use command buffer)

[//]: # (    } finally {)

[//]: # (        var submitOK = SDL_SubmitGPUCommandBuffer&#40;commandBufferSegment&#41;;)

[//]: # (        if &#40;!submitOK&#41; {)

[//]: # (            throw new RuntimeException&#40;SDL_GetError&#40;&#41;.getString&#40;0&#41;&#41;;)

[//]: # (        })

[//]: # (    })

[//]: # (} finally {)

[//]: # (    SDL_ReleaseWindowFromGPUDevice&#40;deviceSegment, windowSegment&#41;;)

[//]: # (    SDL_DestroyGPUDevice&#40;deviceSegment&#41;;)

[//]: # (})

[//]: # (```)

