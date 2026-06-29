package io.github.plixo2.sodalite.category.video;


import io.github.plixo2.sodalite.Internal;

/// @sdlAPI SDL_GLAttr
public enum GLAttr {
    RED_SIZE,
    GREEN_SIZE,
    BLUE_SIZE,
    ALPHA_SIZE,
    BUFFER_SIZE,
    DOUBLEBUFFER,
    DEPTH_SIZE,
    STENCIL_SIZE,
    ACCUM_RED_SIZE,
    ACCUM_GREEN_SIZE,
    ACCUM_BLUE_SIZE,
    ACCUM_ALPHA_SIZE,
    STEREO,
    MULTISAMPLEBUFFERS,
    MULTISAMPLESAMPLES,
    ACCELERATED_VISUAL,
    RETAINED_BACKING,
    CONTEXT_MAJOR_VERSION,
    CONTEXT_MINOR_VERSION,
    CONTEXT_FLAGS,
    CONTEXT_PROFILE_MASK,
    SHARE_WITH_CURRENT_CONTEXT,
    FRAMEBUFFER_SRGB_CAPABLE,
    CONTEXT_RELEASE_BEHAVIOR,
    CONTEXT_RESET_NOTIFICATION,
    CONTEXT_NO_ERROR,
    FLOATBUFFERS,
    EGL_PLATFORM,

    ;
    public static GLAttr fromCode(int code) {
        return Internal.enumFromCode(GLAttr.class, code, RED_SIZE);
    }

    public int code() {
        return this.ordinal();
    }
}
