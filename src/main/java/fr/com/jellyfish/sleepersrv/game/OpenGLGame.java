package fr.com.jellyfish.sleepersrv.game;

import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.AbstractPool;
import fr.com.jellyfish.sleepersrv.assets.camera.Camera;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.BlockIsland;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.GeoGraphos;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.Golevka;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.Hw1;
import fr.com.jellyfish.sleepersrv.assets.entities.PlasmaPool;
import fr.com.jellyfish.sleepersrv.assets.entities.NavigationEntity;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.GolevkaRand;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.Toutatis;
import fr.com.jellyfish.sleepersrv.assets.entities.asteroids.Vesta;
import fr.com.jellyfish.sleepersrv.assets.globals.Cubemap;
import fr.com.jellyfish.sleepersrv.assets.globals.VCompass;
import fr.com.jellyfish.sleepersrv.constants.FrameVars;
import fr.com.jellyfish.sleepersrv.iomanagers.keyboard.KeyBoardManager;
import fr.com.jellyfish.sleepersrv.iomanagers.mouse.MouseButtonManager;
import fr.com.jellyfish.sleepersrv.iomanagers.mouse.MousePositionManager;
import fr.com.jellyfish.sleepersrv.opengl.util.ProgUtils;
import fr.com.jellyfish.sleepersrv.opengl.util.ShaderUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import org.lwjgl.system.Callback;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author thw
 */
public class OpenGLGame {

    private GLCapabilities glCapabilities;
    private final Camera camera = new Camera();
    private final Cubemap cubeMap = new Cubemap();
    private final VCompass vCompass = new VCompass();
    private final Map<String, AbstractAsset> assets = new HashMap<>();
    private final Map<String, AbstractPool> pools = new HashMap<>();
    private long window;

    private int defaultProg;
    private int default_viewUniform;
    private int default_projUniform;
    private int default_modelUniform;
    
    private int plasma_projUniform;
    private long lastShotTime = 0L;
    
    public boolean leftMouseDown = false;
    public boolean rightMouseDown = false;

    private float mouseX = 0.0f;
    private float mouseY = 0.0f;
    private long lastTime = System.nanoTime();

    private final Vector3f tempVect = new Vector3f();
    private final Matrix4f projMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f viewProjMatrix = new Matrix4f();
    private final Matrix4f invViewMatrix = new Matrix4f();
    private final Matrix4f invViewProjMatrix = new Matrix4f();
    private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    private KeyBoardManager keyCallback;
    private MousePositionManager mousePosCallBack;    
    private GLFWMouseButtonCallback buttonCallback;
    private Callback debugProc;
    
    private float dt;

    void init() throws IOException {

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        window = glfwCreateWindow(FrameVars.V_WIDTH, FrameVars.V_HEIGHT, "SleeperSrv", 0L, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - FrameVars.V_WIDTH) / 2,
            (vidmode.height() - FrameVars.V_HEIGHT) / 2
        );
        
        glfwSetCursor(window, glfwCreateStandardCursor(GLFW_HAND_CURSOR));

