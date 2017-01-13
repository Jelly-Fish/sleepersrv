/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.com.jellyfish.sleepersrv.assets.entities;

import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.camera.Camera;
import fr.com.jellyfish.sleepersrv.opengl.util.WavefrontMeshLoader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
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
public class Eye extends AbstractAsset {
    
    private float scale;
    private final int positionVbo;
    private final int normalsVbo;
    private WavefrontMeshLoader.Mesh mesh;
    private final Camera camera;
    private final FrustumIntersection frustumIntersection;
    private final Matrix4f modelMatrix;
    private final FloatBuffer matrixBuffer;
    private final int default_modelUniform;
    private final int defaultProg;
    
    public Eye(final Camera camera, final FrustumIntersection frustumIntersection, 
        final Matrix4f modelMatrix, final FloatBuffer matrixBuffer, final int default_modelUniform,
        final int defaultProg) {
        
        this.camera = camera;
        this.frustumIntersection = frustumIntersection;
        this.modelMatrix = modelMatrix;
        this.matrixBuffer = matrixBuffer;
        this.default_modelUniform = default_modelUniform;
        this.defaultProg = defaultProg;

        this.scale = 3f;
        
        final WavefrontMeshLoader loader = new WavefrontMeshLoader();
        
        try {
            this.mesh = loader.loadMesh("fr/com/jellyfish/mdls/eyeball.obj.zip");
        } catch (final IOException iOEx) {
            Logger.getLogger(Asteroid.class.getName()).log(Level.SEVERE, null, iOEx);
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

        float tmpx = (float) camera.rotation.positiveX(new Vector3f()).x - 1f;
        float tmpy = (float) camera.rotation.positiveY(new Vector3f()).y - 5f;
        float tmpz = (float) camera.rotation.positiveZ(new Vector3f()).z - 35f;

        if (frustumIntersection.testSphere(tmpx, tmpy, tmpz, scale)) {
            modelMatrix.translation(tmpx, tmpy, tmpz);            
            modelMatrix.rotate(camera.rotation);
            modelMatrix.scale(scale);
            glUniformMatrix4fv(default_modelUniform, false, modelMatrix.get(matrixBuffer));
            glDrawArrays(GL_TRIANGLES, 0, mesh.numVertices);
        }
        
        glDisableClientState(GL_NORMAL_ARRAY);
    }
    
}
