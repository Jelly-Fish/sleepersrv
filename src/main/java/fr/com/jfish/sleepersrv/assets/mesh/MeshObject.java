/**
 * # sleepersrv, openGl & LWJGL3 + joml fun. LWJGL 3 3d workshop Maven project
 * with joml and LWJGL3 All credits www.lwjgl.org &
 * https://github.com/LWJGL/lwjgl3 All model credits
 * https://nasa3d.arc.nasa.gov/ JOML https://github.com/JOML-CI/JOML
 */
package fr.com.jellyfish.sleepersrv.assets.mesh;

import org.joml.Vector3f;

/**
 * @author thw
 */
public class MeshObject {
        
    public String name;
    public int first;
    public int count;
    public Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    public Vector3f max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

    @Override
    public String toString() {
        return name + "(" + min + " " + max + ")";
    }
    
}
