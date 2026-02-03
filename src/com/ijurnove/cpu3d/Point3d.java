package com.ijurnove.cpu3d;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;

/**
 * A <code>Point3d</code> is a specific point in 3D space. <code>Point3d</code> objects store two different positions as <code>Matrix</code>es,
 * one for its position in 3D space, and one for its position on the screen.
 */
public class Point3d implements Translatable, Rotatable, Scalable {
    /**
     * A <code>Point3d</code> at (0, 0, 0)
     */
    public static final Point3d ORIGIN = new Point3d(0, 0, 0);

    private Matrix pos;
    private final Matrix renderCoords;

    private final ArrayList<Triangle> adjacentTris = new ArrayList<>();

    /**
     * Constructs a <code>Point3d</code> at the coordinates specified in a given <code>Matrix</code>. The <code>Matrix</code> should have 1 column and 3 or 4 rows. Row 1 is the 
     * X position, row 2 is the Y, and row 3 is the Z.
     * @param pos the Matrix representing the constructed Point3d's coordinates
     */
    public Point3d(Matrix pos) {
        if (!(pos.rowCount() == 4 || pos.rowCount() == 3) && pos.columnCount() != 1) {
            throw new ArrayIndexOutOfBoundsException("Wrong matrix dimensions for Point3d!");
        }
        this.pos = pos;

        this.renderCoords = new Matrix(new double[][] {
            {pos.getValue(0, 0)},
            {pos.getValue(0, 1)},
            {pos.getValue(0, 2)},
            {1}
        });
    }

    /**
     * Constructs a new <code>Point3d</code> at the given X, Y, and Z. 
     * @param x the X position of the constructed Point3d
     * @param y the Y position of the constructed Point3d
     * @param z the Z position of the constructed Point3d
     */
    public Point3d(double x, double y, double z) {
        this(new Matrix(new double[][] {
            {x},
            {y},
            {z},
            {1}
        })
        );
    }

    protected void addAdjTri(Triangle tri) { this.adjacentTris.add(tri); }
    protected ArrayList<Triangle> getAdjacentTris() { return this.adjacentTris; }

    protected Matrix getMatrix() { return this.pos; }
    protected Matrix getRenderCoords() { return this.renderCoords; }

    protected double[][] renderArray() { return this.renderCoords.getValues(); }

    /**
     * Returns the X position on the screen. 
     * @return the X position on the screen
     */
    public double xRend() { return this.renderCoords.getValue(0, 0); }

    /**
     * Returns the Y position on the screen. 
     * @return the Y position on the screen
     */
    public double yRend() { return this.renderCoords.getValue(0, 1); }

    /**
     * Returns the depth of this <code>Point3d</code>, or how far away it is from the camera.
     * @return the depth
     */
    public double depth() { return this.renderCoords.getValue(0, 2); }

    /**
     * Returns the X position in 3D space. 
     * @return the X position in 3D space
     */
    public double xReal() { return this.pos.getValue(0, 0); }
    
    /**
     * Returns the Y position in 3D space. 
     * @return the Y position in 3D space
     */
    public double yReal() { return this.pos.getValue(0, 1); }
    
    /**
     * Returns the Z position in 3D space. 
     * @return the Z position in 3D space
     */
    public double zReal() { return this.pos.getValue(0, 2); }

    /**
     * Returns a copy of this Point3d with a new reference.
     * @return a copy of this Point3d
     */
    public Point3d copy() {
        return new Point3d(this.xReal(), this.yReal(), this.zReal());
    }

    protected void shiftValue(int axis, double distance) {
        pos.setValue(0, axis, pos.getValue(0, axis) + distance);
    }
    
    private void setValue(int axis, double newPos) {
        pos.setValue(0, axis, newPos);
    }

    /**
     * Sets the X position. 
     * @param newPos the new X position
     */
    public void setX(double newPos) { setValue(0, newPos); }

    /**
     * Sets the Y position.
     * @param newPos the new Y position
     */
    public void setY(double newPos) { setValue(1, newPos); }
    
