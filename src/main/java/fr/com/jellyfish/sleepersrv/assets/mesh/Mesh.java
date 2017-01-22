/**
 * # sleepersrv, openGl & LWJGL3 + joml fun. LWJGL 3 3d workshop Maven project
 * with joml and LWJGL3 All credits www.lwjgl.org &
 * https://github.com/LWJGL/lwjgl3 All model credits
 * https://nasa3d.arc.nasa.gov/ JOML https://github.com/JOML-CI/JOML
 */
package fr.com.jellyfish.sleepersrv.assets.mesh;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thw
 */
public class Mesh {
    
    public ObjWavefrontData data;
    public FloatBuffer positions;
    public FloatBuffer normals;
    public int numVertices;
    public float boundingSphereRadius;
    public List<MeshObject> objects = new ArrayList<>();
    
}
