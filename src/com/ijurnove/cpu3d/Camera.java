package com.ijurnove.cpu3d;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
/**
 * <code>Camera</code> objects define a viewpoint from which a <code>Scene</code> can be rendered. 
 */
public class Camera implements Translatable, Rotatable, Scalable {
    private Point3d pos;
    private Scene parent;
    private double phi; // vertical angle (0-180 degrees, 0 = up, 180 = down)
    private double theta; // horizontal angle (0-360 degrees)
    private double fov;
    
    private Vector3d lineOfSight;
    
    // matrix used for vertical rotation during rendering
    private final Matrix vertRotMatrix = new Matrix(new double[][] {
        {-1, 0, -1, 0}, // cos(-phi), 0, -sin(-phi), 0
        {0, 1, 0, 0}, // 0, 1, 0, 0
        {-1, 0, -1, 0}, // sin(-phi), 0, cos(-phi), 0
        {0, 0, 0, 0} // 0, 0, 0, 0
    });

    // matrix used for hortizontal rotation during rendering
    private final Matrix horRotMatrix = new Matrix(new double[][] {
        {-1, -1, 0, 0}, // cos(-theta), -sin(-theta), 0, 0
        {-1, -1, 0, 0}, // sin(-theta), cos(-theta), 0, 0
        {0, 0, 1, 0}, // 0, 0, 1, 0
        {0, 0, 0, 0} // 0, 0, 0, 0
    });

    // matrix used for position offset during rendering
    private final Matrix posOffsetMatrix = new Matrix(new double[][] {
        {1, 0, 0, -1}, // 1, 0, 0, camX
        {0, 1, 0, -1}, // 0, 1, 0, camY
        {0, 0, 1, -1}, // 0, 0, 1, camZ
        {0, 0, 0, 0} // 0, 0, 0, 0
    });

    // t/b = top/bottom
    // r/l = right/left
    // f/n = front/back

    private final Matrix orthographicMatrix = new Matrix(new double[][] {
        {1, 0, 0, 1},
        {0, 1, 0, 1},
        {0, 0, 1, 1},
        {0, 0, 0, 1}
    });
    // private final Matrix orthoMatrix = new Matrix(new double[][] {
    // {2/(r-l), 0, 0, -((r+l)/(r-l))},
    // {0, 2/(t-b), 0, -((t+b)/t-b))},
    // {0, 0, -2/(f-n), -((f+n)/(f-n))},
    // {0, 0, 0, 1}
    //})

    protected Camera(Scene parent, Point3d pos, double phi, double theta, double fov) {
        this.parent = parent;
        this.pos = pos;
        this.phi = phi;
        this.theta = theta;
        this.fov = fov;
    }

    /**
     * Constructs a <code>Camera</code> with specified pos, phi, theta, and FOV.
     * @param pos the starting position
     * @param phi vertical rotation, in radians
     * @param theta horizontal rotation, in radians
     * @param fov field of view
     */
    public Camera(Point3d pos, double phi, double theta, double fov) {
        this.pos = pos;
        this.phi = phi;
        this.theta = theta;
        this.fov = fov;
    }

    /**
     * Returns the position.
     * @return the position of this Camera
     */
    public Point3d getPos() { return this.pos; }

    /**
     * Returns the vertical rotation, in radians.
     * @return the vertical rotation of this Camera
     */
    public double getPhi() { return this.phi; }

    /**
     * Returns the horizontal rotation, in radians.
     * @return the horizontal rotation of this Camera
     */
    public double getTheta() { return this.theta; }

    /**
     * Returns the field of view.
     * @return the field of view of this Camera
     */
    public double getFov() { return this.fov; }

    /**
     * Returns the parent <code>Scene</code>.
     * @return the parent Scene of this Camera
     */
    public Scene getParent() { return this.parent; }

    protected Matrix getVertRotMatrix() { return this.vertRotMatrix; }
    protected Matrix getHorRotMatrix() { return this.horRotMatrix; }
    protected Matrix getPosOffsetMatrix() { return this.posOffsetMatrix; }
    protected Matrix getOrthoMatrix() { return this.orthographicMatrix; }

    protected Matrix getPosMatrix() { return this.pos.getMatrix(); }

    /**
     * Sets the vertical rotation. (In radians)
     * @param phi the vertical rotation to set, in radians
     */
    public void setPhi(double phi) { this.phi = phi; }

    /**
     * Sets the horizontal rotation. (In radians)
     * @param theta the horizontal rotation to set, in radians
     */
    public void setTheta(double theta) { this.theta = theta; }

    /**
     * Sets the parent <code>Scene</code>.
     * @param parent the scene to set
     */
    public void setParent(Scene parent) { this.parent = parent; }

    /**
     * Sets the field of view.
     * @param fov the field of view to set
     */
    public void setFOV(double fov) { this.fov = fov; }

    /**
     * Sets the position. 
     * @param pos the position to set
     */
    public void setPos(Point3d pos) { this.pos = pos; }

