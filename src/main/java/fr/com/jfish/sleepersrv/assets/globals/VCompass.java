/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jellyfish.sleepersrv.assets.globals;

import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.camera.Camera;
import fr.com.jellyfish.sleepersrv.assets.mesh.Mesh;
import fr.com.jellyfish.sleepersrv.constants.FileConst;
import fr.com.jellyfish.sleepersrv.opengl.util.WavefrontMeshLoader;
import java.io.IOException;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NORMAL_ARRAY;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glLoadMatrixf;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glMultMatrixf;
import static org.lwjgl.opengl.GL11.glNormalPointer;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author thw
 */
public class VCompass extends AbstractAsset {

    private Mesh mesh;
    
    public void createMesh() throws IOException {
        WavefrontMeshLoader loader = new WavefrontMeshLoader();
        mesh = loader.loadMesh(FileConst.RES + FileConst.MDLS + "sphere.obj.zip");
    }
    
    public void render(final Matrix4f projMatrix, final FloatBuffer matrixBuffer, 
            final Matrix4f viewMatrix, final float maxLinearVel, final Camera camera) {
        
        glUseProgram(0);
        glEnable(GL_BLEND);
        glVertexPointer(3, GL_FLOAT, 0, mesh.positions);
        glEnableClientState(GL_NORMAL_ARRAY);
        glNormalPointer(GL_FLOAT, 0, mesh.normals);
        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadMatrixf(projMatrix.get(matrixBuffer));
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glLoadIdentity();
        glTranslatef(0, -1, -4);
        glMultMatrixf(viewMatrix.get(matrixBuffer));
        glScalef(0.3f, 0.3f, 0.3f);
        glColor4f(0.1f, 0.1f, 0.1f, 0.2f);
        glDisable(GL_DEPTH_TEST);
        glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices);
        glEnable(GL_DEPTH_TEST);
        glBegin(GL_LINES);
        glColor4f(1, 0, 0, 1);
        glVertex3f(0, 0, 0);
        glVertex3f(1, 0, 0);
        glColor4f(0, 1, 0, 1);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 1, 0);
        glColor4f(0, 0, 1, 1);
        glVertex3f(0, 0, 0);
        glVertex3f(0, 0, 1);
        glColor4f(1, 1, 1, 1);
        glVertex3f(0, 0, 0);
        glVertex3f(camera.linearVelocity.x / maxLinearVel, camera.linearVelocity.y / maxLinearVel, 
            camera.linearVelocity.z / maxLinearVel);
        glEnd();
        glPopMatrix();
        glMatrixMode(GL_PROJECTION);
        glPopMatrix();
        glMatrixMode(GL_MODELVIEW);
        glDisableClientState(GL_NORMAL_ARRAY);
        glDisable(GL_BLEND);    
    }
    
    @Override
    public void update(final float dt) { }
    
        
    @Override
    public void render() { 
        throw new UnsupportedOperationException();
    }
    
}
