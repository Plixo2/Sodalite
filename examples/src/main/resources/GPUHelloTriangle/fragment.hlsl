struct PSInput
{
    float4 v_color : TEXCOORD0;
};

cbuffer UniformBlock : register(b0, space3)
{
    float time;
};

float4 main(PSInput input) : SV_Target0
{
    float pulse = sin(time * 2.0) * 0.5 + 0.5; // range [0, 1]
    return float4(input.v_color.rgb * (0.8 + pulse * 0.5), input.v_color.a);
}