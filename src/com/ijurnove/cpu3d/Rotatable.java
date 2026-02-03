package com.ijurnove.cpu3d;

/**
 * This interface allows objects in 3D space to be rotated.
 */
public interface Rotatable {
    /**
     * Rotates this object about a given point on a specified axis. 0 corresponds to the X axis, 1 corresponds to the Y axis, and 2 corresponds to the Z axis.
     * @param axis the axis to rotate around
     * @param theta the radians to rotate by
     * @param point the center point to rotate around
     */
    public void rotate(int axis, double theta, Point3d point);
}