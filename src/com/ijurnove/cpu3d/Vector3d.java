package com.ijurnove.cpu3d;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A <code>Vector3d</code> represents a direction in 3D space. <code>Vector3d</code>s do not have an origin, and only represent a direction.
 */
public class Vector3d {
    private double x;
    private double y;
    private double z;

    protected static final Vector3d NEUTRAL_VEC = new Vector3d(0, 0, 0);
    protected static final Vector3d UP = new Vector3d(0, 1, 0);

    /**
     * Constructs a new <code>Vector3d</code> with the given values.
     * @param x the X value
     * @param y the Y value
     * @param z the Z value
     */
    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected Vector3d(double[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    /**
     * Constructs a <code>Vector3d</code> between two <code>Point3d</code>s.
     * @param start the starting Point3d
     * @param end the ending Point3d
     */
    public Vector3d(Point3d start, Point3d end) {
        this.x = end.xReal() - start.xReal();
        this.y = end.yReal() - start.yReal();
        this.z = end.zReal() - start.zReal();
    }

    /**
     * Returns the X value.
     * @return the X value
     */
    public double x() { return x; }

    /**
     * Returns the Y value.
     * @return the Y value
     */
    public double y() { return y; }
    
    /**
     * Returns the Z value.
     * @return the Z value
     */
    public double z() { return z; }

    /**
     * Sets the X value.
     * @param x the value to set
     */
    public void setX(double x) { this.x = x; }

    /**
     * Sets the Y value.
     * @param y the value to set
     */
    public void setY(double y) { this.y = y; }
    
    /**
     * Sets the Z value.
     * @param z the value to set
     */
    public void setZ(double z) { this.z = z; }

    /**
     * Returns a copy of this <code>Vector3d</code>.
     * @return a copy of this Vector3d
     */
    public Vector3d copy() {
        return new Vector3d(this.x, this.y, this.z);
    }

    /**
     * Returns true if the X, Y, and Z values of a given <code>Vector3d</code> are equal to those of this <code>Vector3d</code>.
     * @param v the Vector3d to compare
     * @return true if both Vector3ds have the same X, Y, and Z values
     */
    public boolean isEqual(Vector3d v) {
        return v.x == this.x && v.y == this.y && v.z == this.z;
    }

    /**
     * Returns the magnitude.
     * @return magnitude
     */
    public double magnitude() {
        return Math.sqrt((x*x) + (y*y) + (z*z));
    }

    /**
     * Sets the length of this <code>Vector3d</code> to 1.
     */
    public void normalize() {
        double mag = this.magnitude();

        this.x /= mag;
        this.y /= mag;
        this.z /= mag;
    }

    /**
     * Rotates this <code>Vector3d</code>.
     * @param axis the axis to rotate around
     * @param theta the amount to rotate by
     */
    public void rotate(int axis, double theta) {
        Matrix rotMatrix;
        
        // x axis
        switch (axis) {
            case 0 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {1, 0, 0},
                            new double[] {0, cos(theta), -1 * sin(theta)},
                            new double[] {0, sin(theta), cos(theta)}
                        }
                );
            case 1 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {cos(theta), 0, sin(theta)},
                            new double[] {0, 1, 0},
                            new double[] {-1 * sin(theta), 0, cos(theta)}
                        }
                );
            case 2 -> rotMatrix = new Matrix(
                        new double[][] {
                            new double[] {cos(theta), -1 * sin(theta), 0},
                            new double[] {sin(theta), cos(theta), 0},
                            new double[] {0, 0, 1}
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

        Matrix finalMatrix = Matrix.multiply(rotMatrix, new Matrix(
            new double[][] {
                {x},
                {y},
                {z}
            }
        ));

        this.x = finalMatrix.getValue(0, 0);
        this.y = finalMatrix.getValue(0, 1);
        this.z = finalMatrix.getValue(0, 2);
    }

    /**
     * Returns a <code>Vector3d</code> perpendicular to two given <code>Vector3d</code>s.
     * @param v1 the first Vector3d
     * @param v2 the second Vector3d
     * @return a Vector3d perpendicular to v1 and v2
     */
    public static Vector3d crossProduct(Vector3d v1, Vector3d v2) {
        return new Vector3d(
            (v1.y * v2.z) - (v1.z * v2.y),
            (v1.z * v2.x) - (v1.x * v2.z),
            (v1.x * v2.y) - (v1.y * v2.x)
        );
    }

    /**
     * Returns a <code>Vector3d</code> perpendicular to this <code>Vector3d</code> and another.
     * @param v2 the second Vector3d
     * @return a Vector3d perpendicular to this and v2
     */
    public Vector3d cross(Vector3d v2) {
        return new Vector3d(
            (this.y * v2.z) - (this.z * v2.y),
            (this.z * v2.x) - (this.x * v2.z),
            (this.x * v2.y) - (this.y * v2.x)
        );
    }

    /**
     * Returns the dot product of two <code>Vector3d</code>s.
     * @param v1 the first Vector3d
     * @param v2 the second Vector3d
     * @return the dot product of v1 and v2
     */
    public static double dotProduct(Vector3d v1, Vector3d v2) {
        return (v1.x * v2.x) + (v1.y * v2.y) + (v1.z * v2.z);
    }

    /**
     * Returns the dot product of this <code>Vector3d</code> and another.
     * @param v2 the second Vector3d
     * @return the dot product of this and v2
     */
    public double dotProduct(Vector3d v2) {
        return (this.x * v2.x) + (this.y * v2.y) + (this.z * v2.z);
    }

    /**
     * Returns a new <code>Vector3d</code> with the values of a given <code>Vector3d</code> multiplied by a number.
     * @param vec the Vector3d to be altered
     * @param num the number to multiply by
     * @return a new Vector3d with the values of the given Vector3d multiplied by the given number
     */
    public static Vector3d multiplyByNum(Vector3d vec, double num) {
        return new Vector3d(vec.x * num, vec.y * num, vec.z * num);
    }

    /**
     * Subtracts one <code>Vector3d</code> from another.
     * @param v1 the first Vector3d
     * @param v2 the second Vector3d
     * @return v1 minus v2
     */
    public static Vector3d subtract(Vector3d v1, Vector3d v2) {
        return new Vector3d(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    /**
     * Adds one <code>Vector3d</code> to another.
     * @param v1 the first Vector3d
     * @param v2 the second Vector3d
     * @return v1 plus v2
     */
    public static Vector3d add(Vector3d v1, Vector3d v2) {
        return new Vector3d(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    protected void absVal() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
    }

    /**
     * Reverses the direction of this <code>Vector3d</code>.
     */
    public void invert() {
        this.x *= -1;
        this.y *= -1;
        this.z *= -1;
    }

    protected static Vector3d average(Vector3d[] vectors) {
        double xTotal = 0;
        double yTotal = 0;
        double zTotal = 0;
        double count = 0;

        for (Vector3d v : vectors) {
            xTotal += v.x;
            yTotal += v.y;
            zTotal += v.z;
            count++;
        }

        return new Vector3d(xTotal/count, yTotal/count, zTotal/count);
    }

    protected static Vector3d fromBary(Vector3d[] vectors, double[] bary) {
        double xTotal = 0;
        double yTotal = 0;
        double zTotal = 0;

        for (int i = 0; i < 3; i++) {
            xTotal += vectors[i].x * bary[i];
            yTotal += vectors[i].y * bary[i];
            zTotal += vectors[i].z * bary[i];
        }

        return new Vector3d(xTotal, yTotal, zTotal);
    }

    protected void swapXY() {
        double xTemp = this.x;
        this.x = this.y;
        this.y = xTemp;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
