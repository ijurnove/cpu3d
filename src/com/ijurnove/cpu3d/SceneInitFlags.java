package com.ijurnove.cpu3d;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.EnumMap;

/**
 * <code>SceneInitFlags</code> is used in a <code>Scene</code> to set certain values at initialization. Values cannot be modified after a <code>Scene</code> has been initialized.
 * Values are stored in an EnumMap with key <code>SceneInitFlag</code> and value <code>Double</code>.
 * <p>
 * Refer to enum <code>SceneInitFlag</code> to see what each flag controls.
 */
public class SceneInitFlags {
    private final EnumMap<SceneInitFlag, Double> flags = new EnumMap<>(SceneInitFlag.class);
    private boolean locked = false;

    /**
     * Constructs a new <code>SceneInitFlags</code> with the default values for each flag.
     */
    public SceneInitFlags() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        flags.put(SceneInitFlag.IMAGE_SIZE_ACROSS, screenSize.getWidth());
        flags.put(SceneInitFlag.IMAGE_SIZE_UP, screenSize.getHeight());

        flags.put(SceneInitFlag.RESOLUTION_MULTIPLIER_ACROSS, 1D);
        flags.put(SceneInitFlag.RESOLUTION_MULTIPLIER_UP, 1D);

        flags.put(SceneInitFlag.SHADOW_RESOLUTION_ACROSS, 2048D);
        flags.put(SceneInitFlag.SHADOW_RESOLUTION_UP, 2048D);

        flags.put(SceneInitFlag.THREADS_ACROSS, 3D);
        flags.put(SceneInitFlag.THREADS_UP, 3D);

    }

    protected void lock() {
        this.locked = true;
    }

    /**
     * Sets the value of a specified <code>SceneInitFlag</code> to a given double.
     * @param flag the specified SceneInitFlag
     * @param value the value to set
     */
    public void setFlag(SceneInitFlag flag, double value) {
        if (locked) {
            throw new RuntimeException("Cannot modify flags after scene creation");
        }
        
        flags.replace(flag, value);
    }

    /**
     * Returns the value corresponding to a given <code>SceneInitFlag</code>.
     * @param flag the given SceneInitFlag
     * @return the value corresponding to the given SceneInitFlag
     */
    public double getFlag(SceneInitFlag flag) {
        return flags.get(flag);
    }
}
