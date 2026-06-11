package io.github.plixo2.sodalite.category.video;

import io.github.plixo2.sodalite.category.gpu.GPU;

public class GPUPackageAccess {

    public GPUPackageAccess(GPU gpu) {
        if (gpu == null) {
            throw new IllegalArgumentException("For internal use only");
        }
    }

    public void setClaimedGPU(Window window, boolean claimed) {
        window.setClaimedGPU(claimed);
    }

}
