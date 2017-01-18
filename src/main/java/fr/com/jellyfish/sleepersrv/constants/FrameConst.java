package fr.com.jellyfish.sleepersrv.constants;

/**
 * @author thw
 */
class FrameConst {
    
    /**
     * Nombre d'or.
     */
    public static final float NOMBRE_OR = 1.61803398875f;
    
    /**
     * Frame static width.
     * = 800
     */
    public static final int FRM_WIDTH = 800;
    
    /**
     * Frame static height.
     * = 600
     */
    public static final int FRM_HEIGHT = 712;

    /**
     * Width/height of custom library mouse cursor tile.
     */
    public static final int LIB_MOUSE_CURSOR_WH = 32;
    
    /**
     * Width/height of custom library mouse cursor tile.
     */
    public static final int LIB_MOUSE_CURSOR_POINTER_WH = 2;
    
    /**
     * View port extension that prevents wavefront meshs from vanishing when out
     * of normal viewport settings.
     * Will be used as a initialisable var in Frame variables :
     * @see FrameVars
     * @see OpenlGLGame loop method.
     */
    public static final int ADDITIONAL_VIEWPORT = 500;
    
    /**
     * Frame title.
     */
    public static final String FRAME_TITLE = "SleeperSrv";
    
    /**
     * Frame icon.
     */
    public static final String ICON = "ressources\\img\\FRAME\\frame.png";
        
}
