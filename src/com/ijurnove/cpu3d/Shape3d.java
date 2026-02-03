package com.ijurnove.cpu3d;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>Shape3d</code>s represent a three-dimensional shape with a texture and a material. 
 */
public class Shape3d implements Translatable, Rotatable, Scalable {
    private final Triangle[] triangles;
    private BufferedImage texture;
    private Material material;
    private Scene parent;

    private final EnumMap<ShapeFlag, Integer> shapeFlags = new EnumMap<>(ShapeFlag.class);
    
    /**
     * Constructs a <code>Shape3d</code> with a specified <code>Mesh</code>, <code>Material</code>, and a texture represented by a <code>BufferedImage</code>.
     * @param mesh the specified Mesh
     * @param texture the specified texture
     * @param material the specified Material
     */
    public Shape3d(Mesh mesh, BufferedImage texture, Material material) {
        for (Triangle tri : mesh.getTriangles()) {
            tri.setParent(this);
        }

        this.triangles = mesh.getTriangles();
        this.texture = texture;
        this.material = material;

        initShapeFlags();
    }

    /**
     * Constructs a <code>Shape3d</code> with the given <code>Triangle[]</code>, without a texture or <code>Material</code>.
     */
    public Shape3d(Triangle[] triangles) {
        this.triangles = triangles;

        for (Triangle t : this.triangles) {
            t.setParent(this);
        }

        initShapeFlags();
    }

    private void initShapeFlags() {
        this.shapeFlags.put(ShapeFlag.RECIEVE_LIGHTING, 1);
        this.shapeFlags.put(ShapeFlag.CAST_SHADOW, 1);
        this.shapeFlags.put(ShapeFlag.VISIBLE, 1);
    }

    /**
     * Returns the value of a specified <code>ShapeFlag</code>.
     * <br>
     * Refer to <code>ShapeFlag</code> to see what each one does.
     * @param flag the specified ShapeFlag
     * @return the value of the ShapeFlag
     */
    public int getShapeFlag(ShapeFlag flag) {
        return shapeFlags.get(flag);
    }

    /**
     * Sets the value of a specified <code>ShapeFlag</code> to a given value.
     * <br>
     * Refer to <code>ShapeFlag</code> to see what each one does.
     * @param flag the specified ShapeFlag
     * @param value the new value
     */
    public void setShapeFlag(ShapeFlag flag, int value) {
        shapeFlags.replace(flag, value);
    }

    /**
     * Returns a new <code>Shape3d</code> that is a copy of this one with new references.
     * @return an identical Shape3d
     */
    public Shape3d copy() {
        Triangle[] copyTriangles = new Triangle[triangles.length];
        for (int i = 0; i < copyTriangles.length; i++) {
            copyTriangles[i] = triangles[i].copy();
        }

        return new Shape3d(new Mesh(copyTriangles), texture, material);
    }

    /**
     * Returns this <code>Shape3d</code>'s <code>Triangle</code> array.
     * @return the Triangle[] of this Shape3d
     */
    public Triangle[] getTriangles() { return this.triangles; }
    
    /**
     * Returns the texture of this <code>Shape3d</code> as a <code>BufferedImage</code>.
     * @return the texture of this Shape3d
     */
    public BufferedImage getTexture() { return this.texture; }

    /**
     * Returns the <code>Material</code> of this <code>Shape3d</code>.
     * @return the Material of this Shape3d
     */
    public Material getMaterial() { return this.material; }
    
    /**
     * Sets the parent of this <code>Shape3d</code> to a given <code>Scene</code>.
     * @param parent the specified parent Scene
     */
    public void setParent(Scene parent) { this.parent = parent; }

    /**
     * Sets the texture of this <code>Shape3d</code> to a given <code>BufferedImage</code>.
     * @param texture the specified BufferedImage
     */
    public void setTexture(BufferedImage texture) { this.texture = texture; }
    
    /**
     * Sets the <code>Material</code> of this <code>Shape3d</code>.
     * @param material the Material to set
     */
    public void setMaterial(Material material) { this.material = material; }

