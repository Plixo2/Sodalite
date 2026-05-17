#version 460

layout (location = 0) in vec4 v_color;
layout (location = 0) out vec4 fragColor;

layout(std140, set = 3, binding = 0) uniform UniformBlock {
    vec3 color;
    mat4 model;
};

void main()
{
    fragColor = vec4(color, v_color.a);
}