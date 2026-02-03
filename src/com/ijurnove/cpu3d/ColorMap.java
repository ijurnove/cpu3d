package com.ijurnove.cpu3d;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import java.util.HashMap;
import java.util.List;

class ColorMap {
    private Pixel[][] screen;
    private BufferedImage image;
    private double[][] depthBuffer;
    private Scene parent;

    private int width;
    private int height;

    private int imageWidth;
    private int imageHeight;

    protected int getWidth() { return this.width; }
    protected int getHeight() { return this.height; }

    protected ColorMap(Scene parent, int col, int row) {
        this.parent = parent;
        
        this.width = col;
        this.height = row;

        this.imageWidth = (int) (width / parent.getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_ACROSS));
        this.imageHeight = (int) (height / parent.getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_UP));

        this.image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        this.screen = new Pixel[row][col];
        this.depthBuffer = new double[row][col];

        for (int r = 0; r < screen.length; r++) {
            for (int c = 0; c < screen[0].length; c++) {
                screen[r][c] = new Pixel();
                depthBuffer[r][c] = 9999;
            }
        }
        // System.out.println("width: " + screen[0].length + " height: " + screen.length);
    }

    protected void drawTriangle(Triangle tri, int minX, int minY, int maxX, int maxY) {
        Point3d v1 = tri.vrtx1();
        Point3d v2 = tri.vrtx2();
        Point3d v3 = tri.vrtx3();

        // bounding box corners
        int minBoundX = (int) min(v1.xRend(), min(v2.xRend(), v3.xRend()));
        int minBoundY = (int) min(v1.yRend(), min(v2.yRend(), v3.yRend()));
        
        int maxBoundX = (int) max(v1.xRend(), max(v2.xRend(), v3.xRend()));
        int maxBoundY = (int) max(v1.yRend(), max(v2.yRend(), v3.yRend()));

        // limit bounding box to thread's range
        minBoundX = max(minBoundX, minX);
        minBoundY = max(minBoundY, minY);

        maxBoundX = min(maxBoundX, maxX);
        maxBoundY = min(maxBoundY, maxY);

        // loop over bounding box
        for (int row = minBoundY; row <= maxBoundY; row++) {
            for (int col = minBoundX; col <= maxBoundX; col++) {
                double[] bary = tri.barycentricCoords(col, row);

                if ((bary[0] >= 0) && (bary[1] >= 0) && (bary[2] >= 0)) {
                    if (parent.getFlag(SceneFlag.WIREFRAME) != 1) {
                        double pointZ = Util.baryInterpolate(bary, v1.depth(), v2.depth(), v3.depth());

                        if (pointZ > 0 && pointZ < depthBuffer[row][col]) {
                            depthBuffer[row][col] = pointZ;
                            
                            int[] colors = readTexture(tri, bary);
                            
                            doPhong(parent.getViewCamera(), tri, bary, colors, row, col);
                        } 
                    } else {
                        if ((bary[0] >= 0 && bary[0] <= 0.025) || (bary[1] >= 0 && bary[1] <= 0.025) || (bary[2] >= 0 && bary[2] <= 0.025)) {
                            this.setPixel(row, col, 0, 255, 0);
                        }
                    }
                }
            }
        }
    }

    private void doPhong(Camera camera, Triangle tri, double[] bary, int[] colors, int row, int col) {
        Point3d point = tri.point3dFromBary(bary);
        double[] lightValues = LightCalc.phongLighting(camera, point, Vector3d.fromBary(tri.getPointNormals(), bary), parent, tri.getMaterial(), tri.getParent());

        for (int i = 0; i < 3; i++) {
            colors[i] *= lightValues[i];
        }

        setPixel(row, col, colors);
    }

    private int[] readTexture(Triangle tri, double[] bary) {
        UV[] textureCoords = tri.getTextureCoords();
        BufferedImage texture = tri.getTexture();

        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();

        double u = 0;
        double v = 0;

        for (int i = 0; i < 3; i++) {
            u += textureCoords[i].u() * bary[i];
            v += textureCoords[i].v() * bary[i];
        }

        u *= textureWidth;
        v *= textureHeight;

        u = Util.clamp(u, 0, textureWidth-1);
        v = Util.clamp(v, 0, textureHeight-1);

        int argb = texture.getRGB((int) u, (int) v);

        int red = argb >> 16 & 0xff;
        int green = argb >> 8 & 0xff;
        int blue = argb & 0xff;

        return new int[] {red, green, blue};
    }

    // private void doGoraud(Triangle tri, double[] bary, int[] baseColors, int row, int col) {
    //     // lighting
    //     double lightV1 = LightCalc.goraudDiffuse(tri.getVrtx1(), tri.getPointNormals()[0]);
    //     double lightV2 = LightCalc.goraudDiffuse(tri.getVrtx2(), tri.getPointNormals()[1]);
    //     double lightV3 = LightCalc.goraudDiffuse(tri.getVrtx3(), tri.getPointNormals()[2]);

    //     double w1 = bary[0];
    //     double w2 = bary[1];
    //     double w3 = bary[2];
        
    //     double finalLight = (lightV1 * w1) + (lightV2 * w2) + (lightV3 * w3);

    //     this.setPixel(row, col, (int) (finalLight * baseColors[0]), (int) (finalLight * baseColors[1]), (int) (finalLight * baseColors[2]));
    // }

    protected void drawString(int x, int y, String str, int red, int green, int blue) {
        int charWidth = 5;
        int charSpacing = 2;
        
        int curX = x;
        char[] chars = str.toCharArray();

        for (char c : chars) {
            boolean[][] boolArr = TextMap.getCharArr(c);
            for (int row = 0; row < boolArr.length; row++) {
                for (int col = 0; col < boolArr[0].length; col++) {
                    if (boolArr[row][col]) {
                        setPixel(row + y, col + curX, red, green, blue);
                    }
                }
            }

            curX += charWidth + charSpacing;
        }
    }

    protected int[] gammaCorrection(int[] values) {
        double[] doubleClr = gammaCorrection(new double[] {
            values[0] / 255D,
            values[1] / 255D,
            values[2] / 255D
        });

        return new int[] {
            (int) (doubleClr[0] * 255),
            (int) (doubleClr[1] * 255),
            (int) (doubleClr[2] * 255)
        };
    }

    public double[] gammaCorrection(double[] values) {
        double[] clr = new double[] {
            values[0],
            values[1],
            values[2]
        };

        for (int i = 0; i < clr.length; i++) {
            clr[i] = pow(clr[i], (1 / this.parent.getFlag(SceneFlag.GAMMA)));
        }

        return clr;
    }

    protected void setPixel(int row, int col, int red, int green, int blue) {
        if (row >= height || row < 0 || col >= width || col < 0) {
            return;
        }

        int[] endColors;
        if (this.parent.getFlag(SceneFlag.DO_GAMMA_CORRECTION) == 1) {
            endColors = gammaCorrection(new int[] {
                red,
                green,
                blue
            });
        } else {
            endColors = new int[] {red, green, blue};
        }

        screen[row][col].setValues(clamp(endColors[0], 0, 255), clamp(endColors[1], 0, 255), clamp(endColors[2], 0, 255));
    }

    private static int clamp(int val, int minVal, int maxVal) {
        return max(min(val, maxVal), minVal);
    }

    protected void setPixel(int row, int col, int[] values) { setPixel(row, col, values[0], values[1], values[2]); }

    protected void reset(int red, int green, int blue) {
        for (Pixel[] row : screen) {
            for (Pixel p : row) {
                p.setValues(red, green, blue);
            }
        }
    }

    protected void reset(int[] rgb) {
        this.reset(rgb[0], rgb[1], rgb[2]);
    }

    protected void reset(Color color) {
        this.reset(color.getRed(), color.getGreen(), color.getBlue());
    }

    protected void clearDepthBuffer() {
        for (double[] depthBuffer1 : depthBuffer) {
            for (int col = 0; col < depthBuffer[0].length; col++) {
                depthBuffer1[col] = 9999;
            }
        }
    }

    protected void updateImage() {
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                pixels[(row * imageWidth) + col] = screen
                    [(int) (row * parent.getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_UP))]
                    [(int) (col * parent.getInitFlag(SceneInitFlag.RESOLUTION_MULTIPLIER_ACROSS))]
                    .argbIntValue();
            }
        }
    }

    protected BufferedImage getImg() { return this.image; }

    protected void drawGraph(int x, int y, List<Integer> values, int height, int maxVal) {
        int curX = x;

        for (int val : values) {
            int barSize = (int) (((double) val / height) * maxVal);
            for (int i = y; i >= y - barSize; i--) {
                setPixel(i, curX, (int) Math.min(255, 255 - (val / (double) height) * 255), (int) Math.min(255, (val / (double) height) * 255), 0);
            }

            curX++;
        }
    }

    protected static class Pixel {
        private int red;
        private int green;
        private int blue;

        protected Pixel() {
            this.red = 0;
            this.green = 0;
            this.blue = 0;
        }

        protected Pixel(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        protected void setValues(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        protected void setValues(int[] values) {
            setValues(values[0], values[1], values[2]);
        }

        protected int argbIntValue() {
            return 0xFF000000 | ((0xFF & red) << 16) | ((0xFF & green) << 8) | (0xFF & blue);
        }
    }

    protected static class TextMap {
        private static int[][] SPACE_INT = new int[][] {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
        };

        private static int[][] ZERO_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0}
        };

        private static int[][] ONE_INT = new int[][] {
            {0,0,1,0,0},
            {0,1,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,1,1,1,0}
        };

        private static int[][] TWO_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {0,0,0,0,1},
            {0,0,0,0,1},
            {0,1,1,1,1},
            {1,0,0,0,0},
            {1,0,0,0,0},
            {1,1,1,1,1},
        };

        private static int[][] THREE_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {0,0,0,0,1},
            {0,1,1,1,0},
            {0,0,0,0,1},
            {0,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0},
        };

        private static int[][] FOUR_INT = new int[][] {
            {0,0,0,1,0},
            {0,0,1,1,0},
            {0,1,0,1,0},
            {1,0,0,1,0},
            {1,1,1,1,1},
            {0,0,0,1,0},
            {0,0,0,1,0},
            {0,0,0,1,0},
        };

        private static int[][] FIVE_INT = new int[][] {
            {1,1,1,1,1},
            {1,0,0,0,0},
            {1,0,0,0,0},
            {1,1,1,1,0},
            {0,0,0,0,1},
            {0,0,0,0,1},
            {0,0,0,0,1},
            {1,1,1,1,0},
        };

        private static int[][] SIX_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,0},
            {1,0,0,0,0},
            {1,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0},
        };

        private static int[][] SEVEN_INT = new int[][] {
            {1,1,1,1,1},
            {0,0,0,0,1},
            {0,0,0,1,0},
            {0,0,0,1,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
        };

        private static int[][] EIGHT_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0},
        };

        private static int[][] NINE_INT = new int[][] {
            {0,1,1,1,0},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,1},
            {0,0,0,0,1},
            {0,0,0,0,1},
            {1,0,0,0,1},
            {0,1,1,1,0},
        };

        private static int[][] PERIOD_INT = new int[][] {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,1,1,0,0},
            {0,1,1,0,0},
        };

        private static int[][] ERROR_INT = new int[][] {
            {1,1,1,1,1},
            {1,0,0,0,1},
            {1,1,0,1,1},
            {1,0,1,0,1},
            {1,1,0,1,1},
            {1,0,0,0,1},
            {1,0,0,0,1},
            {1,1,1,1,1},
        };

        private static int[][] LIGHTBULB_INT = new int[][] {
            {0,0,1,1,1,1,0,0}, // 1
            {0,1,0,0,0,0,1,0}, // 2
            {1,0,0,0,0,0,0,1}, // 3
            {1,0,0,0,0,0,0,1}, // 4
            {1,0,1,0,0,1,0,1}, // 5
            {1,0,0,1,1,0,0,1}, // 6
            {0,1,0,1,1,0,1,0}, // 7
            {0,0,1,1,1,1,0,0}, // 8
            {0,0,1,0,0,1,0,0}, // 9
            {0,0,1,1,1,1,0,0}, // 10
            {0,0,1,0,0,1,0,0}, // 11
            {0,0,0,1,1,0,0,0}  // 12
        };

        private static final HashMap<Character, boolean[][]> boolGridMap = new HashMap<>() {{
            put(' ', toBoolArr(SPACE_INT));
            put('.', toBoolArr(PERIOD_INT));
            put('0', toBoolArr(ZERO_INT));
            put('1', toBoolArr(ONE_INT));
            put('2', toBoolArr(TWO_INT));
            put('3', toBoolArr(THREE_INT));
            put('4', toBoolArr(FOUR_INT));
            put('5', toBoolArr(FIVE_INT));
            put('6', toBoolArr(SIX_INT));
            put('7', toBoolArr(SEVEN_INT));
            put('8', toBoolArr(EIGHT_INT));
            put('9', toBoolArr(NINE_INT));
            put('|', toBoolArr(LIGHTBULB_INT));
        }};

        protected static boolean[][] getCharArr(char character) {
            try {
                return boolGridMap.get(character);
            } catch (Exception e) {
                return toBoolArr(ERROR_INT);
            }
        }

        protected static boolean[][] toBoolArr(int[][] grid) {
            boolean[][] boolGrid = new boolean[grid.length][grid[0].length];

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                    boolGrid[row][col] = (grid[row][col] == 1);
                }
            }

            return boolGrid;
        }
    }
}