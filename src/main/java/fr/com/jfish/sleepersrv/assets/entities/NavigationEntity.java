/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jellyfish.sleepersrv.assets.entities;

import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.AsteroidLowPoly;
import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.camera.Camera;
import fr.com.jellyfish.sleepersrv.assets.mesh.Mesh;
import fr.com.jellyfish.sleepersrv.constants.FileConst;
import fr.com.jellyfish.sleepersrv.game.OpenGLGame;
import fr.com.jellyfish.sleepersrv.opengl.util.WavefrontMeshLoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;
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
public class NavigationEntity extends AbstractAsset {
    
    /**
     * Velocity constants.
     */
    public static final float STRAFF_THRUST_FACTOR = 1024;    
    public static final float VELOCITY_THRUST_FACTOR = 1024f;        
    public static final float MAX_LINEAR_VELOCITY = 1024f;
    
    private float scale;
    private final int positionVbo;
    private final int normalsVbo;
    private Mesh mesh;
    private final OpenGLGame game;
    private final Camera camera;
    private final FrustumIntersection frustumIntersection;
    private double tmpRotationVal = 0.1f;
    private static final float ROTATION_Y_VAL = 0.3f;
    
    public NavigationEntity(final OpenGLGame game, final Camera camera, final FrustumIntersection frustumIntersection,
        final String mdl) {
                
        try {
            this.createProg("mouvable.vs", "mouvable.fs");
        } catch (final IOException iOEx) {
            Logger.getLogger(AsteroidLowPoly.class.getName()).log(Level.SEVERE, null, iOEx);
        }
        
        this.game = game;
        this.camera = camera;
        this.frustumIntersection = frustumIntersection;

        this.scale = 0.6f;
        
        final WavefrontMeshLoader loader = new WavefrontMeshLoader();
        
        try {
            this.mesh = loader.loadMesh(String.format(FileConst.RES + FileConst.MDLS + "%s", mdl));
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

        glUseProgram(this.abs_prog);
        glBindBuffer(GL_ARRAY_BUFFER, positionVbo);
        glVertexPointer(3, GL_FLOAT, 0, 0);
        glEnableClientState(GL_NORMAL_ARRAY);
        glBindBuffer(GL_ARRAY_BUFFER, normalsVbo);
        glNormalPointer(GL_FLOAT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        float tmpx = (float) camera.rotation.positiveX(new Vector3f()).x - 1f;
        float tmpy = (float) camera.rotation.positiveY(new Vector3f()).y - 2f;
        float tmpz = (float) camera.rotation.positiveZ(new Vector3f()).z - 30f;

        if (frustumIntersection.testSphere(tmpx, tmpy, tmpz, scale)) {
                                    
            game.getViewMatrix().translation(tmpx, tmpy, tmpz);             
            game.getViewMatrix().rotate(camera.rotation);     
            game.getViewMatrix().scale(scale);
              
            glUniformMatrix4fv(this.abs_modelUniform, false, game.getViewMatrix().get(game.getMatrixBuffer()));
            glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices);
        }
        
        glDisableClientState(GL_NORMAL_ARRAY);
    }
        
    @Override
    public void update(final float dt) { 
        
        /* Rotation this mesh not working.
        tmpRotationVal = tmpRotationVal > 360f ? 0.1f : tmpRotationVal + ROTATION_Y_VAL;
        //game.getViewMatrix().rotateY((float) Math.toRadians(tmpRotationVal));       
        GL11.glRotatef((float) tmpRotationVal, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef((float) tmpRotationVal, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef((float) tmpRotationVal, 0.0f, 0.0f, 1.0f);      
        */
        
        glUseProgram(this.abs_prog);
        glUniformMatrix4fv(this.abs_viewUniform, false, game.getViewMatrix().get(game.getMatrixBuffer()));
        glUniformMatrix4fv(this.abs_projUniform, false, game.getProjMatrix().get(game.getMatrixBuffer())); 
        glUseProgram(0); 
    }
    
}
