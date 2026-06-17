
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


Minimal 'Hello Triangle' example using `SDL_gpu`, [originally written in C by Hamdy Elzanqali](https://hamdy-elzanqali.medium.com/let-there-be-triangles-sdl-gpu-edition-bd82cf2ef615)


```java
public class GPUHelloTriangle implements Callbacks {
    /// struct Vertex
    /// {
    ///     float x, y, z;      //vec3 position
    ///     float r, g, b, a;   //vec4 color
    /// };
    static StructLayout Vertex = MemoryLayout.structLayout(
            Layouts.VECTOR_3F.withName("position"),
            Layouts.VECTOR_4F.withName("color")
    );
    WriteBuffer<?> vertices;
    {
        this.vertices = ConstantWriteBuffer.allocate(ResourceSet.global(), Vertex, 3)
               .writeFloats( 0.0f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f)
               .writeFloats(-0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f)
               .writeFloats( 0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f);
    }

    /// struct UniformBuffer
    /// {
    ///     float time;
    /// };
    static StructLayout UniformBuffer = MemoryLayout.structLayout(
            Layouts.FLOAT.withName("time")
    );
    CStruct timeUniform = CStruct.allocate(ResourceSet.global(), UniformBuffer);

    Window window;
    Device device;
    Device.WindowClaim claim;
    Buffer vertexBuffer;
    GraphicsPipeline pipeline;

    @Override
    public AppResult onWindowCloseRequested(long timestamp, int windowID) {
        if (this.window.id() != windowID) {
            return AppResult.CONTINUE;
        }
        return AppResult.SUCCESS;
    }

    @Override
    public AppResult init(String[] args) throws IOException {
        this.window = Video.createWindow(
                ResourceSet.global(),
                "Hello, Triangle!",
                960, 540,
                WindowFlags.RESIZABLE
        );
        this.device = GPU.createDevice(
                ResourceSet.global(),
                ShaderFormat.SPIRV | ShaderFormat.DXIL,
                true,
                GPUDriver.optimal()
        );
        this.claim = this.device.claimWindow(this.window);

        var format = this.device.supportsShaderFormat(ShaderFormat.SPIRV)
                ? ShaderFormat.SPIRV
                : ShaderFormat.DXIL;
        var ext = format == ShaderFormat.SPIRV ? "spv" : "dxil";
        var dir = format == ShaderFormat.SPIRV ? "spirv" : "dxil";

        this.pipeline = this.device.createGraphicsPipeline(
                ResourceSet.global(),
                Shader.Creator.of(
                        format,
                        GPUHelloTriangle.class.getResourceAsStream("/GPUHelloTriangle/" + dir + "/vertex." + ext),
                        Shader.Parameters.of(0, 0, 0, 0)
                ),
                Shader.Creator.of(
                        format,
                        GPUHelloTriangle.class.getResourceAsStream("/GPUHelloTriangle/" + dir + "/fragment." + ext),
                        Shader.Parameters.of(0, 0, 0, 1)
                ),
                PrimitiveType.TRIANGLELIST,
                VertexInputState.of(0, Vertex, VertexInputState.Rate.VERTEX),
                RasterizerState.defaultValue(),
                MultisampleState.disabled(),
                DepthStencilState.disabled(),
                GraphicsPipelineTargetInfo.of(
                        this.device.getSwapchainTextureFormat(this.window),
                        ColorTargetBlendState.standardAlphaBlend()
                )
        );

        this.vertexBuffer = this.device.createBuffer(
                ResourceSet.global(),
                BufferUsageFlags.VERTEX,
                this.vertices.capacity()
        );

        try (var transferSet = ResourceSet.ofConfined()) {
            var transferBuffer = this.device.createTransferBuffer(
                    transferSet,
                    TransferBufferUsage.UPLOAD,
                    this.vertexBuffer.size()
            );
            try (var mapped = transferBuffer.map(this.device,Cycle.FALSE)) {
                mapped.memory().copyFrom(this.vertices.memory());
            }
            try (var commandBuffer = this.device.acquireCommandBuffer()) {
                try (var copyPass = commandBuffer.beginCopyPass()){
                    copyPass.upload(transferBuffer, this.vertexBuffer, Cycle.FALSE);
                }
            }
        }

        return AppResult.CONTINUE;
    }
    static Vector4f CLEAR_COLOR = new Vector4f(240/255.0f, 240/255.0f, 240/255.0f, 255/255.0f);
    @Override
    public AppResult iterate() {
        try (var commandBuffer = this.device.acquireCommandBuffer()) {
            var swapchain = commandBuffer.waitAndAcquireSwapchainTexture(this.window);
            if (swapchain == null) {
                return AppResult.CONTINUE;
            }
            var colorTarget0 = RenderPass.ColorTargetInfo.clear(
                    swapchain,
                    CLEAR_COLOR,
                    Cycle.FALSE
            );
            try (var renderPass = commandBuffer.beginRenderPass(null, colorTarget0)) {
                renderPass.bindPipeline(this.pipeline);
                renderPass.bindVertexBuffer(0, this.vertexBuffer);
                this.timeUniform.at("time").writeFloat(Timer.getTicksNS() / 1e9f);
                commandBuffer.pushFragmentUniform(0, this.timeUniform);
                renderPass.drawPrimitives(3, 1, 0, 0);
            }
        }
        return AppResult.CONTINUE;
    }

    @Override
    public void quit(AppResult result) {
        if (this.claim != null) {
            this.claim.close();
        }
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

- **Typed API** \
    Types are modeled using Java types, instead of raw `MemorySegment`s or `long` pointers. \
    Enums and `@MagicConstant` annotations replace untyped ints. \
    Builders for complex types. \
    Sensible defaults & good method overloads. 

- **No error-checking boilerplate** \
    Every SDL call is checked automatically, and failures throw a `SDL3Exception` with the `SDL_GetError()` message attached.

- **Opinionated but safe defaults** \
    Common pitfalls (wrong cleanup order, leaked handles, unchecked return values) are handled for you by design.

- **No manual memory management** \
    `ResourceSet` tracks native resources and releases them automatically:
  - `ofConfined()` for deterministic cleanup in a  try-with-resource block
  - `ofAuto()` lets the GC collect resources
  - `global()` covers resources that live for the entire application lifetime

--- 

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

## Downsides and Philosophy

Sodalite is not a thin wrapper, and it doesn't try to be.
It doesn't expose every single SDL function directly, so function can be at times hard to find.
When you are looking for a specific function or wrapper, you can seach for the `@sdlAPI` & `@sdlOther` tags.

Every wrapper provides a way to get to the underlying resource, so you can be as flexible as you want, when you need to.

## How to use Sodalite?
```
// todo
```



