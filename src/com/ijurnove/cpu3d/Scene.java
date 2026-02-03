package com.ijurnove.cpu3d;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A <code>Scene</code> consists of a set of <code>Shape3d</code>s, a set of <code>Light</code>s, and a <code>Camera</code>. 
 * <code>Scene</code>s have two sets of flags, one which can be modified at any time, and one which can only be set at
 * initialization. These are stored as a <code>SceneFlags</code> object and a <code>SceneInitFlags</code> object respectively. 
 * <p>
 * To render a <code>Scene</code>, use the <code>render()</code> method. 
 */
public final class Scene {
    private final List<Shape3d> shapes = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private Camera viewCamera;
    
    private final SceneFlags flags = new SceneFlags();
    private final SceneInitFlags initFlags;
    
    private final ColorMap colorMap;
    private final RenderTask[] renderTasks;
    
    private final int pixelWidth;
    private final int pixelHeight;

    private int[] backgroundRGB = new int[] {66, 176, 245};
    
    /**
     * Constructs an empty <code>Scene</code> with the specified <code>SceneInitFlags</code>.
     * @param initFlags the specified SceneInitFlags
     */
    public Scene(SceneInitFlags initFlags) {
        this.initFlags = initFlags;
        this.initFlags.lock();
        
        this.pixelWidth = (int) (getInitFlag(SceneInitFlag.IMAGE_SIZE_ACROSS) * getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_ACROSS));
        this.pixelHeight = (int) (getInitFlag(SceneInitFlag.IMAGE_SIZE_UP) * getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_UP));
        
        this.colorMap = new ColorMap(this, (int) getInitFlag(SceneInitFlag.IMAGE_SIZE_ACROSS), (int) getInitFlag(SceneInitFlag.IMAGE_SIZE_UP));
        renderTasks = RenderTask.initRenderThreads(this);
    }
    
    /**
     * Constructs a Scene with a specified SceneInitFlags, Shape3d[], Light[], and Camera.
     * @param initFlags the specified SceneInitFlags
     * @param shapes the specified Shape3d[]
     * @param lights the specified Light[]
     * @param viewCamera the specified Camera from which the Scene will be rendered
     */
    public Scene(SceneInitFlags initFlags, Shape3d[] shapes, Light[] lights, Camera viewCamera) {
        this.initFlags = initFlags;
        this.initFlags.lock();
        
        this.pixelWidth = (int) (getInitFlag(SceneInitFlag.IMAGE_SIZE_ACROSS) * getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_ACROSS));
        this.pixelHeight = (int) (getInitFlag(SceneInitFlag.IMAGE_SIZE_UP) * getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_UP));
        
        this.viewCamera = viewCamera;
        
        this.shapes.addAll(Arrays.asList(shapes));
        this.lights.addAll(Arrays.asList(lights));
        
        for (Shape3d s : shapes) {
            s.setParent(this);
        }
        
        for (Light l : lights) {
            l.setParent(this);
            l.init();
        }
        
        viewCamera.setParent(this);
        
        this.colorMap = new ColorMap(this, (int) getInitFlag(SceneInitFlag.IMAGE_SIZE_ACROSS), (int) getInitFlag(SceneInitFlag.IMAGE_SIZE_UP));
        renderTasks = RenderTask.initRenderThreads(this);
    }
    
    /**
     * Returns the Shape3d at a specified index.
     * @param index the index of the Shape3d
     * @return the Shape3d at the specified index
     */
    public Shape3d getShape(int index) { return shapes.get(index); }

    /**
     * Returns the Light at a specified index.
     * @param index the index of the Light
     * @return the Light at the specified index
     */
    public Light getLight(int index) { return lights.get(index); }

    /**
     * Returns the Camera that this Scene is rendered from.
     * @return the Camera that this Scene is rendered from
     */
    public Camera getViewCamera() { return this.viewCamera; }

    /**
     * Returns the value of a specified SceneInitFlag.
     * @param flag the specified SceneInitFlag
     * @return the value of the specified SceneInitFlag
     */
    public final double getInitFlag(SceneInitFlag flag) { return this.initFlags.getFlag(flag); }

    /**
     * Returns the number of Shape3ds in this Scene.
     * @return the number of Shape3ds in this Scene
     */
    public int shapeCount() { return shapes.size(); }

    /**
     * Returns the number of Lights in this Scene.
     * @return the number of Lights in this Scene
     */
    public int lightCount() { return lights.size(); }

    protected ColorMap getColorMap() { return this.colorMap; }
    protected int getPixelWidth() { return this.pixelWidth; }
    protected int getPixelHeight() { return this.pixelHeight; }

    protected List<Shape3d> getShapes() { return this.shapes; }
    protected List<Light> getLights() { return this.lights; }

    /**
     * Adds a specified Light to this Scene.
     * @param light the specified Light
     */
    public void addLight(Light light) { 
        light.setParent(this);
        light.init();
        lights.add(light);
    }

    /**
     * Adds a specified Shape3d to this Scene.
     * @param shape the specified Shape3d
     */
    public void addShape(Shape3d shape) { 
        shape.setParent(this);
        shapes.add(shape);
    }

    /**
     * Sets the Camera of this Scene.
     * @param viewCamera the specified Camera
     */
    public void setViewCamera(Camera viewCamera) { 
        viewCamera.setParent(this);
        this.viewCamera = viewCamera; 
    }
    
    /**
     * Sets a SceneFlag to a given value.
     * @param flag the specified SceneFlag
     * @param value the new value
     */
    public void setFlag(SceneFlag flag, double value) {
        this.flags.setFlag(flag, value);
    }

    /**
     * Returns the value of a specified SceneFlag.
     * @param flag the specified SceneFlag
     * @return the value of the specified SceneFlag
     */
    public double getFlag(SceneFlag flag) {
        return this.flags.getFlag(flag);
    }

    /**
     * Sets the background color of this Scene.
     * @param rgb the RGB color values to set. values range from 0-255
     */
    public void setBackground(int[] rgb) {
        this.backgroundRGB = rgb;
    }

    protected void project(Camera perspectiveCam) {
        for (Shape3d s : shapes) {
            s.perspProjection(perspectiveCam);
        }
    }

    protected void perspProjectionForPointShadowMap(Camera perspCam) {
        for (Shape3d s : shapes) {
            s.perspProjectionForPointShadowMap(perspCam);
        }
    }

    protected void orthoProjection(Camera orthoCam) {
        for (Shape3d s : shapes) {
            s.orthoProjection(orthoCam);
        }
    }

    protected void updateShadows() {
        for (Light l : this.lights) {
            if (getFlag(SceneFlag.DO_SHADOWS) == 1) {
                l.updateShadowMap();            
            }
        }
    }

    /**
     * Renders this Scene from the perspective of its Camera. Returns a BufferedImage with dimensions
     * specified in the SceneInitFlags.
     * @return the rendered Scene as a BufferedImage
     */
    public BufferedImage render() {
        // viewCamera.inputTick();
        viewCamera.tick();

        this.updateShadows();
        this.project(viewCamera);


        int triCount = 0;
        for (Shape3d shape : this.shapes) {
            if (shape.getShapeFlag(ShapeFlag.VISIBLE) == 1) {
                for (Triangle t : shape.getTriangles()) {                
                    t.setShown(false);
                    if (
                        t.overlapsWithRect(0, 0, (int) this.pixelWidth, (int) this.pixelHeight) &&
                        t.vrtx1().depth() > 0 &&
                        t.vrtx2().depth() > 0 &&
                        t.vrtx3().depth() > 0
                    ) {
                        if (t.isFacingViewer() || !(getFlag(SceneFlag.DO_BACKFACE_CULLING) == 1)) {
                            t.setShown(true);
                            triCount++;
                        }
                    }
                }
            }
        }
        
        Triangle[] allTriangles = new Triangle[triCount];

        int current = 0;
        for (Shape3d shape : this.shapes) {
            if (shape.getShapeFlag(ShapeFlag.VISIBLE) == 1) {
                for (Triangle t : shape.getTriangles()) {
                    if (t.isShown()) {
                        allTriangles[current] = t;
                        current++;
                    }
                }
            }
        }

        this.colorMap.reset(backgroundRGB);
        this.colorMap.clearDepthBuffer();

        RenderTask.allAssignTris(renderTasks, allTriangles);
        RenderTask.allStartRendering(renderTasks);
        
        boolean rendering = true;
        while (rendering) {
            rendering = false;
            for (RenderTask thr : renderTasks) {
                if (thr.isRendering()) {
                    rendering = true;
                }
            }
        }
        
        if (getFlag(SceneFlag.DISPLAY_LIGHTS) == 1) {
            for (Light l : this.lights) {
                if (l.getType() == LightType.POINT) {
                    PointLight pl = (PointLight) l;
                    pl.getPos().perspProjection(viewCamera);
                    this.colorMap.drawString((int) pl.getPos().xRend(), (int) pl.getPos().yRend(), "|", 255, 255, 0);
                }
            }
        }

        this.colorMap.updateImage();
        return this.colorMap.getImg();
    }
}
