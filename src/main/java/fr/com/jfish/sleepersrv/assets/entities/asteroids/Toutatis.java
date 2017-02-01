/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jellyfish.sleepersrv.assets.entities.asteroids;

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
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
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
public class Toutatis extends AbstractAsset {
    
    private double x, y, z;
    private float scale;
    private final int positionVbo;
    private final int normalsVbo;
    private Mesh mesh;
    private final OpenGLGame game;
    private final Camera camera;
    private final FrustumIntersection frustumIntersection;
    private double rotationAng = 1f;
    private double rotationVel = 1f;
    
    public Vector3f linearAccelaration = new Vector3f();
    public Vector3f linearVelocity = new Vector3f();
    public Vector3d position = new Vector3d(0, 0, 0);
    public Quaternionf rotation = new Quaternionf();
    public Vector3f angularVelocity = new Vector3f();
    public Vector3f angularAccelaration = new Vector3f();
    
    public Toutatis(final OpenGLGame game, final Camera camera, final FrustumIntersection frustumIntersection,
        final double x, final double y, final double z, final float scale) {
        
        try {
            this.createProg("asteroid_shader/asteroid.vs", "asteroid_shader/asteroid.fs");
        } catch (final IOException iOEx) {
            Logger.getLogger(AsteroidLowPoly.class.getName()).log(Level.SEVERE, null, iOEx);
        }
        
        this.game = game;
        this.camera = camera;
        this.frustumIntersection = frustumIntersection;
        
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        
        try {
            this.mesh = new WavefrontMeshLoader().loadMesh(FileConst.RES + FileConst.MDLS + "toutatis.obj.zip");
        } catch (final IOException iOEx) {
            Logger.getLogger(AsteroidLowPoly.class.getName()).log(Level.SEVERE, null, iOEx);
        }
        
        this.positionVbo = glGenBuffers();        
        glBindBuffer(GL_ARRAY_BUFFER, this.positionVbo);
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
  
        float tmpx = (float) (x - camera.position.x);
        float tmpy = (float) (y - camera.position.y);
        float tmpz = (float) (z - camera.position.z);
        
        if (frustumIntersection.testSphere(tmpx, tmpy, tmpz, scale)) {
            
            game.getViewMatrix().translation(tmpx, tmpy, tmpz);  
            
            //mesh.objects
            // FIXEM : shader/lighting is not updated after rotations because cam is rotating
            // relativ to the mesh - the model is not rotating :
            // game.getViewMatrix().rotateY((float) Math.toRadians(++this.rotationAng));
            //game.getViewMatrix().rotateZ((float) Math.toRadians(this.rotationAng));  
            //game.getViewMatrix().rotateX((float) Math.toRadians(this.rotationAng));  
            
            /* obsolete rotation :
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glPushMatrix();
            // ===================================================================
            // Now, do the model transform. Remember that this has to 
            // be read "backwards":
            // THIRD STEP: Move the model back so that its center is
            // again at its original position
            GL11.glTranslatef((float) x, (float) y, (float) z);
            // SECOND STEP: Rotate the model about the origin (which now
            // is the center of the model)
            this.rotationAng += 4f;
            GL11.glRotatef((float) rotationAng, 0, 0, 1);
            // FIRST STEP: Translate the model so that its center is at the origin
            GL11.glTranslatef(-((float) x), -((float) y), -((float) z));
            // ===================================================================*/
            
            game.getViewMatrix().scale(scale);  
            glUniformMatrix4fv(this.abs_modelUniform, false, game.getViewMatrix().get(game.getMatrixBuffer()));
              
            glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices);
        }
        
        GL11.glPopMatrix();
        
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    @Override
    public void update(final float dt) { 
        
        /*
        //rotationAng = rotationAng > 360 ? 1f : rotationAng + rotationVel;
        /* Rotation stuff */
        //angularVelocity.y += (float) ++rotationAng * 10f;
        //rotationAng = rotationAng > 360 ? 1f : rotationAng + rotationVel;
        x += 0.5d;
        //z += 2.6d;
        
        /*
        // update linear velocity based on linear acceleration
        linearVelocity.fma(dt, linearAccelaration);
        // update angular velocity based on angular acceleration
        angularVelocity.fma(dt, angularAccelaration);
        // update the rotation based on the angular velocity
        rotation.integrate(dt, angularVelocity.x, angularVelocity.y, angularVelocity.z);
        // update position based on linear velocity
        position.fma(dt, linearVelocity);
        */
        
        glUseProgram(this.abs_prog);
        glUniformMatrix4fv(this.abs_viewUniform, false, game.getViewMatrix().get(game.getMatrixBuffer()));
        glUniformMatrix4fv(this.abs_projUniform, false, game.getProjMatrix().get(game.getMatrixBuffer())); 
        glUseProgram(0);

    }
    
}
