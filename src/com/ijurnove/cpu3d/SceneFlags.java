package com.ijurnove.cpu3d;

import java.util.EnumMap;

/**
 * <code>SceneFlags</code> is used in a <code>Scene</code> to toggle certain features or change specific values. 
 * Values are stored in an EnumMap with key <code>SceneFlag</code> and value <code>Double</code>.
 * <p>
 * Refer to enum <code>SceneFlag</code> to see what each flag controls.
 */
public class SceneFlags {
    private final EnumMap<SceneFlag, Double> flags = new EnumMap<>(SceneFlag.class);

    /**
     * Constructs a new <code>SceneFlags</code> with the default values for each flag.
     */
    public SceneFlags() {
        flags.put(SceneFlag.DO_BACKFACE_CULLING, 1D);
        flags.put(SceneFlag.DO_GAMMA_CORRECTION, 1D);
        flags.put(SceneFlag.DO_SHADOWS, 1D);
        flags.put(SceneFlag.DO_LIGHTING, 1D);

        flags.put(SceneFlag.WIREFRAME, 0D);
        flags.put(SceneFlag.DISPLAY_LIGHTS, 0D);

        flags.put(SceneFlag.GAMMA, 2.2);
    }

    /**
     * Returns the value corresponding to a given <code>SceneFlag</code>.
     * @param flag the given SceneFlag
     * @return the value corresponding to the given SceneFlag
     */
    public double getFlag(SceneFlag flag) {
        return flags.get(flag);
    }

    /**
     * Sets the value of a specified <code>SceneFlag</code> to a given double.
     * @param flag the flag to set
     * @param value the value to set
     */
    public void setFlag(SceneFlag flag, double value) {
        flags.replace(flag, value);
    }
}
