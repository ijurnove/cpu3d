package com.ijurnove.cpu3d;

/**
 * This interface allows objects in 3D space to be translated. 
 */
public interface Translatable {
    /**
     * Moves this object along a given Vector3d.
     * @param vec the Vector3d to shift this object along
     */
    public default void translate(Vector3d vec) {
        translate(vec.x(), vec.y(), vec.z());
    }

    /**
     * Shifts this object's X, Y, and Z position by the given values.
     * @param x how far the object should be translated along the X axis
     * @param y how far the object should be translated along the Y axis
     * @param z how far the object should be translated along the Z axis
     */
    public void translate(double x, double y, double z);
}

