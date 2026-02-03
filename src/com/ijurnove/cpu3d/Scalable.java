package com.ijurnove.cpu3d;

/**
 * This interface allows objects in 3D space to be scaled. 
 */
public interface Scalable {
    /**
     * Scales this object by a given amount from a point. 
     * @param size the scale multiplier
     * @param point the point that this object should be scaled from
     */
    public void scale(double size, Point3d point);
}