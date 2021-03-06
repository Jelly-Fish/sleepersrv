/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package fr.com.jfish.sleepersrv.opengl.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Writes a "struct" (i.e. a class with field members) to a {@link ByteBuffer} in std430 layout.
 * 
 * @author Kai Burjack
 */
public class Std430Writer {

    private static final Map<Class<?>, Integer> SIZES = new IdentityHashMap<>();
    static {
        SIZES.put(int.class, 1);
        SIZES.put(float.class, 1);
        SIZES.put(Vector3f.class, 3);
        SIZES.put(Vector4f.class, 4);
    }
    private static final Map<Class<?>, Integer> ALIGNMENTS = new IdentityHashMap<>();
    static {
        ALIGNMENTS.put(int.class, 1);
        ALIGNMENTS.put(float.class, 1);
        ALIGNMENTS.put(Vector3f.class, 4);
        ALIGNMENTS.put(Vector4f.class, 4);
    }

    /**
     * Write the given {@link List} of struct objects into the given {@link DynamicByteBuffer}, taking into account 
     * data type alignments and necessary paddings.
     * <p>
     * The GLSL types vec3 and vec4 are mapped to the JOML types {@link Vector3f} and {@link Vector4f}, respectively.
     * 
     * @param <T>
     * @param list
     *          the list containing the struct objects to write
     * @param clazz
     *          the class object representing the type of the objects in the list
     * @param bb
     *          the {@link DynamicByteBuffer} to write into
     */
    public static <T> void write(List<T> list, Class<T> clazz, DynamicByteBuffer bb) {
        Field[] fields = clazz.getDeclaredFields();
        int ints = 0;
        int[] paddings = new int[fields.length];
        int largestAlign = 0;
        /* Compute necessary padding after each field */
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            Class<?> t = f.getType();
            int len = 1;
            if (t.isArray()) {
                t = t.getComponentType();
                Member mem = f.getAnnotation(Member.class);
                len = mem.length();
            }
            int align = ALIGNMENTS.get(t);
            largestAlign = Math.max(largestAlign, align);
            if (i >= 1) {
                int neededPadding = (align - ints % align) % align;
                paddings[i - 1] = neededPadding;
                ints += neededPadding;
            }
            int size = SIZES.get(t) * len;
            ints += size;
        }
        /* Compute padding at the end of the struct */
        int endPadding = (largestAlign - ints % largestAlign) % largestAlign;
        paddings[fields.length - 1] = endPadding;
        /* Write list */
        for (T t : list) {
            for (int i = 0; i < fields.length; i++) {
                // Write field value
                Field f = fields[i];
                try {
                    writeField(f, t, bb);
                } catch (final IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException("Could not write struct field: " + f, e);
                }
                // Write padding
                int pad = paddings[i];
                for (int p = 0; p < pad; p++)
                    bb.putInt((byte) 0);
            }
        }
    }

    private static <T> void writeField(Field f, T obj, DynamicByteBuffer bb) throws IllegalArgumentException, IllegalAccessException {
        
        Class<?> t = f.getType();
        if (t == int.class)
            bb.putInt(f.getInt(obj));
        else if (t == float.class)
            bb.putFloat(f.getFloat(obj));
        else if (t == Vector3f.class) {
            Vector3f v = (Vector3f) f.get(obj);
            bb.putFloat(v.x).putFloat(v.y).putFloat(v.z);
        } else if (t.isArray()) {
            Object arr = f.get(obj);
            int len = Array.getLength(arr);
            Class<?> ct = t.getComponentType();
            for (int i = 0; i < len; i++) {
                if (ct == int.class)
                    bb.putInt(Array.getInt(arr, i));
                else
                    throw new UnsupportedOperationException("NYI");
            }
        } else {
            throw new UnsupportedOperationException("NYI");
        }
    }

}