    /**
     * Sets the Z position. 
     * @param newPos the new Z position
     */
    public void setZ(double newPos) { setValue(2, newPos); }

    @Override
    public void translate(double xShift, double yShift, double zShift) {
        shiftValue(0, xShift);
        shiftValue(1, yShift);
        shiftValue(2, zShift);
    }

    @Override
    public void scale(double size, Point3d point) {
        setX(size * (this.xReal() - point.xReal()) + point.xReal());
        setY(size * (this.yReal() - point.yReal()) + point.yReal());
        setZ(size * (this.zReal() - point.zReal()) + point.zReal());
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        translate(-1 * point.xReal(), -1 * point.yReal(), -1 * point.zReal());
        rotate(axis, theta);
        translate(point.xReal(), point.yReal(), point.zReal());
    }

    // goes around origin
    protected void rotate(int axis, double theta) {
        Matrix rotMatrix;
        
        // x axis
        switch (axis) {
            case 0 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {1, 0, 0, 0},
                            new double[] {0, cos(theta), -1 * sin(theta), 0},
                            new double[] {0, sin(theta), cos(theta), 0},
                            new double[] {0, 0, 0, 0}
                        }
                );
            case 1 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {cos(theta), 0, sin(theta), 0},
                            new double[] {0, 1, 0, 0},
                            new double[] {-1 * sin(theta), 0, cos(theta), 0},
                            new double[] {0, 0, 0, 0}
                        }
                );
            case 2 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {cos(theta), -1 * sin(theta), 0, 0},
                            new double[] {sin(theta), cos(theta), 0, 0},
                            new double[] {0, 0, 1, 0},
                            new double[] {0, 0, 0, 0}
                        }
                );
            default -> {
                rotMatrix = new Matrix(
                    new double[][] {
                        {}
                    }
                );
            }
        }

        pos.setValues(Matrix.multiply(rotMatrix, this.pos));
        pos.setValue(0, 3, 1);
    }

    // updates renderCoords to relative position to cam - call 1st
    private void updRelativePos(Camera cam) {
        this.renderCoords.setValues(
            // multiplies: posOffsetMatrix * horRotMatrix * vertRotMatrix * AXIS_CONV_MATRIX * point.getMatrix()
            Matrix.multiply(cam.getVertRotMatrix(), Matrix.multiply(
            cam.getHorRotMatrix(), Matrix.multiply(
            Matrix.AXIS_CONV_MATRIX, Matrix.multiply(
            cam.getPosOffsetMatrix(), this.getMatrix()
            )))));


        double hold = this.renderCoords.getValue(0, 0);
        this.renderCoords.setValue(0, 0, renderCoords.getValue(0, 1));
        this.renderCoords.setValue(0, 1, hold);
    }

    private void updRelativePosUsingLookAtWithLineOfSight(Camera cam) {
        double x = this.xReal();
        this.setX(this.yReal());
        this.setY(x);

        this.renderCoords.setValues(
            Matrix.multiply(
                cam.lookAtWithLineOfSight(),
                this.pos
            )
        );

        double hold = this.renderCoords.getValue(0, 0);
        this.renderCoords.setValue(0, 0, renderCoords.getValue(0, 1));
        this.renderCoords.setValue(0, 1, hold);

        double x2 = this.xReal();
        this.setX(this.yReal());
        this.setY(x2);
    }

    protected void perspProjection(Camera cam) {
        updRelativePos(cam);

        renderCoords.perspRenderUpd(cam);
    }

    protected void perspProjectionForPointShadowMap(Camera cam) {
        updRelativePosUsingLookAtWithLineOfSight(cam);
        
        renderCoords.perspRenderUpdForPointShadowMap(cam);
    }

    protected void orthoProjection(Camera cam) {
        renderCoords.setValues(
            Matrix.multiply(
                Matrix.multiply(cam.getOrthoMatrix(), cam.lookAtMatrixToOrigin()),
                this.getMatrix())
        );

        renderCoords.orthoRenderUpd(cam);
    }
    
}
