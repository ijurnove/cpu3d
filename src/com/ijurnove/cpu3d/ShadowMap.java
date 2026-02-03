package com.ijurnove.cpu3d;
import static java.lang.Math.max;
import static java.lang.Math.min;

class ShadowMap {
    private final Camera camera;
    private final double[][] depthMap;
    private final int width;
    private final int height;
    private final ProjectionType projType;

    protected ShadowMap(ProjectionType projType, int width, int height, Camera camera) {
        this.camera = camera;
        this.width = width;
        this.height = height;
        this.projType = projType;
        this.depthMap = new double[height][width];

        this.clear();
    }

    protected Camera getCamera() { return this.camera; }

    private void clear() {
        for (double[] row : depthMap) {
            for (int col = 0; col < row.length; col++) {
                row[col] = 500;
            }
        }
    }

    protected void updateDepthMap(Scene scene) {
        this.clear();

        switch (projType) {
            case ORTHOGRAPHIC -> updateDepthMapOrtho(scene);
            case PERSPECTIVE -> updateDepthMapPersp(scene);
            default -> throw new RuntimeException("No depth map functionality for current ProjectionType " + projType);
        }
    }

    protected double shadowValue(Point3d p) {
        return switch (projType) {
            case ORTHOGRAPHIC -> shadowValueOrtho(p);
            case PERSPECTIVE -> shadowValuePersp(p);
            default -> throw new RuntimeException("ProjectionType " + projType + " is not in ShadowMap.shadowValue");
        };
    }

    private void updateDepthMapPersp(Scene scene) {
        scene.perspProjectionForPointShadowMap(camera);

        int triCount = 0;
        for (Shape3d shape : scene.getShapes()) {
            if (shape.getShapeFlag(ShapeFlag.CAST_SHADOW) == 1) {
                for (Triangle t : shape.getTriangles()) {                
                    t.setShown(false);
                    if  (
                        (t.vrtx1().xRend() < width && t.vrtx1().xRend() >= 0 && t.vrtx1().yRend() < height && t.vrtx1().yRend() >= 0) ||
                        (t.vrtx2().xRend() < width && t.vrtx2().xRend() >= 0 && t.vrtx2().yRend() < height && t.vrtx2().yRend() >= 0) ||
                        (t.vrtx3().xRend() < width && t.vrtx3().xRend() >= 0 && t.vrtx3().yRend() < height && t.vrtx3().yRend() >= 0)
                    ) {
                        t.setShown(true);
                        triCount++;
                    }
                }
            } else {
                for (Triangle t : shape.getTriangles()) {
                    t.setShown(false);
                }
            }
        }
        
        Triangle[] visibleTris = new Triangle[triCount];

        int current = 0;
        for (Shape3d shape : scene.getShapes()) {
            for (Triangle t : shape.getTriangles()) {
                if (t.isShown()) {
                    visibleTris[current] = t;
                    current++;
                }
            }
        }

        for (Triangle t : visibleTris) {
            if (t.getParent().getShapeFlag(ShapeFlag.CAST_SHADOW) == 1) {
                drawTriangleDepth(t, 0, 0, width-1, height-1);
            }
        }
    }

    private void updateDepthMapOrtho(Scene scene) {
        double maxCoord = 0;

        for (Shape3d s : scene.getShapes()) {
            for (Triangle t : s.getTriangles()) {
                for (Point3d p : t.getVertices()) {
                    if (Math.abs(p.xReal()) > maxCoord) {
                        maxCoord = Math.abs(p.xReal());
                    }
                    
                    if (Math.abs(p.yReal()) > maxCoord) {
                        maxCoord = Math.abs(p.yReal());
                    }

                    if (Math.abs(p.zReal()) > maxCoord) {
                        maxCoord = Math.abs(p.zReal());
                    }
                }
            }
        }
        
        maxCoord *= 4;

        camera.updateOrthoMatrix(maxCoord*-1, maxCoord, maxCoord*-1, maxCoord, maxCoord*-1, maxCoord);

        scene.orthoProjection(camera);

        for (Shape3d shape : scene.getShapes()) {
            if (shape.getShapeFlag(ShapeFlag.CAST_SHADOW) == 1) {
                for (Triangle tri : shape.getTriangles()) {
                    drawTriangleDepth(tri, 0, 0, width, height);
                }
            }
        }
    }

