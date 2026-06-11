#version 460 core

struct Rect {
    vec4 coords;
    vec4 fill_color;
    vec4 outline_color_or_uv;
    float radius;
    float outline_width;
    uint texture;
    uint isTexture;
};

layout(std430, binding = 0) restrict readonly buffer InstanceData {
    Rect rects[];
};

layout(std430, binding = 1) restrict readonly buffer TransformData {
    mat4 transforms[];
};

uniform mat4 projection;
uniform int startIndex;

flat out int v_rectIndex;
flat out uint v_textureIndex;
out vec2 v_localPos;
out vec2 v_uv;

vec4 roundToPixels(vec4 pos) {
    return vec4(round(pos.xy), pos.zw);
}

void main() {
    int index = startIndex + gl_InstanceID;
    Rect rect = rects[index];
    mat4 transform = transforms[index];

    int id = gl_VertexID;
    vec2 n_quad_coords = vec2(
        id & 1,
        id >> 1
    );
    vec2 position = instance.coords.xy;
    vec2 size = instance.coords.zw;
    vec2 n_pos = n_quad_coords * size;
    vec4 world_pos = transform * vec4(position + n_pos, 0.0, 1.0);
    world_pos = roundToPixels(world_pos);
    gl_Position = projection * world_pos;

    v_localPos = n_pos;
    v_rectIndex = index;
    v_textureIndex = rect.texture;

    vec4 uvBias = rect.outline_color_or_uv;
    v_uv = mix(uvBias.xy, uvBias.zw, n_quad_coords);
}