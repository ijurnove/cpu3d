package com.ijurnove.cpu3d;
/**
 * DirectionalLight is a Light with a direction but no specific position. A DirectionalLight casts light
 * over the entire scene in the direction of the specified Vector3d. 
 */
public class DirectionalLight extends Light implements Rotatable {
    private final Vector3d direction;
    private Camera camera;
    private ShadowMap shadowMap;
    /**
     * Constructs a <code>DirectionalLight</code> with the specified direction and <code>PhongComponents</code>.
     * @param direction the direction of the light
     * @param components the PhongComponents to set, which detail the color and intensity of each type of light
     */
    public DirectionalLight(Vector3d direction, PhongComponents components) {
        super(LightType.DIRECTIONAL, components);
        this.direction = direction;
    }

    @Override
    protected void init() {
        this.camera = new Camera(this.getParent(), new Point3d(0, 0, 0), 0, 0, 90);

        // TODO: adjust camera pos over time to cover the entire scene
        for (int i = 0; i < 100; i++) {
            this.camera.translate(direction);
        }

        this.shadowMap = new ShadowMap(
            ProjectionType.ORTHOGRAPHIC, 
            (int) this.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_ACROSS), (int) this.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_UP), 
            this.camera
        );
    }

    protected Camera getCamera() { return this.camera; }

    // private void updateCameraDirection() {
    //     double[] cameraSphericalLookCoords = Util.cartesianToSphere(direction.x(), direction.y(), direction.z());

    //     camera.setTheta(cameraSphericalLookCoords[1]);
    //     camera.setPhi(cameraSphericalLookCoords[2]);
    // }

    @Override
    protected void updateShadowMap() {
        // updateCameraDirection();
        shadowMap.updateDepthMap(this.getParent());
    }

    @Override
    protected double shadowValue(Point3d point) {
        return shadowMap.shadowValue(point);
    }

    @Override
    protected Vector3d lightVec(Point3d point) {
        return direction;
    }

    @Override
    protected double distance(Vector3d vec) {
        return 1;
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        direction.rotate(axis, theta);
    }
}