        glfwSetKeyCallback(window, keyCallback = new KeyBoardManager());
        glfwSetCursorPosCallback(window, mousePosCallBack = new MousePositionManager(this));        
        glfwSetMouseButtonCallback(window, buttonCallback = new MouseButtonManager(this));

        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);

        IntBuffer framebufferSize = BufferUtils.createIntBuffer(2);
        nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);

        glCapabilities = GL.createCapabilities();
        if (!glCapabilities.OpenGL20) {
            throw new AssertionError("This demo requires OpenGL 2.0.");
        }

        debugProc = GLUtil.setupDebugMessageCallback();

        this.cubeMap.createCubemapTexture(glCapabilities);
        this.cubeMap.createFullScreenQuad();
        this.cubeMap.createCubemapProg();
        this.defaultProg = this.createDefaultProg();        
        this.vCompass.createMesh();        
        this.initEntities();
        
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
    }
    
    void run() {

        try {

            init();
            loop();
            if (debugProc != null) debugProc.free();
            mousePosCallBack.free();            
            keyCallback.free();           
            buttonCallback.free();            
            glfwDestroyWindow(window);

        } catch (final Exception eX) {
            Logger.getLogger(OpenGLGame.class.getName()).log(Level.SEVERE, null, eX);
        } finally {
            glfwTerminate();
        }
    }
    
    void loop() {

        long thisTime = 0L;
        
        while (!glfwWindowShouldClose(window)) {

            glfwPollEvents();
            glViewport(-FrameVars.V_WIDTH, -FrameVars.V_HEIGHT, 
                FrameVars.ADD_VIEWPORT_WIDTH, FrameVars.ADD_VIEWPORT_HEIGHT);

            thisTime = System.nanoTime();
            dt = (thisTime - lastTime) / 1E9f;
            lastTime = thisTime;

            update(dt);            
            updateControls();
            render();
            glfwSwapBuffers(window);
        }
    }
    
    void render() {
        
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        this.cubeMap.render();
        this.vCompass.render(projMatrix, matrixBuffer, viewMatrix, NavigationEntity.MAX_LINEAR_VELOCITY, camera);
        
        for (AbstractPool pool : pools.values()) pool.render();
        for (AbstractAsset asset : assets.values()) asset.render();      
    }
    
    void update(final float dt) {

        projMatrix.setPerspective((float) Math.toRadians(40.0f),
                (float) FrameVars.V_WIDTH / FrameVars.V_HEIGHT, 1f, 500000.0f);
        viewMatrix.set(camera.rotation).invert(invViewMatrix);
        viewProjMatrix.set(projMatrix).mul(viewMatrix).invert(invViewProjMatrix);
        frustumIntersection.set(viewProjMatrix);
        
        /* Update the background shader */
        glUseProgram(this.cubeMap.cubemapProgram);
        glUniformMatrix4fv(this.cubeMap.cubemap_invViewProjUniform, false, invViewProjMatrix.get(matrixBuffer));

        /* Update the default shader */
        glUseProgram(defaultProg);
        glUniformMatrix4fv(default_viewUniform, false, viewMatrix.get(matrixBuffer));
        glUniformMatrix4fv(default_projUniform, false, projMatrix.get(matrixBuffer));
        
        for (AbstractPool pool : pools.values()) pool.update(dt);        
        for (AbstractAsset asset : assets.values()) asset.update(dt);
        
    }
    
    void updateControls() {

        camera.linearAccelaration.zero();
        float rotZ = 0.0f;
        
        if (keyCallback.kDown[GLFW_KEY_O]) {
            camera.linearAccelaration.fma(NavigationEntity.VELOCITY_THRUST_FACTOR, camera.forward(tempVect));
        }
        
        if (keyCallback.kDown[GLFW_KEY_L]) {
            camera.linearAccelaration.fma(-NavigationEntity.VELOCITY_THRUST_FACTOR, camera.forward(tempVect));
        }
        
        if (keyCallback.kDown[GLFW_KEY_RIGHT]) {
            camera.linearAccelaration.fma(NavigationEntity.STRAFF_THRUST_FACTOR, camera.right(tempVect));
        }
        
        if (keyCallback.kDown[GLFW_KEY_LEFT]) {
            camera.linearAccelaration.fma(-NavigationEntity.STRAFF_THRUST_FACTOR, camera.right(tempVect));
        }
        
        if (keyCallback.kDown[GLFW_KEY_K]) {
            rotZ = -1.0f;
        }
        
        if (keyCallback.kDown[GLFW_KEY_J]) {
            rotZ = +1.0f;
        }
        
        if (keyCallback.kDown[GLFW_KEY_UP]) {
            camera.linearAccelaration.fma(NavigationEntity.STRAFF_THRUST_FACTOR, camera.up(tempVect));
        }
        
        if (keyCallback.kDown[GLFW_KEY_DOWN]) {
            camera.linearAccelaration.fma(-NavigationEntity.STRAFF_THRUST_FACTOR, camera.up(tempVect));
        }

        if (keyCallback.kDown[GLFW_KEY_SPACE] && (lastTime - lastShotTime >= 1E6 * PlasmaPool.SPAWN_MS)) {
            ((PlasmaPool) this.pools.get(PlasmaPool.class.getName())).shoot();
            lastShotTime = lastTime;
        }  
        
        if (keyCallback.kDown[GLFW_KEY_P]) camera.freeze();        
        if (keyCallback.kDown[GLFW_KEY_ENTER]) {
            camera.focusMdl((NavigationEntity) assets.get(NavigationEntity.class.getName()));
        } 
        
        if (rightMouseDown) {
            camera.angularAccelaration.set(2.0f * mouseY * mouseY * mouseY, 2.0f * mouseX * mouseX * mouseX, rotZ);            
        } else if (!rightMouseDown) {
            camera.angularAccelaration.set(0, 0, rotZ);
        }
        
        double linearVelAbs = camera.linearVelocity.length();
        if (linearVelAbs > NavigationEntity.MAX_LINEAR_VELOCITY) {
            camera.linearVelocity.normalize().mul(NavigationEntity.MAX_LINEAR_VELOCITY);
        }        
        
    }
    
    private void initEntities() throws IOException {

        for (int i = 0; i < 200 ; ++i) {
            this.assets.put(GolevkaRand.class.getName() + i, 
                new GolevkaRand(this, camera, frustumIntersection, default_modelUniform, defaultProg));
        }
        
        this.assets.put(Camera.class.getName(), this.camera);
        this.assets.put(NavigationEntity.class.getName(), 
            new NavigationEntity(this, camera, frustumIntersection, "cassini.obj.zip"));
        
        this.assets.put(BlockIsland.class.getName(),
            new BlockIsland(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            0d, -100d, -8456d, 10f));
        this.assets.put(GeoGraphos.class.getName(),
            new GeoGraphos(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            0d, 0d, -2200d, 10f));
        this.assets.put(Golevka.class.getName(),
            new Golevka(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            0d, 150d, -1200d, 10f));
        this.assets.put(Hw1.class.getName(),
            new Hw1(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            0d, 15d, -3494d, 50f));
        this.assets.put(Toutatis.class.getName(),
            new Toutatis(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            19d, -58d, -2694d, 20f));
        this.assets.put(Vesta.class.getName(),
            new Vesta(this, camera, frustumIntersection, default_modelUniform, defaultProg,
            0d, 0d, -16676d, 10f)); 
        this.pools.put(PlasmaPool.class.getName(), new PlasmaPool(this, 
            this.frustumIntersection, this.createPlasmaBallProg(), plasma_projUniform));
    }
    
    private int createDefaultProg() throws IOException {
        
        int vshader = ShaderUtils.createShader("fr/com/jellyfish/shader/default_shader/default.vs", GL_VERTEX_SHADER);
        int fshader = ShaderUtils.createShader("fr/com/jellyfish/shader/default_shader/default.fs", GL_FRAGMENT_SHADER);
        int prog = ProgUtils.createProgram(vshader, fshader);
        glUseProgram(prog);
        default_viewUniform = glGetUniformLocation(prog, "view");
        default_projUniform = glGetUniformLocation(prog, "proj");
        default_modelUniform = glGetUniformLocation(prog, "model");
        glUseProgram(0); 
        
        return prog;
    }
       
    private int createPlasmaBallProg() throws IOException {
        
        int vshader = ShaderUtils.createShader("fr/com/jellyfish/shader/shot.vs", GL_VERTEX_SHADER);
        int fshader = ShaderUtils.createShader("fr/com/jellyfish/shader/shot.fs", GL_FRAGMENT_SHADER);
        int prog = ProgUtils.createProgram(vshader, fshader);
        glUseProgram(prog);
        plasma_projUniform = glGetUniformLocation(prog, "proj");
        glUseProgram(0);

        return prog;
    }

    /* *********************************************************************** */
    /* ACCESSORS */
    
    public Camera getCamera() {
        return camera;
    }
    
    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
        
    public FloatBuffer getMatrixBuffer() {
        return matrixBuffer;
    }
    
    public float getDt() {
        return dt;
    }

    public float getMouseX() {
        return mouseX;
    }

    public void setMouseX(final float f) {
        this.mouseX = f;
    }
    
    public float getMouseY() {
        return mouseY;
    }
    
    public void setMouseY(final float f) {
        this.mouseY = f;
    }

    public Vector3f getTempVect() {
        return tempVect;
    }
        
    public Matrix4f getInvViewProjMatrix() {
        return invViewProjMatrix;
    }
    
}
