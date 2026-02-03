package com.ijurnove.cpu3d;

/**
 * A <code>PhongComponents</code> object contains information about lighting, for the Blinn-Phong lighting model. Each <code>PhongComponents</code> has an ambient color, a diffuse color, and a specular color.
 * Ambient lighting illuminates the entire scene at once, diffuse lighting is matte, and specular lighting is a shiny highlight seen on smooth objects. 
 * <p>
 * RGB components range from 0-1, but can be set higher for a more intense light/reflection.
 */
public class PhongComponents {
    private final double[] ambient;
    private final double[] diffuse;
    private final double[] specular;

    /**
     * Constructs a <code>PhongComponents</code> with the given ambient, diffuse, and specular RGB values. Values range from 0-1. 
     * @param ambient the ambient color
     * @param diffuse the diffuse color
     * @param specular the specular color
     */
    public PhongComponents(double[] ambient, double[] diffuse, double[] specular) {
        assert (ambient.length == 3 && diffuse.length == 3 && specular.length == 3) : "Wrong number of color components";

        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    /**
     * Constructs a <code>PhongComponents</code> with the given ambient, diffuse, and specular RGB values. Values range from 0-255.
     * @param ambient the ambient color
     * @param diffuse the diffuse color
     * @param specular the specular color
     */
    public PhongComponents(int[] ambient, int[] diffuse, int[] specular) {
        this.ambient = new double[3];
        this.diffuse = new double[3];
        this.specular = new double[3];

        for (int i = 0; i < 3; i++) {
            this.ambient[i] = (ambient[i] / 255D);
            this.diffuse[i] = (diffuse[i] / 255D);
            this.specular[i] = (specular[i] / 255D);
        }
    }

    /**
     * Returns the ambient color component. Values range from 0-1.
     * @return the ambient color component
     */
    public double[] getAmb() { return ambient; }

    /**
     * Returns the diffuse color component. Values range from 0-1.
     * @return the diffuse color component
     */
    public double[] getDiff() { return diffuse; }

    /**
     * Returns the specular color component. Values range from 0-1.
     * @return the specular color component
     */
    public double[] getSpec() { return specular; }
}
