package com.ijurnove.cpu3d;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Camera controller for testing purposes
public class KeyManager extends KeyAdapter {

    private final boolean[] keys;

    private static final KeyManager keyManager = new KeyManager();

    public static KeyManager getKeyManager() {return keyManager;}

    public static boolean esc() {return keyManager.keys[KeyEvent.VK_ESCAPE];}

    public static boolean w() {return keyManager.keys[KeyEvent.VK_W];}
    public static boolean a() {return keyManager.keys[KeyEvent.VK_A];}
    public static boolean s() {return keyManager.keys[KeyEvent.VK_S];}
    public static boolean d() {return keyManager.keys[KeyEvent.VK_D];}

    public static boolean r() {return keyManager.keys[KeyEvent.VK_R];}

    public static boolean leftArr() {return keyManager.keys[KeyEvent.VK_LEFT];}
    public static boolean rightArr() {return keyManager.keys[KeyEvent.VK_RIGHT];}
    public static boolean upArr() {return keyManager.keys[KeyEvent.VK_UP];}
    public static boolean downArr() {return keyManager.keys[KeyEvent.VK_DOWN];}

    public static boolean shift() {return keyManager.keys[KeyEvent.VK_SHIFT];}
    public static boolean space() {return keyManager.keys[KeyEvent.VK_SPACE];}

    public static boolean t() {return keyManager.keys[KeyEvent.VK_T];}

    public static boolean one() {return keyManager.keys[KeyEvent.VK_1];}
    public static boolean two() {return keyManager.keys[KeyEvent.VK_2];}
    public static boolean three() {return keyManager.keys[KeyEvent.VK_3];}
    public static boolean four() {return keyManager.keys[KeyEvent.VK_4];}
    public static boolean five() {return keyManager.keys[KeyEvent.VK_5];}
    public static boolean six() {return keyManager.keys[KeyEvent.VK_6];}
    public static boolean seven() {return keyManager.keys[KeyEvent.VK_7];}
    public static boolean eight() {return keyManager.keys[KeyEvent.VK_8];}
    public static boolean nine() {return keyManager.keys[KeyEvent.VK_9];}
    public static boolean zero() {return keyManager.keys[KeyEvent.VK_0];}

    public KeyManager() {
        keys = new boolean[256]; // Each item corresponds to a key
    }

    // Updates the item that corresponds to the pressed key in the boolean array
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}