    /**
     * Returns the direction the <code>Camera</code> is looking as a <code>Vector3d</code>.
     * @return the direction the Camera is looking
     */
    public Vector3d lineOfSight() {
        updateLineOfSight();
        return this.lineOfSight;
    }

    protected void updateLineOfSight() {
        this.lineOfSight = new Vector3d(Util.sphereToCartesian(1, theta, phi));
    }

    protected void updateOrthoMatrix(double l, double r, double b, double t, double n, double f) {
        orthographicMatrix.setValue(0, 0, 2/(r-l));
        orthographicMatrix.setValue(1, 1, 2/(t-b));
        orthographicMatrix.setValue(2, 2, -2/(f-n));

        orthographicMatrix.setValue(3, 0, 
            -((r+l) / (r-l))
        );
        orthographicMatrix.setValue(3, 1, 
            -((t+b) / (t-b))
        );
        orthographicMatrix.setValue(3, 2, 
            -((f+n) / (f-n))
        );
    }

    private void updateVertRotMatrix() {
        this.vertRotMatrix.setValue(0, 0, cos(-1 * phi));
        this.vertRotMatrix.setValue(2, 0, -1 * sin(-1 * phi));
        this.vertRotMatrix.setValue(0, 2, sin(-1 * phi));
        this.vertRotMatrix.setValue(2, 2, cos(-1 * phi));
    }

    private void updateHorRotMatrix() {
        this.horRotMatrix.setValue(0, 0, cos(-1 * theta));
        this.horRotMatrix.setValue(1, 0, -1 * sin(-1 * theta));
        this.horRotMatrix.setValue(0, 1, sin(-1 * theta));
        this.horRotMatrix.setValue(1, 1, cos(-1 * theta));
    }

    private void updatePosOffsetMatrix() {
        this.posOffsetMatrix.setValue(3, 0, pos.getMatrix().getValue(0, 0));
        this.posOffsetMatrix.setValue(3, 1, pos.getMatrix().getValue(0, 1));
        this.posOffsetMatrix.setValue(3, 2, pos.getMatrix().getValue(0, 2));
    }

    private void updateMatrices() {
        this.updateVertRotMatrix();
        this.updateHorRotMatrix();
        this.updatePosOffsetMatrix();
    }

    protected void tick() {
        this.updateMatrices();
        this.updateLineOfSight();
    }

    protected Matrix lookAtWithLineOfSight() {
        updateLineOfSight();

        Vector3d fwdVec = this.lineOfSight.copy();
        fwdVec.normalize();
        fwdVec.invert();

        Vector3d rightVec = Vector3d.crossProduct(fwdVec, Vector3d.UP);
        rightVec.normalize();

        Vector3d upVec = Vector3d.crossProduct(fwdVec, rightVec);
        upVec.normalize();

        return Matrix.lookAt(rightVec, upVec, fwdVec, this.pos);
    }

    protected Matrix lookAtMatrixToOrigin() {
        updateLineOfSight();

        Vector3d fwdVec = new Vector3d(pos, Point3d.ORIGIN);
        fwdVec.normalize();
        fwdVec.invert();

        Vector3d rightVec = Vector3d.crossProduct(fwdVec, Vector3d.UP);
        rightVec.normalize();

        Vector3d upVec = Vector3d.crossProduct(fwdVec, rightVec);
        upVec.normalize();

        return Matrix.lookAt(rightVec, upVec, fwdVec, this.pos);
    }

    public void inputTick() {
        double vert = 0;
        double hor = 0;

        double xMov = 0;
        double yMov = 0;
        double zMov = 0;

        if (KeyManager.a())
            hor = -1 * 0.007 * Math.PI;
        if (KeyManager.d())
            hor = 0.007 * Math.PI;
        if (KeyManager.w())
            vert = 0.007 * Math.PI;
        if (KeyManager.s())
            vert = -1 * 0.007 * Math.PI;

        if (KeyManager.upArr()) 
            yMov = 0.1;
        if (KeyManager.downArr()) 
            yMov = -0.1;
        if (KeyManager.leftArr()) 
            xMov = 0.1;
        if (KeyManager.rightArr()) 
            xMov = -0.1;
        if (KeyManager.space())
            zMov = -0.1;
        if (KeyManager.shift())
            zMov = 0.1;

        this.phi += vert;
        this.theta += hor;

        this.pos.getMatrix().setValue(0, 0, pos.getMatrix().getValue(0, 0) + xMov);
        this.pos.getMatrix().setValue(0, 1, pos.getMatrix().getValue(0, 1) + yMov);
        this.pos.getMatrix().setValue(0, 2, pos.getMatrix().getValue(0, 2) + zMov);

        if (phi > Math.PI)
            phi = Math.PI;
        if (phi < 0)
            phi = 0;
        
        if (theta > 2 * Math.PI)
            theta -= 2 * Math.PI;
        if (theta < 0)
            theta += 2 * Math.PI;
    }

    @Override
    public void translate(double x, double y, double z) {
        pos.translate(x, y, z);
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        pos.rotate(axis, theta, point);
    }

    @Override
    public void scale(double size, Point3d point) {
        pos.scale(size, point);
    }
}
