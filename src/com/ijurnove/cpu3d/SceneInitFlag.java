package com.ijurnove.cpu3d;

/**
 * <code>SceneInitFlag</code> values are used as keys in <code>SceneInitFlags</code>. Each flag has a corresponding value in each <code>SceneInitFlags</code> object.
 */
public enum SceneInitFlag {
    /**
     * Sets the width of the image returned by <code>Scene.render()</code>.
     * <br>
     * Default value is the width of the screen.
     */
    IMAGE_SIZE_ACROSS,

    /**
     * Sets the height of the image returned by <code>Scene.render()</code>.
     * <br>
     * Default value is the height of the screen.
     */
    IMAGE_SIZE_UP,

    /**
     * Modifies the width of the image being rendered. The image is automatically upscaled back to the width and height given by <code>IMAGE_SIZE_ACROSS</code> and
     * <code>IMAGE_SIZE_UP</code>.
     * <br>
     * Default value is 1.
     */
    RESOLUTION_MULTIPLIER_ACROSS,

    /**
     * Modifies the height of the image being rendered. The image is automatically upscaled back to the width and height given by <code>IMAGE_SIZE_ACROSS</code> and
     * <code>IMAGE_SIZE_UP</code>.
     * <br>
     * Default value is 1.
     */
    RESOLUTION_MULTIPLIER_UP,

    /**
     * Modifies the shadow rendering width. Higher values will result in sharper shadows, but longer rendering times.
     * <br>
     * Default value is 2048. 
     */
    SHADOW_RESOLUTION_ACROSS,
    

    /**
     * Modifies the shadow rendering height. Higher values will result in sharper shadows, but longer rendering times.
     * <br>
     * Default value is 2048. 
     */
    SHADOW_RESOLUTION_UP,

    /**
     * When a <code>Scene</code> is being rendered, the screen is split into segments vertically and horizontally. Each segment is rendered by a different thread.
     * <code>THREADS_ACROSS</code> controls the number of horizontal divisions.
     * <br>
     * Default value is 3.
     */
    THREADS_ACROSS,

    /**
     * When a <code>Scene</code> is being rendered, the screen is split into segments vertically and horizontally. Each segment is rendered by a different thread.
     * <code>THREADS_UP</code> controls the number of vertical divisions.
     * <br>
     * Default value is 3.
     */
    THREADS_UP
}