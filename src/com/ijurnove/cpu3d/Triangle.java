package com.ijurnove.cpu3d;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

/**
 * A <code>Triangle</code> holds three <code>Point3d</code>s, three point normals, three <code>UV</code> coordinates, and one surface normal. Point normals define the direction
 * each vertex is facing. The surface normal defines the direction the entire <code>Triangle</code> is facing. <code>UV</code> coordinates define the texture coordinates at
 * each vertex.
 * <p>
 * Texture and <code>Material</code> data is taken from the parent <code>Shape3d</code>.
 */
public class Triangle implements Comparable<Triangle>, Translatable, Rotatable, Scalable {
    private final Point3d[] vertices;
    private Vector3d[] pointNormals = new Vector3d[3];
    private UV[] textureCoords = new UV[3];
    private Shape3d parent;
    
    private Vector3d surfNormal;
    
    private Point3d center;
    private double centerZ = 0;

    private boolean calcPointNorms = false;
    
    private boolean shown = false;

    // cached values for barycentric coordinates
    private double baryDenominator = 1;

    // double weight1Numerator = ((v2.yRend() - v3.yRend()) * (pX - v3.xRend())) + ((v3.xRend() - v2.xRend()) * (pY - v3.yRend()));
    private double v2y_minus_v3y = 1;
    private double v3x_minus_v2x = 1;

    // double weight2Numerator ((v3.yRend() - v1.yRend()) * (pX - v3.xRend())) + ((v1.xRend() - v3.xRend()) * (pY - v3.yRend()));
    private double v3y_minus_v1y = 1;
    private double v1x_minus_v3x = 1;

    /**
     * Constructs a new <code>Triangle</code> with the given vertices and point normals.
     * @param v1 the first vertex
     * @param v2 the second vertex
     * @param v3 the third vertex
     * @param pn1 the first point normal
     * @param pn2 the second point normal
     * @param pn3 the third point normal
     */
    public Triangle(Point3d v1, Point3d v2, Point3d v3, Vector3d pn1, Vector3d pn2, Vector3d pn3) {
        this.vertices = new Point3d[] {v1, v2, v3};
        this.pointNormals = new Vector3d[] {pn1, pn2, pn3};

        this.initialize();
    }
    
    protected Triangle(Point3d v1, Point3d v2, Point3d v3) {
        this.vertices = new Point3d[] {v1, v2, v3};
        
        this.initialize();
    }

    protected Triangle(Point3d[] vertices) {
        this.vertices = vertices;
        
        this.initialize();
    }

    private void initialize() {
        updSurfNorm();
        updCenter();

        for (Point3d p : this.vertices) {
            p.addAdjTri(this);
        }
    }

    protected void setParent(Shape3d parent) {
        this.parent = parent;
    }

    /**
     * Returns the parent <code>Shape3d</code> of this <code>Triangle</code>.
     * @return the parent Shape3d of this Triangle
     */
    public Shape3d getParent() {
        return this.parent;
    }

    /**
     * Returns a copy of this <code>Triangle</code>.
     * @return an identical Triangle
     */
    public Triangle copy() {
        Point3d[] copyVertices = new Point3d[vertices.length];
        for (int i = 0; i < copyVertices.length; i++) {
            copyVertices[i] = vertices[i].copy();
        }

        Vector3d[] copyPointNorms = new Vector3d[pointNormals.length];
        for (int i = 0; i < copyPointNorms.length; i++) {
            copyPointNorms[i] = pointNormals[i].copy();
        }

        UV[] copyTextureCoords = new UV[textureCoords.length];
        for (int i = 0; i < copyTextureCoords.length; i++) {
            copyTextureCoords[i] = textureCoords[i].copy();
        }
        
        Triangle copy = new Triangle(copyVertices);
        copy.pointNormals = copyPointNorms;
        copy.textureCoords = copyTextureCoords;
        // not setting a parent might cause issues later,
        // however this doesn't matter in Shape3d.copy() (which is the only place this is used),
        // since it sets the parents for each triangle in the Shape3d
        // constructor
        copy.surfNormal = surfNormal.copy();

        return copy;
    }

    protected void maybeCalculatePointNormals() {
        if (this.calcPointNorms) {
            for (int i = 0; i < 3; i++) {
                pointNormals[i] = Vector3d.average(
                    vertices[i].getAdjacentTris().stream()
                    .map(t -> (Vector3d) t.getSurfNorm())
                    .toArray(Vector3d[]::new)
                );
            }
        }
    }

    /**
     * Returns the direction the <code>Triangle</code> is facing at each vertex.
     * @return the point normals
     */
    public Vector3d[] getPointNormals() { return this.pointNormals; }

    /**
     * Returns the vertices.
     * @return the vertices
     */
    public Point3d[] getVertices() { return this.vertices; }

    /**
     * Returns the UV coordinates at each vertex.
     * @return the UV coordinates of each vertex
     */
    public UV[] getTextureCoords() { return this.textureCoords; }

    /**
     * Returns the first vertex. 
     * @return the first vertex
     */
    public Point3d vrtx1() { return this.vertices[0]; }

