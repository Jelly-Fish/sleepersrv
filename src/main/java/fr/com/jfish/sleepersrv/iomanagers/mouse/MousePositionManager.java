/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jellyfish.sleepersrv.iomanagers.mouse;

import fr.com.jellyfish.sleepersrv.constants.FrameVars;
import fr.com.jellyfish.sleepersrv.game.OpenGLGame;
import org.lwjgl.glfw.GLFWCursorPosCallback;

/**
 * @author thw
 */
public class MousePositionManager extends GLFWCursorPosCallback {

    final OpenGLGame game;
    
    public MousePositionManager(final OpenGLGame game) {
        this.game = game;
    }
    
    @Override
    public void invoke(long window, double xpos, double ypos) {
        float normX = (float) ((xpos - FrameVars.V_WIDTH / 2.0) / FrameVars.V_WIDTH * 2.0);
        float normY = (float) ((ypos - FrameVars.V_HEIGHT / 2.0) / FrameVars.V_HEIGHT * 2.0);
        game.setMouseX(Math.max(-FrameVars.V_WIDTH / 2.0f, Math.min(FrameVars.V_WIDTH / 2.0f, normX)));
        game.setMouseY(Math.max(-FrameVars.V_HEIGHT / 2.0f, Math.min(FrameVars.V_HEIGHT / 2.0f, normY)));
    }  
    
}
