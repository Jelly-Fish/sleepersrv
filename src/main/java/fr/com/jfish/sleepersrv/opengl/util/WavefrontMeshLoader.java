/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package fr.com.jfish.sleepersrv.opengl.util;

import fr.com.jfish.sleepersrv.assets.mesh.Mesh;
import fr.com.jfish.sleepersrv.assets.mesh.MeshObject;
import fr.com.jfish.sleepersrv.assets.mesh.ObjWavefrontData;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;

import org.lwjgl.BufferUtils;

import org.joml.Vector3f;

/**
 * A simple Wavefront obj file loader.
 * <p>
 * Does not load material files.
 * 
 * @author Kai Burjack
 */
public class WavefrontMeshLoader {

    private boolean fourComponentPosition;

    public WavefrontMeshLoader() { }

    public boolean isFourComponentPosition() {
        return fourComponentPosition;
    }

    public void setFourComponentPosition(boolean fourComponentPosition) {
        this.fourComponentPosition = fourComponentPosition;
    }

    private ObjWavefrontData getObjData(final BufferedReader reader) throws IOException {
        
        boolean initialized = false;
        String vLine[] = null;
        float x, y, z, minX = 0f, minY = 0f, minZ = 0f, maxX = 0f, maxY = 0f, maxZ = 0f;
        String line = StringUtils.EMPTY;
        ObjWavefrontData objData = new ObjWavefrontData();
        
        while (true) {
            
            line = reader.readLine();
            if (line == null) break;
            
            if (line.startsWith("v ")) {   
                
                vLine = line.split(" +");
                x = Float.parseFloat(vLine[1]);
                y = Float.parseFloat(vLine[2]);
                z = Float.parseFloat(vLine[3]);
                
                if (!initialized) {
                    initialized = true;
                    minX = x; maxX = x;
                    minY = y; maxY = y;
                    minZ = z; maxZ = z;
                } else {                    
                    minX = x < minX ? x : minX;
                    maxX = x > maxX ? x : maxX;
                    minY = y < minY ? y : minY;
                    maxY = y > maxY ? y : maxY;
                    minZ = z < minZ ? z : minZ;
                    maxZ = z > maxZ ? z : maxZ;
                }
                
                objData.numberOfVertices++;     
                
            } else if (line.startsWith("f ")) {
                objData.numberOfFaces++;
            } else if (line.startsWith("vn ")) {
                objData.numberOfNormals++;
            }
        }

        objData.updataData(minX, minY, minZ, maxX, maxY, maxZ);
        
        return objData;
    }

