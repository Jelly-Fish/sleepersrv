package fr.com.jellyfish.sleepersrv.assets.camera;

import fr.com.jellyfish.sleepersrv.assets.AbstractAsset;
import fr.com.jellyfish.sleepersrv.assets.entities.NavigationEntity;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * @author thw
 */
public class Camera extends AbstractAsset {

    public Vector3f linearAccelaration = new Vector3f();
    public Vector3f linearVelocity = new Vector3f();
    float linearDamping = 0.08f; // linear speed damping.
    public Vector3f angularAccelaration = new Vector3f();
    public Vector3f angularVelocity = new Vector3f();
    float angularDamping = 0.5f; // angular speed damping.

    public Vector3d position = new Vector3d(0, 0, 0);
    public Quaternionf rotation = new Quaternionf();

    @Override
    public void update(final float dt) {
        
        // update linear velocity based on linear acceleration
        linearVelocity.fma(dt, linearAccelaration);
        // update angular velocity based on angular acceleration
        angularVelocity.fma(dt, angularAccelaration);
        // update the rotation based on the angular velocity
        rotation.integrate(dt, angularVelocity.x, angularVelocity.y, angularVelocity.z);
        angularVelocity.mul(1.0f - angularDamping * dt);
        // update position based on linear velocity
        position.fma(dt, linearVelocity);
        linearVelocity.mul(1.0f - linearDamping * dt);
    }
    
    public void freeze() {        
        // Freeze cam mvt.
        angularAccelaration.x = 0f;
        angularAccelaration.y = 0f;
        angularAccelaration.z = 0f;
        angularVelocity.x = 0f;
        angularVelocity.y = 0f;
        angularVelocity.z = 0f;        
    }    
    
    public void focusMdl(final NavigationEntity sphere) {          
        // re-center on main mdl. Param0 = main mdl.
        this.freeze();
        rotation = new Quaternionf();
    }    

    public Vector3f right(Vector3f dest) {
        return rotation.positiveX(dest);
    }

    public Vector3f up(Vector3f dest) {
        return rotation.positiveY(dest);
    }

    public Vector3f forward(Vector3f dest) {
        return rotation.positiveZ(dest).negate();
    }

    @Override
    public void render() { }

}
