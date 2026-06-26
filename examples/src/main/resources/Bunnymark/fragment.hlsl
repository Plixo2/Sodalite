
Texture2D<float4> Texture  : register(t0,  space2);
SamplerState      Sampler  : register(s0,  space2);

struct PSInput {
    float2 v_uv     : TEXCOORD0;
    nointerpolation float4 v_color  : TEXCOORD1;
};

float4 main(PSInput input) : SV_Target0 {
    float4 sampled = Texture.Sample(Sampler, input.v_uv);

    return sampled * input.v_color;
}