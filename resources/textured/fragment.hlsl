struct FSInput {
    float4 v_color : TEXCOORD0;
    float2 v_uv    : TEXCOORD1;
};

cbuffer UniformBlock : register(b0, space3) {
    float3 color;
    float4x4 model;
};


Texture2D<float4> Textures0  : register(t0,  space2);
Texture2D<float4> Textures1  : register(t1,  space2);
Texture2D<float4> Textures2  : register(t2,  space2);
Texture2D<float4> Textures3  : register(t3,  space2);
Texture2D<float4> Textures4  : register(t4,  space2);
Texture2D<float4> Textures5  : register(t5,  space2);
Texture2D<float4> Textures6  : register(t6,  space2);
Texture2D<float4> Textures7  : register(t7,  space2);
Texture2D<float4> Textures8  : register(t8,  space2);
Texture2D<float4> Textures9  : register(t9,  space2);
Texture2D<float4> Textures10 : register(t10, space2);
Texture2D<float4> Textures11 : register(t11, space2);
Texture2D<float4> Textures12 : register(t12, space2);
Texture2D<float4> Textures13 : register(t13, space2);

SamplerState Samplers0  : register(s0,  space2);
SamplerState Samplers1  : register(s1,  space2);
SamplerState Samplers2  : register(s2,  space2);
SamplerState Samplers3  : register(s3,  space2);
SamplerState Samplers4  : register(s4,  space2);
SamplerState Samplers5  : register(s5,  space2);
SamplerState Samplers6  : register(s6,  space2);
SamplerState Samplers7  : register(s7,  space2);
SamplerState Samplers8  : register(s8,  space2);
SamplerState Samplers9  : register(s9,  space2);
SamplerState Samplers10 : register(s10, space2);
SamplerState Samplers11 : register(s11, space2);
SamplerState Samplers12 : register(s12, space2);
SamplerState Samplers13 : register(s13, space2);

float4 SampleTexture(int index, float2 uv)
{
    switch (index)
    {
        case 0:  return Textures0.Sample(Samplers0,   uv);
        case 1:  return Textures1.Sample(Samplers1,   uv);
        case 2:  return Textures2.Sample(Samplers2,   uv);
        case 3:  return Textures3.Sample(Samplers3,   uv);
        case 4:  return Textures4.Sample(Samplers4,   uv);
        case 5:  return Textures5.Sample(Samplers5,   uv);
        case 6:  return Textures6.Sample(Samplers6,   uv);
        case 7:  return Textures7.Sample(Samplers7,   uv);
        case 8:  return Textures8.Sample(Samplers8,   uv);
        case 9:  return Textures9.Sample(Samplers9,   uv);
        case 10: return Textures10.Sample(Samplers10, uv);
        case 11: return Textures11.Sample(Samplers11, uv);
        case 12: return Textures12.Sample(Samplers12, uv);
        case 13: return Textures13.Sample(Samplers13, uv);
        default: return float4(0, 0, 0, 1);
    }
}

float4 main(FSInput input) : SV_Target0 {
    //return input.v_color * float4(color, 1.0);
    float4 first = SampleTexture(1, input.v_uv);
//     float4 second = Textures1.Sample(Samplers1, input.v_uv);
//     return (first + second) * 0.5;
    return first;
}