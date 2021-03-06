package net.aufdemrand.denizen.nms.helpers;

import net.aufdemrand.denizen.nms.abstracts.ParticleHelper;
import net.aufdemrand.denizen.nms.impl.Particle_v1_13_R2;
import net.aufdemrand.denizen.nms.interfaces.Particle;

public class ParticleHelper_v1_13_R2 extends ParticleHelper {

    @Override
    public void register(String name, Particle particle) {
        super.register(name, new Particle_v1_13_R2(particle.particle));
    }
}
