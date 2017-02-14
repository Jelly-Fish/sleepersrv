/**
 * # sleepersrv, openGl & LWJGL3 + joml fun.
 * LWJGL 3 3d workshop
 * Maven project with joml and LWJGL3
 * All credits www.lwjgl.org & https://github.com/LWJGL/lwjgl3
 * All model credits https://nasa3d.arc.nasa.gov/
 */

package fr.com.jfish.sleepersrv.assets;

import fr.com.jfish.sleepersrv.constants.FileConst;
import fr.com.jfish.sleepersrv.opengl.util.ProgUtils;
import fr.com.jfish.sleepersrv.opengl.util.ShaderUtils;
import java.io.IOException;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author thw
 */
public abstract class AbstractAsset {
    
    public int abs_prog;
    public int abs_viewUniform;
    public int abs_projUniform;
    public int abs_modelUniform;
    
    public abstract void render();
    public abstract void update(final float dt);
    
    public int createProg(final String vs, final String fs) throws IOException {
        
        int vshader = ShaderUtils.createShader(FileConst.RES + FileConst.SHD + vs, GL_VERTEX_SHADER);
        int fshader = ShaderUtils.createShader(FileConst.RES + FileConst.SHD + fs, GL_FRAGMENT_SHADER);
        abs_prog = ProgUtils.createProgram(vshader, fshader);
        glUseProgram(abs_prog);
        abs_viewUniform = glGetUniformLocation(abs_prog, "view");
        abs_projUniform = glGetUniformLocation(abs_prog, "proj");
        abs_modelUniform = glGetUniformLocation(abs_prog, "model");
        glUseProgram(0); 
        
        return abs_prog;
    }
    
}
