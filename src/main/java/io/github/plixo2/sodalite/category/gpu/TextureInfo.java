package io.github.plixo2.sodalite.category.gpu;



/// Call {@link TextureCreateInfo#create)}, {@link TextureBuilder#build} or
/// {@link Texture#createEmpty} to create the texture.
///
/// @see TextureCreateInfo
/// @see TextureBuilder
/// @see Texture
public sealed interface TextureInfo permits TextureCreateInfo, TextureBuilder, Texture {

    TextureType type();
    TextureFormat format();
    @TextureUsageFlags int usage();
    int width();
    int height();
    int layerCountOrDepth();
    int mipLevelCount();
    SampleCount sampleCount();


}
