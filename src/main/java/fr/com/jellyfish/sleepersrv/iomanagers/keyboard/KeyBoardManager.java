package fr.com.jellyfish.sleepersrv.iomanagers.keyboard;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * @author thw
 */
public class KeyBoardManager extends GLFWKeyCallback {
    
    public final boolean[] kDown = new boolean[GLFW.GLFW_KEY_LAST];
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        
        if (key == GLFW_KEY_UNKNOWN) return;
        
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
        
        kDown[key] = (action == GLFW_PRESS || action == GLFW_REPEAT);   
        
        
    }
    
}