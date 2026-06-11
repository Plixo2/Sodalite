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

uniform sampler2D textures[NUM_TEXTURES];

flat in int v_rectIndex;
flat in uint v_textureIndex;
in vec2 v_localPos;
in vec2 v_uv;

out vec4 out_color;

bool is_textured(uint texture) {
    return texture != 0u;
}


float sdRoundBox2(vec2 p, vec2 b, float r) {
    vec2 q = abs(p)-b+r;
    return min(max(q.x,q.y),0.0) + length(max(q,0.0)) - r;
}
float sdRoundBoxAnnular(vec2 position, vec2 size, float radius, float outline) {
    return abs(sdRoundBox2(position, size - outline, radius - outline)) - outline;
}

vec4 blendStandard(vec4 src, vec4 dst) {
    vec4 result;
    result.a = src.a + dst.a * (1.0 - src.a);
    result.rgb = (src.rgb * src.a + dst.rgb * dst.a * (1.0 - src.a));
    return result;
}

vec4 blend(vec4 a, vec4 b) {
    float outAlpha = a.a + b.a * (1.0 - a.a);
    if (outAlpha == 0.0) return vec4(0.0);
    vec3 outRGB = (a.rgb * a.a + b.rgb * b.a * (1.0 - a.a)) / outAlpha;
    return vec4(outRGB, outAlpha);
}

vec4 saturate(vec4 x) {
    return clamp(x, 0.0, 1.0);
}
float saturate(float x) {
    return clamp(x, 0.0, 1.0);
}

const float smoothness = 1.0 / 1.0;

void main() {
    Rect instance = rects[v_rectIndex];
    vec4 fill = instance.fill_color;
    float radius = instance.radius;

    if (is_textured(instance.isTexture)) {
        vec4 texture_color = texture(textures[v_textureIndex], v_uv);
        vec3 color_mixed = mix(texture_color.rgb, vec3(1.0), radius);
        out_color = vec4(fill.rgb * color_mixed, texture_color.a * fill.a);
        return;
    }

    vec2 size = instance.coords.zw;
    vec2 half_size = size * 0.5;
    vec2 centered = v_localPos - half_size;

    vec4 outline = instance.outline_color_or_uv;

    float outline_thickness = instance.outline_width * 0.5;

    float fill_distance = sdRoundBox2(centered, half_size - outline_thickness, radius - outline_thickness);
    float fill_alpha = fill.a * (1.0 - saturate(fill_distance * smoothness));
    vec4 fill_color = vec4(fill.rgb, fill_alpha);

    float outline_dist = sdRoundBoxAnnular(centered, half_size, radius, outline_thickness);
    float outline_alpha = outline.a * (1.0 - saturate(outline_dist * smoothness));
    vec4 outline_color = vec4(outline.rgb, outline_alpha);

    out_color = blend(outline_color, fill_color);
}
