struct Rect {
    float4x4 transform;
    float4 coords;
    float4 fill_color;
    float4 outline_color_or_uv;
    float radius;
    float outline_width;
    uint texture;
    uint isTexture;
};

// struct Transform {
//     float4x4 matrix;
// };

StructuredBuffer<Rect>      Rects      : register(t0, space0);
// StructuredBuffer<Transform> Transforms : register(t1, space0);

cbuffer Uniforms : register(b0, space1) {
    float4x4 projection;
    int      startIndex;
};

struct VSOutput {
    float4 position          : SV_Position;
    float2 v_localPos        : TEXCOORD0;
    float2 v_uv              : TEXCOORD1;
    float4 v_fill_color      : TEXCOORD2;
    float4 v_outline_color   : TEXCOORD3;
    float2 v_size            : TEXCOORD4;
    float  v_radius          : TEXCOORD5;
    float  v_outline_width   : TEXCOORD6;
    uint   v_textureIndex    : TEXCOORD7;
    uint   v_isTexture       : TEXCOORD8;
};

float4 roundToPixels(float4 pos) {
    return float4(round(pos.xy), pos.zw);
}

VSOutput main(uint vertexID : SV_VertexID, uint instanceID : SV_InstanceID) {
    int index = startIndex + (int)instanceID;
    Rect rect = Rects[index];
//     float4x4 transform = Transforms[index].matrix;
    float4x4 transform = rect.transform;

    float2 n_quad_coords = float2(
        vertexID & 1,
        vertexID >> 1
    );

    float2 position = rect.coords.xy;
    float2 size     = rect.coords.zw;
    float2 n_pos    = n_quad_coords * size;

    float4 world_pos = mul(transform, float4(position + n_pos, 0.0, 1.0));
    world_pos = roundToPixels(world_pos);

    VSOutput output;
    output.position        = mul(projection, world_pos);
    output.v_localPos      = n_pos;
    output.v_uv            = lerp(rect.outline_color_or_uv.xy, rect.outline_color_or_uv.zw, n_quad_coords);
    output.v_fill_color    = rect.fill_color;
    output.v_outline_color = rect.outline_color_or_uv;
    output.v_size          = size;
    output.v_radius        = rect.radius;
    output.v_outline_width = rect.outline_width;
    output.v_textureIndex  = rect.texture;
    output.v_isTexture     = rect.isTexture;
    return output;
}