struct VSInput {
    float2 position : TEXCOORD0;
    float2 size     : TEXCOORD1;
    float4 color    : TEXCOORD2;
};

cbuffer Uniforms : register(b0, space1) {
    float4x4 u_projection;
};

struct VSOutput {
    float4 position : SV_Position;
    float2 v_uv     : TEXCOORD0;
    nointerpolation float4 v_color  : TEXCOORD1;
};


VSOutput main(VSInput sprite, uint vertexID : SV_VertexID) {
    float2 vertex_coords = float2(
        vertexID & 1,
        vertexID >> 1
    );
    float2 position = sprite.position + vertex_coords * sprite.size;

    VSOutput output;
    output.position = mul(u_projection, float4(position, 0.0, 1.0));
    output.v_uv = vertex_coords;
    output.v_color = sprite.color;
    return output;
}
