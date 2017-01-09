package fr.com.jellyfish.sleepersrv.constants;

import java.io.Serializable;

/**
 * @author thw
 */
public class FrameVars implements Serializable {
    
    static {
        FrameVars.V_HEIGHT = FrameConst.FRM_HEIGHT;
        FrameVars.V_WIDTH = (int) (FrameVars.V_HEIGHT * FrameConst.NOMBRE_OR);
    };
    
    /**
     * Frame width.
     */
    public static Integer V_WIDTH;

    /**
     * Frame height = width * nombre d'or.
     */
    public static Integer V_HEIGHT;
    
}
