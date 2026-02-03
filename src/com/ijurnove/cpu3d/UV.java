package com.ijurnove.cpu3d;

/**
 * <code>UV</code> coordinates represent a point on a texture. Each coordinate ranges from 0-1: (0,0) would be the top left of a texture, and (1,1) would be the bottom right.
 */
public class UV {
    private final double u;
    private final double v;

    /**
     * Constructs a new <code>UV</code> with the given values.
     * @param u the U coordinate
     * @param v the V coordinate
     */
    public UV(double u, double v) {
        this.u = u;
        this.v = v;
    }

    /**
     * Returns a copy of this <code>UV</code>.
     * @return a copy of this UV
     */
    public UV copy() {
        return new UV(u, v);
    }

    /**
     * Returns the U coordinate.
     * @return the U coordinate
     */
    public double u() { return u; }

    /**
     * Returns the V coordinate.
     * @return the V coordinate
     */
    public double v() { return v; }
}