    private byte[] readSingleFileZip(String zipResource) throws IOException {
        
        ByteArrayOutputStream baos;
        try (ZipInputStream zipStream = new ZipInputStream(WavefrontMeshLoader.class.getClassLoader().getResourceAsStream(
                zipResource))) {
            zipStream.getNextEntry();
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = zipStream.read(buffer)) > 0) {
                baos.write(buffer, 0, read);
            }
        }
        
        return baos.toByteArray();
    }

    public Mesh loadMesh(String resource) throws IOException {
        
        byte[] arr = readSingleFileZip(resource);
        ObjWavefrontData meshData = getObjData(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(arr))));

        // Allocate buffers for all vertices/normal
        ByteBuffer positionByteBuffer = BufferUtils.createByteBuffer(3 * meshData.numberOfVertices * 4);
        ByteBuffer normalByteBuffer = BufferUtils.createByteBuffer(3 * meshData.numberOfNormals * 4);
        FloatBuffer positions = positionByteBuffer.asFloatBuffer();
        FloatBuffer normals = normalByteBuffer.asFloatBuffer();

        // Allocate buffers for the actual face vertices/normals
        ByteBuffer positionDataByteBuffer = BufferUtils.createByteBuffer((fourComponentPosition ? 4 : 3) * 3 * meshData.numberOfFaces * 4);
        ByteBuffer normalDataByteBuffer = BufferUtils.createByteBuffer(3 * 3 * meshData.numberOfFaces * 4);
        FloatBuffer positionData = positionDataByteBuffer.asFloatBuffer();
        FloatBuffer normalData = normalDataByteBuffer.asFloatBuffer();

        final Mesh mesh = new Mesh();
        MeshObject object = null;

        float minX = 1E38f, minY = 1E38f, minZ = 1E38f;
        float maxX = -1E38f, maxY = -1E38f, maxZ = -1E38f;
        String[] fs = null, f1 = null, f2 = null, f3 = null, ns = null, vs = null;
        String name = StringUtils.EMPTY;
        int v1, v2, v3, n1, n2, n3;
        float ver1X, ver1Y, ver1Z, x, y, z, ver2X, ver2Y, ver2Z, ver3X, ver3Y, ver3Z;
        float norm1X, norm1Y, norm1Z, norm2X, norm2Y, norm2Z, norm3X, norm3Y, norm3Z;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(arr)));
        String line;
        int faceIndex = 0;
        Vector3f tmp = new Vector3f();
        
        while ((line = reader.readLine()) != null) {
            
            if (line.startsWith("o ")) {
                
                name = line.substring(2);
                object = new MeshObject();
                object.name = name;
                object.first = faceIndex;
                mesh.objects.add(object);
                
            } else if (line.startsWith("vn ")) {
                
                ns = line.split(" +");
                x = Float.parseFloat(ns[1]);
                y = Float.parseFloat(ns[2]);
                z = Float.parseFloat(ns[3]);
                normals.put(x).put(y).put(z);
                
            } else if (line.startsWith("v ")) {
                
                vs = line.split(" +");
                x = Float.parseFloat(vs[1]);
                y = Float.parseFloat(vs[2]);
                z = Float.parseFloat(vs[3]);
                positions.put(x).put(y).put(z);
                
            } else if (line.startsWith("f")) {

                fs = line.split(" +");
                f1 = fs[1].split("/");
                f2 = fs[2].split("/");
                f3 = fs[3].split("/");

                v1 = Integer.parseInt(f1[0]);
                v2 = Integer.parseInt(f2[0]);
                v3 = Integer.parseInt(f3[0]);
                n1 = Integer.parseInt(f1[2]);
                n2 = Integer.parseInt(f2[2]);
                n3 = Integer.parseInt(f3[2]);
                ver1X = positions.get(3 * (v1 - 1) + 0);
                ver1Y = positions.get(3 * (v1 - 1) + 1);
                ver1Z = positions.get(3 * (v1 - 1) + 2);
                minX = minX < ver1X ? minX : ver1X;
                minY = minY < ver1Y ? minY : ver1Y;
                minZ = minZ < ver1Z ? minZ : ver1Z;
                maxX = maxX > ver1X ? maxX : ver1X;
                maxY = maxY > ver1Y ? maxY : ver1Y;
                maxZ = maxZ > ver1Z ? maxZ : ver1Z;
                tmp.set(ver1X, ver1Y, ver1Z);
                
                if (object != null) {
                    object.min.min(tmp);
                    object.max.max(tmp);
                }
                
                ver2X = positions.get(3 * (v2 - 1) + 0);
                ver2Y = positions.get(3 * (v2 - 1) + 1);
                ver2Z = positions.get(3 * (v2 - 1) + 2);
                minX = minX < ver2X ? minX : ver2X;
                minY = minY < ver2Y ? minY : ver2Y;
                minZ = minZ < ver2Z ? minZ : ver2Z;
                maxX = maxX > ver2X ? maxX : ver2X;
                maxY = maxY > ver2Y ? maxY : ver2Y;
                maxZ = maxZ > ver2Z ? maxZ : ver2Z;
                tmp.set(ver2X, ver2Y, ver2Z);
                
                if (object != null) {
                    object.min.min(tmp);
                    object.max.max(tmp);
                }
                
                ver3X = positions.get(3 * (v3 - 1) + 0);
                ver3Y = positions.get(3 * (v3 - 1) + 1);
                ver3Z = positions.get(3 * (v3 - 1) + 2);
                minX = minX < ver3X ? minX : ver3X;
                minY = minY < ver3Y ? minY : ver3Y;
                minZ = minZ < ver3Z ? minZ : ver3Z;
                maxX = maxX > ver3X ? maxX : ver3X;
                maxY = maxY > ver3Y ? maxY : ver3Y;
                maxZ = maxZ > ver3Z ? maxZ : ver3Z;
                tmp.set(ver3X, ver3Y, ver3Z);
                
                if (object != null) {
                    object.min.min(tmp);
                    object.max.max(tmp);
                }
                
                positionData.put(ver1X).put(ver1Y).put(ver1Z);
                if (fourComponentPosition) {
                    positionData.put(1.0f);
                }
                
                positionData.put(ver2X).put(ver2Y).put(ver2Z);
                if (fourComponentPosition) {
                    positionData.put(1.0f);
                }
                
                positionData.put(ver3X).put(ver3Y).put(ver3Z);
                if (fourComponentPosition) {
                    positionData.put(1.0f);
                }
                
                norm1X = normals.get(3 * (n1 - 1) + 0);
                norm1Y = normals.get(3 * (n1 - 1) + 1);
                norm1Z = normals.get(3 * (n1 - 1) + 2);
                norm2X = normals.get(3 * (n2 - 1) + 0);
                norm2Y = normals.get(3 * (n2 - 1) + 1);
                norm2Z = normals.get(3 * (n2 - 1) + 2);
                norm3X = normals.get(3 * (n3 - 1) + 0);
                norm3Y = normals.get(3 * (n3 - 1) + 1);
                norm3Z = normals.get(3 * (n3 - 1) + 2);
                normalData.put(norm1X).put(norm1Y).put(norm1Z);
                normalData.put(norm2X).put(norm2Y).put(norm2Z);
                normalData.put(norm3X).put(norm3Y).put(norm3Z);
                faceIndex++;
                
                if (object != null) {
                    object.count++;
                }
            }
        }
        
        if (mesh.objects.isEmpty()) {
            object = new MeshObject();
            object.count = faceIndex;
            mesh.objects.add(object);
        }
        
        positionData.flip();
        normalData.flip();
        mesh.boundingSphereRadius = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)) * 0.5f;
        mesh.positions = positionData;
        mesh.normals = normalData;
        mesh.numVertices = positionData.limit() / (fourComponentPosition ? 4 : 3);
        
        mesh.data = meshData;
        
        return mesh;
    }
}