    protected double shadowValuePersp(Point3d p) {
        Point3d point = p.copy();
        Matrix m = point.getMatrix();
        double swapX = m.getValue(0, 0);
        m.setValue(0, 0, m.getValue(0, 1));
        m.setValue(0, 1, swapX);

        point.perspProjectionForPointShadowMap(camera);
        
        if (
            point.xRend() < width && 
            point.xRend() >= 0 && 
            point.yRend() < height && 
            point.yRend() >= 0 &&
            point.depth() > 0
        ) {
            double depth = point.depth();
            
            double[] mapCoords = new double[] {
                point.xRend() / width,
                point.yRend() / height
            };
            
            int shadowX = (int) Util.clamp(mapCoords[0] * width, 0, width-1);
            int shadowY = (int) Util.clamp(mapCoords[1] * height, 0, height-1);

            return (depth - 0.05 > depthMap[shadowY][shadowX] ? 0 : 1);            
        }

        return -1;
    }

    protected double shadowValueOrtho(Point3d p) {
        Point3d point = p.copy();
        Matrix m = point.getMatrix();
        double swapX = m.getValue(0, 0);
        m.setValue(0, 0, m.getValue(0, 1));
        m.setValue(0, 1, swapX);

        point.orthoProjection(camera);
        
        double depth = point.depth();

        double lightSpaceWidth = point.getRenderCoords().getValue(0, 3);
        
        double[] mapCoords = new double[] {
            point.xRend() / width / lightSpaceWidth,
            point.yRend() / height / lightSpaceWidth
        };
        depth = depth / lightSpaceWidth;
                
        int shadowX = (int) Util.clamp(mapCoords[0] * width, 0, width-1);
        int shadowY = (int) Util.clamp(mapCoords[1] * height, 0, height-1);

        return (depth - 0.005 > depthMap[shadowY][shadowX] ? 0 : 1);
    }

    protected void drawTriangleDepth(Triangle tri, int minX, int minY, int maxX, int maxY) {
        Point3d v1 = tri.vrtx1();
        Point3d v2 = tri.vrtx2();
        Point3d v3 = tri.vrtx3();

        int minBoundX = (int) min(v1.xRend(), min(v2.xRend(), v3.xRend()));
        int minBoundY = (int) min(v1.yRend(), min(v2.yRend(), v3.yRend()));
        
        int maxBoundX = (int) max(v1.xRend(), max(v2.xRend(), v3.xRend()));
        int maxBoundY = (int) max(v1.yRend(), max(v2.yRend(), v3.yRend()));

        minBoundX = max(minBoundX, minX);
        minBoundY = max(minBoundY, minY);

        maxBoundX = min(maxBoundX, maxX);
        maxBoundY = min(maxBoundY, maxY);

        for (int row = minBoundY; row <= maxBoundY; row++) {
            for (int col = minBoundX; col <= maxBoundX; col++) {
                double[] bary = tri.barycentricCoords(col, row);
                
                if ((bary[0] >= 0) && (bary[1] >= 0) && (bary[2] >= 0)) {
                    double depth = Util.baryInterpolate(bary, v1.depth(), v2.depth(), v3.depth());
                    
                    if (row > 0 && row < depthMap.length && col > 0 && col < depthMap[0].length) {
                        if (depth > 0 && depth < depthMap[row][col]) {
                            depthMap[row][col] = depth;
                        }
                    }
                }
            }
        }
    }
}
