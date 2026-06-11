#version 460

layout(location = 0) in vec4 v_color;
layout(location = 1) in vec2 v_uv;

layout(location = 0) out vec4 fragColor;

layout(std140, set = 3, binding = 0) uniform UniformBlock {
    vec3 color;
    mat4 model;
};

layout(set = 2, binding = 0) uniform sampler2D Textures1;
layout(set = 2, binding = 1) uniform sampler2D Textures2;

void main() {
    fragColor = texture(Textures1, v_uv) + texture(Textures2, v_uv);
//    fragColor = texture(Textures1[0], v_uv);
}