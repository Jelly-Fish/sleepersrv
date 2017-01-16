/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.com.jellyfish.sleepersrv.assets.entities.asteroids;

import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.camera.Camera;
import fr.com.jellyfish.sleepersrv.game.OpenGLGame;
import fr.com.jellyfish.sleepersrv.opengl.util.WavefrontMeshLoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.FrustumIntersection;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 *
 * @author thw
 */
public class Hw1 extends AbstractAsset {
    
    private double x, y, z;
    private float scale;
    private final int positionVbo;
    private final int normalsVbo;
    private WavefrontMeshLoader.Mesh mesh;
    private final OpenGLGame game;
    private final Camera camera;
    private final FrustumIntersection frustumIntersection;
    private final int default_modelUniform;
    private final int defaultProg;
    
    public Hw1(final OpenGLGame game, final Camera camera, final FrustumIntersection frustumIntersection,
        final int default_modelUniform, final int defaultProg, final double x, final double y, 
        final double z, final float scale) {
        
        this.game = game;
        this.camera = camera;
        this.frustumIntersection = frustumIntersection;
        this.default_modelUniform = default_modelUniform;
        this.defaultProg = defaultProg;
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        
        final WavefrontMeshLoader loader = new WavefrontMeshLoader();
        
        try {
            this.mesh = loader.loadMesh("fr/com/jellyfish/mdls/hw1.obj.zip");
        } catch (final IOException iOEx) {
            Logger.getLogger(AsteroidLowPoly.class.getName()).log(Level.SEVERE, null, iOEx);
        }
        
        this.positionVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glBufferData(GL_ARRAY_BUFFER, this.mesh.positions, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        this.normalsVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.normalsVbo);
        glBufferData(GL_ARRAY_BUFFER, this.mesh.normals, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    @Override
    public void render() {
    
        glUseProgram(defaultProg);
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, normalsVbo);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
  
        float tmpx = (float) (x - camera.position.x);
        float tmpy = (float) (y - camera.position.y);
        float tmpz = (float) (z - camera.position.z);
        if (frustumIntersection.testSphere(tmpx, tmpy, tmpz, scale)) {
            game.getViewMatrix().translation(tmpx, tmpy, tmpz);
            game.getViewMatrix().scale(scale);
            glUniformMatrix4fv(default_modelUniform, false, game.getViewMatrix().get(game.getMatrixBuffer()));
            glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices);
        }
        
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    @Override
    public void update(final float dt) { }
    
}
