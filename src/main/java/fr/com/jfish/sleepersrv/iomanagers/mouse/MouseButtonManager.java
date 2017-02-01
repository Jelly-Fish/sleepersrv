/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jellyfish.sleepersrv.iomanagers.mouse;

import fr.com.jellyfish.sleepersrv.game.OpenGLGame;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 * @author thw
 */
public class MouseButtonManager extends GLFWMouseButtonCallback {

    final OpenGLGame game;
    
    public MouseButtonManager(final OpenGLGame game) {
        this.game = game;
    }    
    
    @Override
    public void invoke(long window, int button, int action, int mods) {
        
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            if (action == GLFW_PRESS) {
                game.leftMouseDown = true;
            } else if (action == GLFW_RELEASE) {
                game.leftMouseDown = false;
            }
        } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            if (action == GLFW_PRESS) {
                game.rightMouseDown = true;
            } else if (action == GLFW_RELEASE) {
                game.rightMouseDown = false;
            }
        }
    }

}
