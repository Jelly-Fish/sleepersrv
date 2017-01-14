package fr.com.jellyfish.sleepersrv.assets.entities;

import fr.com.jellyfish.sleepersrv.assets.AbstractPool;
import fr.com.jellyfish.sleepersrv.game.OpenGLGame;
import java.nio.FloatBuffer;
import org.joml.FrustumIntersection;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author thw
 */
public class PlasmaPool extends AbstractPool {

    public static final int SPAWN_MS = 124;
    private static final int MAX_PLASMA = 1024;
    private static final float MAX_LIFE = 30.0f;
    private static final float DBL_MARGIN = 2.2f;
    private static final float PLASMA_VELOCITY = 300.0f;
    private static final float PLASMA_SIZE = 0.7f;
    
    private final FloatBuffer shotsVertices = BufferUtils.createFloatBuffer(6 * 6 * MAX_PLASMA);

    private final int default_projUniform;
    private final Vector3f tmpVector = new Vector3f();
    private final Vector3d newPosition = new Vector3d();
    private final OpenGLGame game;    
    private final FrustumIntersection frustumIntersection;
    private final int prog;
    
    private final Vector3d[] projectilePositions = new Vector3d[MAX_PLASMA];
    private final Vector4f[] projectileVelocities = new Vector4f[MAX_PLASMA];
    {
        for (int i = 0; i < projectilePositions.length; i++) {
            Vector3d projectilePosition = new Vector3d(0, 0, 0);
            projectilePositions[i] = projectilePosition;
            Vector4f projectileVelocity = new Vector4f(0, 0, 0, 0);
            projectileVelocities[i] = projectileVelocity;
        }
    }
    
    public PlasmaPool(final OpenGLGame game, final FrustumIntersection frustumIntersection,
        final int prog, final int default_projUniform) {
        
        this.game = game;
        this.frustumIntersection = frustumIntersection;
        this.prog = prog;
        this.default_projUniform = default_projUniform;
    }
    
    @Override
    public void render() {
        
        shotsVertices.clear();
        int num = 0;
        for (int i = 0; i < projectilePositions.length; i++) {
            
            Vector3d projectilePosition = projectilePositions[i];
            Vector4f projectileVelocity = projectileVelocities[i];
            
            if (projectileVelocity.w > 0.0f) {
                
                float x = (float) (projectilePosition.x - game.getCamera().position.x);
                float y = (float) (projectilePosition.y - game.getCamera().position.y);
                float z = (float) (projectilePosition.z - game.getCamera().position.z);
                
                if (frustumIntersection.testPoint(x, y, z)) {
                    float w = projectileVelocity.w;
                    game.getViewMatrix().transformPosition(game.getTempVect().set(x, y, z));
                    shotsVertices.put(game.getTempVect().x - PLASMA_SIZE).put(
                        game.getTempVect().y - PLASMA_SIZE).put(game.getTempVect().z).put(w).put(-1).put(-1);
                    shotsVertices.put(game.getTempVect().x + PLASMA_SIZE).put(
                        game.getTempVect().y - PLASMA_SIZE).put(game.getTempVect().z).put(w).put( 1).put(-1);
                    shotsVertices.put(game.getTempVect().x + PLASMA_SIZE).put(
                        game.getTempVect().y + PLASMA_SIZE).put(game.getTempVect().z).put(w).put( 1).put( 1);
                    shotsVertices.put(game.getTempVect().x + PLASMA_SIZE).put(
                        game.getTempVect().y + PLASMA_SIZE).put(game.getTempVect().z).put(w).put( 1).put( 1);
                    shotsVertices.put(game.getTempVect().x - PLASMA_SIZE).put(
                        game.getTempVect().y + PLASMA_SIZE).put(game.getTempVect().z).put(w).put(-1).put( 1);
                    shotsVertices.put(game.getTempVect().x - PLASMA_SIZE).put(
                        game.getTempVect().y - PLASMA_SIZE).put(game.getTempVect().z).put(w).put(-1).put(-1);
                    num++;
                }
            } else {
                break;
            }
        }
        
        shotsVertices.flip();
                
        if (num > 0) {
            
            glUseProgram(prog);
            glDepthMask(false);
            glEnable(GL_BLEND);
            glVertexPointer(4, GL_FLOAT, 6 * 4, shotsVertices);
            shotsVertices.position(4);
            glTexCoordPointer(2, GL_FLOAT, 6 * 4, shotsVertices);
            shotsVertices.position(0);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
            glDrawArrays(GL_TRIANGLES, 0, num * 6);
            glDisableClientState(GL_TEXTURE_COORD_ARRAY);
            glDisable(GL_BLEND);
            glDepthMask(true);
            glUseProgram(0);
        }
    }
    
    public void shoot() {
        
        boolean first = false;
        
        for (int i = 0; i < projectilePositions.length; i++) {
            
            Vector3d projectilePosition = projectilePositions[i];
            Vector4f projectileVelocity = projectileVelocities[i];
            game.getInvViewProjMatrix().transformProject(
                game.getTempVect().set(game.getMouseX(), -game.getMouseY(), -512.0f)).normalize();
            
            if (projectileVelocity.w <= 0.0f) {
                
                projectileVelocity.x = 
                    game.getCamera().linearVelocity.x + (game.getTempVect().x * PLASMA_VELOCITY);
                projectileVelocity.y = 
                    game.getCamera().linearVelocity.y + (game.getTempVect().y * PLASMA_VELOCITY);
                projectileVelocity.z = 
                    game.getCamera().linearVelocity.z + (game.getTempVect().z * PLASMA_VELOCITY);
                projectileVelocity.w = 0.01f;
                
                if (!first) {
                    projectilePosition.set(game.getCamera().right(this.tmpVector)
                        ).mul(DBL_MARGIN).add(game.getCamera().position);
                    first = true;
                } else {
                    projectilePosition.set(game.getCamera().right(this.tmpVector)
                        ).mul(-DBL_MARGIN).add(game.getCamera().position);
                    break;
                }
            }
        }
    }
    
    @Override
    public void update(final float dt) {
        
        /* Update the shot shader */
        glUseProgram(prog);
        glUniformMatrix4fv(default_projUniform, false, game.getMatrixBuffer()); 
        glUseProgram(0);
        
        for (int i = 0; i < projectilePositions.length; i++) {
            
            Vector4f projectileVelocity = projectileVelocities[i];
            if (projectileVelocity.w <= 0.0f) continue;
            projectileVelocity.w += dt;
            Vector3d projectilePosition = projectilePositions[i];            
            newPosition.set(projectileVelocity.x, projectileVelocity.y, projectileVelocity.z).mul(
                    dt).add(projectilePosition);
            
            if (projectileVelocity.w > MAX_LIFE) {
                projectileVelocity.w = 0.0f;
                continue;
            }            
           
            projectilePosition.set(newPosition);
        }
    }
    
}
