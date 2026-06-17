
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
// todo, see example folder
```

</details>


## What is Sodalite?

Sodalite is a high-level Java library built on top of SDL bindings, designed to make SDL feel 
natural in Java rather than like a thin layer over C.
It handles native resource lifetimes, error checking, and low-level memory details automatically.

Sodalite wraps SDL's integer constants into proper enums and `@MagicConstant` bit flags,
and models types using java classes and ADTs (sealed interfaces and records).
It also ships the raw Jextract-generated bindings alongside the high-level API, 
so you can drop down to them directly when you need full control, just include 
`import static org.libsdl.sdl.SDL3_h.*;` and you're good to go.

The included Jextract bindings are slightly altered:
- Every C struct has a `create(SegmentAllocator, ...)`/`initialize(MemorySegment, ...)` method 
that initializes all fields in one go, so you can't forget to set a field
- Primitive constants are made public to be used in switch statements

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
            GPUDriver.vulkan()
    );
    try (var _ = device.claimWindow(window)) {
        try (var commandBuffer = device.acquireCommandBuffer()) {
            // use command buffer
        }
    }
}
```

