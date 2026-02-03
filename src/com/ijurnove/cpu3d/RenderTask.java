package com.ijurnove.cpu3d;
import java.util.Arrays;
import java.util.stream.Stream;

class RenderTask implements Runnable {
    private final int minX;
    private final int minY;
    private final int maxX;
    private final int maxY;

    private boolean rendering = false;
    
    private Stream<Triangle> triangles;

    private final ColorMap colorMap;


    protected RenderTask(int minX, int minY, int maxX, int maxY, ColorMap colorMap) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

        this.colorMap = colorMap;
    }

    @Override
    public void run() {
        this.renderLoop();
    }

    private synchronized void renderLoop() {
        while (true) {
            if (rendering && triangles != null) {
                triangles.filter(t -> t.overlapsWithRect(minX, minY, maxX, maxY))
                    .forEach(t -> {
                    this.colorMap.drawTriangle(t, minX, minY, maxX, maxY); 
                    
            });
                rendering = false;
            } else {
                try {
                    this.wait();
                } catch (InterruptedException e) {}
            }
        }
    }

    protected boolean isRendering() { return this.rendering; }

    protected static void allAssignTris(RenderTask[] tasks, Triangle[] tris) {
        for (RenderTask thr : tasks) {
            thr.assignTris(tris);
        }
    }

    protected static void allStartRendering(RenderTask[] tasks) {
        for (RenderTask thr : tasks) {
            thr.startRendering();
        }
    }

    protected void assignTris(Triangle[] tris) {
        this.triangles = Arrays.stream(tris);
    }

    synchronized protected void startRendering() {
        this.rendering = true;
        notify();
    }

    protected static RenderTask[] initRenderThreads(Scene scene) {
        RenderTask[] tasks = createTasks(scene.getColorMap(), (int) scene.getInitFlag(SceneInitFlag.THREADS_ACROSS), (int) scene.getInitFlag(SceneInitFlag.THREADS_UP));

        Thread[] runningThreads = new Thread[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            runningThreads[i] = new Thread(tasks[i]);
            runningThreads[i].setDaemon(true);
            runningThreads[i].start();
        }

        return tasks;
    }

    private static RenderTask[] createTasks(ColorMap cMap, int horizThreads, int vertThreads) {
        int cMapWidth = cMap.getWidth();
        int cMapHeight = cMap.getHeight();
        
        int thrWidth = cMapWidth / horizThreads;
        int thrHeight = cMapHeight / vertThreads;

        RenderTask[] threads = new RenderTask[horizThreads * vertThreads];

        int index = 0;

        for (int row = 0; row < vertThreads; row++) {
            for (int col = 0; col < horizThreads; col++) {
                // set bounds for this thread
                threads[index] = new RenderTask(col * thrWidth, row * thrHeight, ((col+1) * thrWidth) - 1, ((row + 1) * thrHeight) - 1, cMap);
                index++;
            }
        }

        return threads;
    }
}