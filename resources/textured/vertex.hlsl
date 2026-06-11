struct VSInput {
    float3 a_position : TEXCOORD0;
    float4 a_color    : TEXCOORD1;
    float2 a_uv       : TEXCOORD2;
};

struct VSOutput {
    float4 position : SV_Position;
    float4 v_color  : TEXCOORD0;
    float2 v_uv     : TEXCOORD1;
};

VSOutput main(VSInput input) {
    VSOutput output;
    output.position = float4(input.a_position, 1.0f);
    output.v_color  = input.a_color;
    output.v_uv     = input.a_uv;
    return output;
}