    /**
     * Returns the second vertex. 
     * @return the second vertex
     */
    public Point3d vrtx2() { return this.vertices[1]; }

    /**
     * Returns the third vertex. 
     * @return the third vertex
     */
    public Point3d vrtx3() { return this.vertices[2]; }

    /**
     * Returns the parent <code>Shape3d</code>'s texture.
     * @return the texture of the parent Shape3d
     */
    public BufferedImage getTexture() { return this.parent.getTexture(); }

    /**
     * Returns the parent <code>Shape3d</code>'s <code>Material</code>.
     * @return the Material of the parent Shape3d
     */
    public Material getMaterial() { return this.parent.getMaterial(); }

    /**
     * Returns the center of this <code>Triangle</code>.
     * @return the center
     */
    public Point3d getCenter() { return this.center; }

    /**
     * Returns the direction the <code>Triangle</code> is facing.
     * @return the direction the Triangle is facing
     */
    public Vector3d getSurfNorm() { return this.surfNormal; }

    protected boolean isShown() { return this.shown; }
    protected void setShown(boolean shown) { this.shown = shown; }

    protected void flip() {
        for (Vector3d v : pointNormals) {
            v.invert();
        }

        surfNormal.invert();
        
        // TODO: Make this retain original UV values
        // UV uv0 = vertices[0].getUV();
        // UV uv2 = vertices[2].getUV();
        
        Point3d p = vertices[0];
        vertices[0] = vertices[2];
        vertices[2] = p;
        
        // vertices[0].setUV(uv0);
        // vertices[2].setUV(uv2);

        Vector3d v = pointNormals[0];
        pointNormals[0] = pointNormals[2];
        pointNormals[2] = v;
    }

    protected Point3d point3dFromBary(double[] weights) {
        Point3d v1 = vrtx1();
        Point3d v2 = vrtx2();
        Point3d v3 = vrtx3();

        double w1 = weights[0];
        double w2 = weights[1];
        double w3 = weights[2];

        double finalY = (v1.xReal() * w1) + (v2.xReal() * w2) + (v3.xReal() * w3);
        double finalX = (v1.yReal() * w1) + (v2.yReal() * w2) + (v3.yReal() * w3);
        double finalZ = (v1.zReal() * w1) + (v2.zReal() * w2) + (v3.zReal() * w3);

        return new Point3d(finalX, finalY, finalZ);
    }

    protected void markForPointNormalCalculation() {
        this.calcPointNorms = true;
    }

    /**
     * Sets the point normals at each vertex.
     * @param v1Norm the first point normal to set
     * @param v2Norm the second point normal to set
     * @param v3Norm the third point normal to set
     */
    public void setPointNormals(Vector3d v1Norm, Vector3d v2Norm, Vector3d v3Norm) {
        this.pointNormals = new Vector3d[] {v1Norm, v2Norm, v3Norm};
    }

    /**
     * Sets the UV coordinates at each vertex.
     * @param v1coords the first UV to set
     * @param v2coords the second UV to set
     * @param v3coords the third UV to set
     */
    public void setTextureCoords(UV v1coords, UV v2coords, UV v3coords) {
        this.textureCoords = new UV[] {v1coords, v2coords, v3coords};
    }

    private void updSurfNorm() {
        this.surfNormal = Vector3d.crossProduct(new Vector3d(vrtx1(), vrtx2()), 
            new Vector3d(vrtx1(), vrtx3())
            );

        this.surfNormal.normalize();
    }

    private void updCenter() {
        double xTotal = 0;
        double yTotal = 0;
        double zTotal = 0;

        for (Point3d p : this.vertices) {
            xTotal += p.xReal();
            yTotal += p.yReal();
            zTotal += p.zReal();
        }

        this.center = new Point3d(xTotal / 3, yTotal / 3, zTotal / 3);
        // this.center.updRenderCoords(Renderer3d.userCamera);
    }

    private void updCenterZ() {
        this.centerZ = (vrtx1().depth() + vrtx2().depth() + vrtx3().depth()) / 3;
    }

    protected void updateValues() {
        this.updCenterZ();
        this.updCenter();
        this.updateBaryCache();
        // this.updSurfNorm();
    }

    protected boolean isFacingViewer() {
        double firstPart = (vrtx2().xRend() - vrtx1().xRend()) * (vrtx3().yRend() - vrtx1().yRend());
        double secondPart = (vrtx3().xRend() - vrtx1().xRend()) * (vrtx2().yRend() - vrtx1().yRend());

        return firstPart < secondPart;
    }

    // used for shiftOnX/Y/Z() and translate()
    private void shiftPoints(int axis, double distance) {
        for (Point3d p : this.vertices) {
            p.shiftValue(axis, distance);
        }
    }
    
    protected void shiftOnX(double distance) { shiftPoints(0, distance); }
    protected void shiftOnY(double distance) { shiftPoints(1, distance); }
    protected void shiftOnZ(double distance) { shiftPoints(2, distance); }
    
    @Override
    public void translate(double x, double y, double z) {
        for (Point3d p : vertices) {
            p.translate(x, y, z);
        }
    }

    @Override
    public int compareTo(Triangle o) {
        return Double.compare(o.centerZ, this.centerZ);
    }

