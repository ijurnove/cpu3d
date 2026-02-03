package com.ijurnove.cpu3d;

/**
 * Objects that extend the <code>Light</code> class are used by <code>Scene</code> to calculate lighting. Each extension of the <code>Light</code> class must have
 * a <code>LightType</code> and a <code>PhongComponents</code> to describe the color and type of light that it casts. 
 * <p>
 * <code>lightVec()</code>, <code>distance()</code>, <code>shadowValue()</code>, <code>updateShadowMap()</code>, and <code>init()</code> are automatically run in
 * light calculations and should never need to be manually called.
 */
public abstract class Light {
    private PhongComponents components;
    private final LightType type;
    private Scene parent;

    protected Light(LightType type, PhongComponents components) {
        this.type = type;
        this.components = components;
    }

    /**
     * Returns the <code>LightType</code>.
     * @return the LightType
     */
    public LightType getType() { return this.type; }

    /**
     * Returns the <code>PhongComponents</code>. 
     * @return the PhongComponents
     */
    public PhongComponents getComponents() { return this.components; }

    /**
     * Returns the parent <code>Scene</code>.
     * @return the parent Scene
     */
    public Scene getParent() { return this.parent; }

    /**
     * Sets the parent <code>Scene</code> of this <code>Light</code>.
     * @param parent the scene to set
     */
    public void setParent(Scene parent) { this.parent = parent; }

    /**
     * Sets the <code>PhongComponents</code> of this <code>Light</code>.
     * @param components the PhongComponents to set
     */
    public void setComponents(PhongComponents components) { this.components = components; }

    /**
     * Returns a <code>Vector3d</code> representing the light angle at any given <code>Point3d</code>.
     * @param point the Point3d where the light hits
     * @return a Vector3d representing the direction of the light
     */
    abstract Vector3d lightVec(Point3d point);

    /**
     * Returns the distance that the light has traveled.
     * @param vec the vector between the Light and a Point3d where the light hits
     * @return the distance between the Light and the point where the light hits
     */
    abstract double distance(Vector3d vec);

    /**
     * Returns the shadow value at the specified <code>Point3d</code>. 
     * @param point the specified Point3d
     * @return the shadow value at the given Point3d
     */
    abstract double shadowValue(Point3d point);

    /**
     * Updates this <code>Light</code>'s <code>ShadowMap</code> using its parent <code>Scene</code>.
     */
    abstract void updateShadowMap();

    /**
     * Runs after a <code>Light</code> is added to a <code>Scene</code>. The <code>Light</code>'s parent can be accessed in this method.
     */
    abstract void init();
}
