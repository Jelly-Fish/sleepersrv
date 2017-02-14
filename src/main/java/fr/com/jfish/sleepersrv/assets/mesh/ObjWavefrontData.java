/**
 * # sleepersrv, openGl & LWJGL3 + joml fun. LWJGL 3 3d workshop Maven project
 * with joml and LWJGL3 All credits www.lwjgl.org &
 * https://github.com/LWJGL/lwjgl3 All model credits
 * https://nasa3d.arc.nasa.gov/ JOML https://github.com/JOML-CI/JOML
 */
package fr.com.jfish.sleepersrv.assets.mesh;

/**
 * @author thw
 */
public class ObjWavefrontData {
    
    public int numberOfVertices;
    public int numberOfFaces;
    public int numberOfNormals;
    public float xCenter;
    public float yCenter;
    public float zCenter;
    public float minX; 
    public float minY;
    public float minZ;
    public float maxX; 
    public float maxY;
    public float maxZ;
    
    /**
     * 
     * @param minX minimum vertex in wavefront file.
     * @param minY .
     * @param minZ .
     * @param maxX maximum vertex in wavefront file.
     * @param maxY .
     * @param maxZ .
     * Define xyz center point.
     */
    public void updataData(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        
        xCenter = minX + ((maxX - minX) / 2f);
        yCenter = minY + ((maxY - minY) / 2f); 
        zCenter = minZ + ((maxZ - minZ) / 2f);
    }
    
    @Override
    public String toString() {
        return String.format(
            "\n------------------\nmixX: %f\nminY: %f\nminZ: %f\nmaxX: %f\nmaxY: %f\nmaxZ: %f\nxCenter: %f\nyCenter: %f\nzCenter: %f", 
            minX, minY, minZ, maxX, maxY, maxZ, xCenter, yCenter, zCenter);
    }
    
}
