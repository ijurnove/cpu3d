package com.ijurnove.cpu3d;

/**
 * <code>SceneFlag</code> values are used as keys in <code>SceneFlags</code>. Each flag has a corresponding value in each <code>SceneFlags</code> object.
 */
public enum SceneFlag {
    /**
     * Enables or disables backface culling. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 1.
     */
    DO_BACKFACE_CULLING,

    /**
     * Enables or disables backface culling. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 1.
     */
    DO_GAMMA_CORRECTION,

    /**
     * Enables or disables shadows. A value of 0 turns them off, and 1 turns them on.
     * <br>
     * Default value of 1.
     */
    DO_SHADOWS,

    /**
     * Enables or disables lighting. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 1. 
     */
    DO_LIGHTING,

    /**
     * Enables or disables wireframe rendering. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 0. 
     */
    WIREFRAME,

    /**
     * Enables or disables displaying point lights with an icon. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 0. 
     */
    DISPLAY_LIGHTS,

    /**
     * Controls the gamma correction value.
     * <br>
     * Default value of 2.2. 
     */
    GAMMA
}
