package com.ijurnove.cpu3d;

/**
 * <code>PointLight</code> extends the abstract class <code>Light</code>. A <code>PointLight</code> illuminates the area around it. Shadow
 * calculations are significantly more expensive than DirectionalLights, as shadows must be calculated in all six directions. 
 * <p>
 * <code>PointLight</code> have a <code>decayPow</code> field that specifies how fast the light should decay as it gets farther away 
 * from the source. For each illuminated point, the diffuse and specular light values are divided by
 * the distance from the light source raised to the power of <code>decayPow</code>. The default value is 3. 
 * <code>PointLight</code>s can be translated, rotated, and scaled. 
 */
public class PointLight extends Light implements Translatable, Rotatable, Scalable {
    private final Point3d pos;
    private ShadowMap[] cubeMap;
    private double decayPow = 3;

    /**
     * Returns the position as a <code>Point3d</code>. 
     * @return the position of this PointLight
     */
    public Point3d getPos() { return this.pos; }    
    
    /**
     * Constructs a <code>PointLight</code> with a specified position and <code>PhongComponents</code>.
     * @param pos the specified position
     * @param components the specified PhongComponents
     */
    public PointLight(Point3d pos, PhongComponents components) {
        super(LightType.POINT, components);
        this.pos = pos;

        double x = pos.xReal();
        pos.setX(pos.yReal());
        pos.setY(x);
    }

    @Override
    protected void init() {
        double fov = 127;
        Camera[] cams = new Camera[] {
            new Camera(this.getParent(), pos, Math.PI, 0, fov), // up
            new Camera(this.getParent(), pos, 0, 0, fov), // down
            new Camera(this.getParent(), pos, Math.PI/2, 0, fov), // forwards
            new Camera(this.getParent(), pos, Math.PI/2, Math.PI + 0.00000001, fov), // backwards - floating point error occurs if I don't add a small number
            new Camera(this.getParent(), pos, Math.PI/2, Math.PI*0.5, fov), // left
            new Camera(this.getParent(), pos, Math.PI/2, Math.PI*1.5, fov) // right
        };

        cubeMap = new ShadowMap[6];

        for (int i = 0; i < 6; i++) {
            cubeMap[i] = new ShadowMap(
                ProjectionType.PERSPECTIVE,
                (int) this.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_ACROSS), (int) this.getParent().getInitFlag(SceneInitFlag.SHADOW_RESOLUTION_UP),
                cams[i]
            );
            // System.out.println(cams[i].getPhi() + "," + cams[i].getTheta());
        }
    }

    /**
     * Sets the decay exponent.
     * @param decay the decay exponent
     */
    public void setDecay(double decay) { this.decayPow = decay; }

    /**
     * Returns the decay exponent.
     * @return the decay exponent
     */
    public double getDecay() { return this.decayPow; }

    @Override
    protected void updateShadowMap() {
        for (ShadowMap map : cubeMap) {
            map.updateDepthMap(this.getParent());
        }
    }

    @Override
    protected double shadowValue(Point3d point) {
        double[] vals = new double[6];
        double totalShadow = 0;
        int timesSeen = 0;

        for (int i = 0; i < 6; i++) {
            vals[i] = cubeMap[i].shadowValue(point);
        }
        
        for (double d : vals) {
            if (d != -1) {
                timesSeen++;
            }
            if (d > 0) {
                totalShadow = d;
            }
        }
        
        if (timesSeen == 0) {
            return -1;
        }

        return totalShadow;
    }

    @Override
    protected Vector3d lightVec(Point3d point) {
        return new Vector3d(point, this.pos);
    }

    @Override
    protected double distance(Vector3d lightVec) {
        return lightVec.magnitude();
    }
    
    @Override
    public void translate(double x, double y, double z) {
        pos.translate(x, y, z);
        for (ShadowMap m : this.cubeMap) {
            m.getCamera().translate(x, y, z);
        }
    }

    @Override
    public void rotate(int axis, double theta, Point3d point) {
        pos.rotate(axis, theta, point);
        for (ShadowMap m : this.cubeMap) {
            m.getCamera().rotate(axis, theta, point);
        }
    }

    @Override
    public void scale(double size, Point3d point) {
        pos.scale(size, point);
        for (ShadowMap m : this.cubeMap) {
            m.getCamera().scale(size, point);
        }
    }
}