package com.ijurnove.cpu3d;

/**
 * The <code>Material</code> class defines how light interacts with a surface. Specular, diffuse, and ambient values are given by the <code>PhongComponents</code>, but the
 * emissive value is stored in a double array. A <code>Material</code> with an emissive value greater than 0 will always be illuminated by that color. The emissive component
 * is represented by a <code>double[]</code> with three values, each corresponding to red, green, or blue respectively. 
 * <p>
 * The shininess field determines the characteristics of the specular highlight. A larger shininess value results in a smaller, more concentrated highlight, 
 * and a smaller value will result in a larger, more diffused highlight. 
 */
public class Material {
    private final PhongComponents phongComponents;
    private final double[] emissive;
    private final double shininess;

    /**
     * Constructs a <code>Material</code> with the specified <code>PhongComponents</code>, emissive value, and shininess. The emissive component is represented by a
     * <code>double[]</code> with three values, each corresponding to red, green, or blue respectively. 
     * @param phongComponents the specified PhongComponents
     * @param emissive the emissive light color
     * @param shininess the shininess exponent
     */
    public Material(PhongComponents phongComponents, double[] emissive, double shininess) {
        this.phongComponents = phongComponents;
        this.shininess = shininess;
        this.emissive = emissive;
    }

    /**
     * Returns the <code>PhongComponents</code>.
     * @return the PhongComponents
     */
    public PhongComponents getPhongComponents() { return phongComponents; }
    
    /**
     * Returns the shininess value.
     * @return the shininess value
     */
    public double getShininess() { return shininess; }

    /**
     * Returns the emissive component, represented by a <code>double[]</code> with three values, each corresponding to red, green, or blue respectively. 
     * @return the emissive component
     */
    public double[] getEmissive() { return emissive; }

    /**
     * An example material.
     * <p>
     * Ambient: 255, 255, 255 <br>
     * Diffuse: 255, 255, 255 <br>
     * Specular: 255, 255, 255 <br>
     * Emissive: 0, 0, 0 <br>
     * Shininess: 32
     */
    public static final Material PLASTIC = new Material(
        new PhongComponents(
            new int[] {255, 255, 255},
            new int[] {255, 255, 255},
            new int[] {255, 255, 255}
        ),
        new double[] {0, 0, 0},
        32
    );

    /**
     * An example material.
     * <p>
     * Ambient: 255, 255, 255 <br>
     * Diffuse: 255, 255, 255 <br>
     * Specular: 16, 16, 16 <br>
     * Emissive: 0, 0, 0 <br>
     * Shininess: 0.5
     */
    public static final Material STONE = new Material(
        new PhongComponents(
            new int[] {255, 255, 255},
            new int[] {255, 255, 255},
            new int[] {16, 16, 16}
        ),
        new double[] {0, 0, 0},
        0.5
    );

    /**
     * An example material.
     * <p>
     * Ambient: 255, 255, 255 <br>
     * Diffuse: 255, 255, 255 <br>
     * Specular: 128, 128, 128 <br>
     * Emissive: 0, 0, 0 <br>
     * Shininess: 8
     */
    public static final Material METAL = new Material(
        new PhongComponents(
            new int[] {255, 255, 255},
            new int[] {255, 255, 255},
            new int[] {128, 128, 128}
        ),
        new double[] {0, 0, 0},
        8
    );
}