    protected void updateBaryCache() {
        Point3d v1 = vrtx1();
        Point3d v2 = vrtx2();
        Point3d v3 = vrtx3();

        this.v2y_minus_v3y = (v2.yRend() - v3.yRend());
        this.v3x_minus_v2x = (v3.xRend() - v2.xRend());

        this.v3y_minus_v1y = (v3.yRend() - v1.yRend());
        this.v1x_minus_v3x = (v1.xRend() - v3.xRend());

        this.baryDenominator = (v2y_minus_v3y * v1x_minus_v3x) + (v3x_minus_v2x * (v1.yRend() - v3.yRend()));
    }

    protected double[] barycentricCoords(double pX, double pY) {
        // Point3d v1 = vrtx1();
        // Point3d v2 = vrtx2();
        Point3d v3 = vrtx3();

        double pX_minus_v3x = pX - v3.xRend();
        double pY_minus_v3y = pY - v3.yRend();

        // double weight1Numerator = ((v2.yRend() - v3.yRend()) * (pX - v3.xRend())) + ((v3.xRend() - v2.xRend()) * (pY - v3.yRend()));
        double weight1Numerator = (v2y_minus_v3y * pX_minus_v3x) + (v3x_minus_v2x * pY_minus_v3y);

        // double weight2Numerator = ((v3.yRend() - v1.yRend()) * (pX - v3.xRend())) + ((v1.xRend() - v3.xRend()) * (pY - v3.yRend()));
        double weight2Numerator = (v3y_minus_v1y * pX_minus_v3x) + (v1x_minus_v3x * pY_minus_v3y);

        double[] weights = new double[3];

        weights[0] = weight1Numerator / baryDenominator;
        weights[1] = weight2Numerator / baryDenominator;
        weights[2] = 1 - weights[0] - weights[1];

        return weights;
    }

    protected boolean overlapsWithRect(int minX, int minY, int maxX, int maxY) {
        /* Future optimzation: Before anything else, calculate the distance from the center of the triangle
        to its furthest vertex and the distance from the center of the rectangle to one of its corners. Then,
        if the distance between the center of each shape is greater than the sum of these two distances, the
        shapes do not overlap. */

        double[] rectCenter = new double[] {
            minX + ((maxX-minX)/2), 
            minY + ((maxY-minY)/2)
        };
        
        double rectCenterDist = Util.distance2D(
            maxX, maxY,
            rectCenter[0], rectCenter[1]   
        );

        double triCenterDist = 0;
        for (Point3d v : vertices) {
            double dist = Util.distance2D(
                center.xRend(), center.yRend(),
                v.xRend(), v.yRend()
            );

            if (dist > triCenterDist) {
                triCenterDist = dist;
            }
        }

        if (rectCenterDist + triCenterDist < Util.distance2D(
            rectCenter[0], rectCenter[1],
            this.center.xRend(), this.center.yRend()
        )) {
            return false;
        }

        // checking if triangle vertices are contained within the rectangle
        for (Point3d v : vertices) {
            if (v.xRend() >= minX &&
                v.xRend() <= maxX &&
                v.yRend() >= minY && 
                v.yRend() <= maxY) {
                return true;
            }
        }

        int[][] rectPoints = new int[][] {
            {minX, minY},
            {minX, maxY},
            {maxX, maxY},
            {maxX, minY}
        };
        
        // checking if rectangle points are contained within the triangle
        for (int[] rectCorner : rectPoints) {
            double[] bary = barycentricCoords(rectCorner[0], rectCorner[1]);
            
            if (bary[0] >= 0 && bary[1] >= 0 && bary[2] >= 0) {
                return true;
            }
        }

        // checking if any of the sides overlap
        for (int triVrtxIndex = 0; triVrtxIndex < 3; triVrtxIndex++) {
            Line2D triangleSide = new Line2D.Double(
                vertices[triVrtxIndex].xRend(), vertices[triVrtxIndex].yRend(), 
                vertices[(triVrtxIndex+1) % 3].xRend(), vertices[(triVrtxIndex+1) % 3].yRend()
            );

            for (int rectVrtxIndex = 0; rectVrtxIndex < 4; rectVrtxIndex++) {
                if (triangleSide.intersectsLine(
                    rectPoints[rectVrtxIndex][0], rectPoints[rectVrtxIndex][1],
                    rectPoints[(rectVrtxIndex+1) % 3][0], rectPoints[(rectVrtxIndex+1) % 3][1]
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void perspProjectionForPointShadowMap(Camera c) {
        for (Point3d p : vertices) {
            p.perspProjectionForPointShadowMap(c);
        }
    }

    protected void perspProjection(Camera c) {
        for (Point3d p : vertices) {
            p.perspProjection(c);
        }
    }

    protected void orthoProjection(Camera c) {
        for (Point3d p : vertices) {
            p.orthoProjection(c);
        }
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        for (Point3d p : vertices) {
            p.rotate(axis, theta, point);
        }
    }

    @Override
    public void scale(double size, Point3d point) {
        for (Point3d p : vertices) {
            p.scale(size, point);
        }
    }
}
