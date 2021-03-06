/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jfish.sleepersrv.constants;

import java.io.Serializable;

/**
 * @author thw
 */
public class FrameVars implements Serializable {
    
    static {
        FrameVars.V_HEIGHT = FrameConst.FRM_HEIGHT;
        FrameVars.V_WIDTH = (int) (FrameVars.V_HEIGHT * FrameConst.NOMBRE_OR);
        FrameVars.ADD_VIEWPORT_WIDTH = FrameVars.V_WIDTH * 3;
        FrameVars.ADD_VIEWPORT_HEIGHT = FrameVars.V_HEIGHT * 3;
    };
    
    /**
     * Frame width.
     */
    public static Integer V_WIDTH;

    /**
     * Frame height = width * nombre d'or.
     */
    public static Integer V_HEIGHT;
    
    /**
     * View port extension that prevents wavefront meshs from vanishing when out
     * of normal viewport settings.
     * Will be used as a initialisable var in Frame variables :
     * @see OpenlGLGame loop method.
     */
    public static int ADD_VIEWPORT_WIDTH;
    
    /**
     * View port extension that prevents wavefront meshs from vanishing when out
     * of normal viewport settings.
     * Will be used as a initialisable var in Frame variables :
     * @see OpenlGLGame loop method.
     */
    public static int ADD_VIEWPORT_HEIGHT;
    
}
