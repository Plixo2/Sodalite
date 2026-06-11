package io.github.plixo2.uiiii;

import io.github.plixo2.sodalite.category.gpu.RenderPass;
import io.github.plixo2.sodalite.category.gpu.Sampler;
import io.github.plixo2.sodalite.category.gpu.Texture;

public class SamplerArray {

    private final int capacity;
    private int count;
    private final Texture[] textures;
    private final Sampler[] samplers;
    private final Texture defaultTexture;
    private final Sampler defaultSampler;

    public SamplerArray(
            int capacity,
            Texture defaultTexture,
            Sampler defaultSampler
    ) {
        this.capacity = capacity;
        this.count = 0;
        this.textures = new Texture[capacity];
        this.samplers = new Sampler[capacity];
        this.defaultTexture = defaultTexture;
        this.defaultSampler = defaultSampler;
    }

    public void bindFragmentSamplers(
            RenderPass renderPass,
            int firstSlot
    ) {
        for (var i = this.count; i < this.capacity; i++) {
            this.textures[i] = this.defaultTexture;
            this.samplers[i] = this.defaultSampler;
        }

        renderPass.bindFragmentSamplers(firstSlot, this.textures, this.samplers);
    }

    public int put(Texture texture, Sampler sampler) {
        var index = findSampler(texture, sampler);
        if (index != -1) {
            return index;
        }
        if (this.count >= this.capacity) {
            return -1;
        }
        var indexToUse = this.count;
        this.samplers[indexToUse] = sampler;
        this.textures[indexToUse] = texture;
        this.count++;
        return indexToUse;
    }

    private int findSampler(Texture texture, Sampler sampler) {
        // iterate backwards for locality
        for (var i = this.count - 1; i >= 0; i--) {
            if (texture == this.textures[i] && sampler == this.samplers[i]) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        this.count = 0;
    }


}