    /**
     * Inverts all surface and point normals of this <code>Shape3d</code>, essentially flipping it inside out.
     */
    public void flipNormals() {
        for (Triangle t : triangles) {
            t.flip();
        }
    }

    @Override
    public void scale(double scale, Point3d point) {
        for (Point3d p : this.uniqueVertices()) {
            p.scale(scale, point);
        }
    }

    protected void rotate(int axis, double theta) {
        for (Point3d p : this.uniqueVertices()) {
            p.rotate(axis, theta);
        }

        for (Vector3d v : this.uniquePointNormals()) {
            v.rotate(axis, theta);
        }

        for (Triangle t : this.getTriangles()) {
            t.getSurfNorm().rotate(axis, theta);
        }
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        for (Point3d p : this.uniqueVertices()) {
            p.rotate(axis, theta, point);
        }

        for (Vector3d v : this.uniquePointNormals()) {
            v.rotate(axis, theta);
        }

        for (Triangle t : this.getTriangles()) {
            t.getSurfNorm().rotate(axis, theta);
        }
    }
    
    private void shiftPoints(int axis, double distance) {
        for (Point3d p : this.uniqueVertices()) {
            p.shiftValue(axis, distance);
        }
    }
    
    private void shiftOnX(double distance) { shiftPoints(0, distance); }
    private void shiftOnY(double distance) { shiftPoints(1, distance); }
    private void shiftOnZ(double distance) { shiftPoints(2, distance); }
    
    @Override
    public void translate(double x, double y, double z) {
        shiftOnX(x);
        shiftOnY(y);
        shiftOnZ(z);
    }
    
    /**
     * Returns a <code>Point3d</code> array consisting of every unique vertex in this <code>Shape3d</code>.
     * @return a list of every unique vertex
     */
    public Point3d[] uniqueVertices() {
        Set<Point3d> points = new HashSet<>();
        for (Triangle t : this.triangles) {
            points.addAll(Arrays.asList(t.getVertices()));
        }
        
        Point3d[] pointArray = new Point3d[points.size()];

        int i = 0;
        for (Point3d p : points) {
            pointArray[i] = p;
            i++;
        }

        return pointArray;
        
    }

    /**
     * Returns a <code>Vector3d</code> array consisting of every unique point normal in this <code>Shape3d</code>.
     * @return a list of every unique point normal
     */
    public Vector3d[] uniquePointNormals() {
        Set<Vector3d> norms = new HashSet<>();
        for (Triangle t : this.triangles) {
            norms.addAll(Arrays.asList(t.getPointNormals()));
        }
        
        Vector3d[] normArray = new Vector3d[norms.size()];

        int i = 0;
        for (Vector3d v : norms) {
            normArray[i] = v;
            i++;
        }

        return normArray;
        
    }

    protected void perspProjectionForPointShadowMap(Camera c) {
        for (Triangle t : this.triangles) {
            t.perspProjectionForPointShadowMap(c);

            t.updateValues();
            t.getCenter().perspProjectionForPointShadowMap(c);
        }
    }

    protected void perspProjection(Camera c) {
        for (Triangle t : this.triangles) {
            t.perspProjection(c);

            t.updateValues();
            t.getCenter().perspProjection(c);
        }
    }

    protected void orthoProjection(Camera c) {
        for (Triangle t : this.triangles) {
            t.orthoProjection(c);
            
            t.updateValues();
            t.getCenter().orthoProjection(c);
        }
    }
    
    /**
     * Calculates and returns the center of this <code>Shape3d</code> as a <code>Point3d</code>.
     * @return the center of this Shape3d
     */
    public Point3d center() {
        double xTotal = 0;
        double yTotal = 0;
        double zTotal = 0;
        
        Point3d[] vertices = this.uniqueVertices();
        
        for (Point3d p : vertices) {
            xTotal += p.xReal();
            yTotal += p.yReal();
            zTotal += p.zReal();
            }
            
        xTotal /= vertices.length;
        yTotal /= vertices.length;
        zTotal /= vertices.length;

        return new Point3d(xTotal, yTotal, zTotal);
    }
}
