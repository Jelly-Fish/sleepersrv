/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.com.jellyfish.sleepersrv.game;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
