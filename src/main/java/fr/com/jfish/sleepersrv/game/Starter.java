/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jfish.sleepersrv.game;

/**
 *
 * @author thw
 */
public class Starter {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.out.println("Use -fullscreen for fullscreen mode");
        if (args.length > 0) appendArgs(args);
        final OpenGLGame game = new OpenGLGame();
        game.run();
        System.exit(0);
    }
    
    private static void appendArgs(final String[] args) {
        // Deal with launch args.
    }
    
}
