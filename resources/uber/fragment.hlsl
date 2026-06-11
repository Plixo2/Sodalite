
Texture2D<float4> Textures0  : register(t0,  space2);
Texture2D<float4> Textures1  : register(t1,  space2);
Texture2D<float4> Textures2  : register(t2,  space2);
Texture2D<float4> Textures3  : register(t3,  space2);
Texture2D<float4> Textures4  : register(t4,  space2);
Texture2D<float4> Textures5  : register(t5,  space2);
Texture2D<float4> Textures6  : register(t6,  space2);
Texture2D<float4> Textures7  : register(t7,  space2);

SamplerState Samplers0  : register(s0,  space2);
SamplerState Samplers1  : register(s1,  space2);
SamplerState Samplers2  : register(s2,  space2);
SamplerState Samplers3  : register(s3,  space2);
SamplerState Samplers4  : register(s4,  space2);
SamplerState Samplers5  : register(s5,  space2);
SamplerState Samplers6  : register(s6,  space2);
SamplerState Samplers7  : register(s7,  space2);

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
        default: return float4(0, 0, 0, 1);
    }
}


struct PSInput {
    float4 position          : SV_Position;
    float2 v_localPos        : TEXCOORD0;
    float2 v_uv              : TEXCOORD1;
    float4 v_fill_color      : TEXCOORD2;
    float4 v_outline_color   : TEXCOORD3;
    float2 v_size            : TEXCOORD4;
    float  v_radius          : TEXCOORD5;
    float  v_outline_width   : TEXCOORD6;
    nointerpolation uint v_textureIndex : TEXCOORD7;
    nointerpolation uint v_isTexture    : TEXCOORD8;
};

float sdRoundBox2(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
}

float sdRoundBoxAnnular(float2 position, float2 size, float radius, float outline) {
    return abs(sdRoundBox2(position, size - outline, radius - outline)) - outline;
}

float4 blend(float4 a, float4 b) {
    float outAlpha = a.a + b.a * (1.0 - a.a);
    if (outAlpha == 0.0) return float4(0.0, 0.0, 0.0, 0.0);
    float3 outRGB = (a.rgb * a.a + b.rgb * b.a * (1.0 - a.a)) / outAlpha;
    return float4(outRGB, outAlpha);
}

static const float smoothness = 1.0;

float4 main(PSInput input) : SV_Target {
    float4 fill   = input.v_fill_color;
    float  radius = input.v_radius;

    if (input.v_isTexture != 0u) {
        float4 texture_color = SampleTexture(input.v_textureIndex, input.v_uv);
        float3 color_mixed = lerp(texture_color.rgb, float3(1.0, 1.0, 1.0), radius);
        return float4(fill.rgb * color_mixed, texture_color.a * fill.a);
    }

    float2 half_size = input.v_size * 0.5;
    float2 centered  = input.v_localPos - half_size;

    float4 outline           = input.v_outline_color;
    float  outline_thickness = input.v_outline_width * 0.5;

    float fill_distance = sdRoundBox2(centered, half_size - outline_thickness, radius - outline_thickness);
    float fill_alpha    = fill.a * (1.0 - saturate(fill_distance * smoothness));
    float4 fill_color   = float4(fill.rgb, fill_alpha);

    float outline_dist  = sdRoundBoxAnnular(centered, half_size, radius, outline_thickness);
    float outline_alpha = outline.a * (1.0 - saturate(outline_dist * smoothness));
    float4 outline_color = float4(outline.rgb, outline_alpha);

    return blend(outline_color, fill_color);
}