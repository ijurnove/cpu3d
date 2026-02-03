package com.ijurnove.cpu3d;

/**
 * A <code>Mesh</code> consists of a set of <code>Triangle</code>s. 
 */
public class Mesh {
    private final Triangle[] triangles;

    /**
     * Constructs a <code>Mesh</code> with a given set of <code>Triangle</code>s.
     * @param triangles a set of Triangles
     */
    public Mesh(Triangle[] triangles) {
        this.triangles = triangles;
    }

    /**
     * Returns the <code>Triangle</code>s.
     * @return this Mesh's Triangles
     */
    public Triangle[] getTriangles() { return this.triangles; }

    /**
     * Copies this <code>Mesh</code> and all the <code>Triangle</code>s within it.
     * @return a copy of this Mesh
     */
    public Mesh copy() {
        Triangle[] copiedTriangles = new Triangle[triangles.length];

        for (int i = 0; i < copiedTriangles.length; i++) {
            copiedTriangles[i] = triangles[i].copy();
        }

        return new Mesh(copiedTriangles);
    }
}
