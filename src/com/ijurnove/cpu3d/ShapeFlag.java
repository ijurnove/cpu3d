package com.ijurnove.cpu3d;

/**
 * <code>ShapeFlag</code>s are used to toggle specific features in a <code>Shape3d</code>.
 */
public enum ShapeFlag {
    /**
     * Controls whether or not the <code>Shape3d</code> recieves lighting. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 1.
     */
    RECIEVE_LIGHTING,

    /**
     * Controls whether or not the <code>Shape3d</code> casts a shadow. A value of 0 turns it off, and 1 turns it on.
     * <br>
     * Default value of 1.
     */
    CAST_SHADOW,

    /**
     * Controls whether or not the <code>Shape3d</code> is visible. A value of 0 hides it, and 1 shows it.
     * <br>
     * Default value of 1.
     */
    VISIBLE
}
