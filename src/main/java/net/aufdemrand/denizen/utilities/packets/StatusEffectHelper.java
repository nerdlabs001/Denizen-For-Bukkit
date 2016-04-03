package net.aufdemrand.denizen.utilities.packets;

import net.minecraft.server.v1_9_R1.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class StatusEffectHelper {

    public static PacketPlayOutEntityEffect getStatusPacket(LivingEntity ent, MobEffectList effectl, byte amplifier, int duration) {
        MobEffect effect = new MobEffect(effectl, duration, amplifier, true, true);
        PacketPlayOutEntityEffect packet = new PacketPlayOutEntityEffect(ent.getEntityId(), effect);
        return packet;
    }

    public static void setGlowEffect(Player toSee, LivingEntity toGlow, boolean shouldGlow) {
        PacketHelper.sendPacket(toSee, getStatusPacket(toGlow, MobEffects.GLOWING, (byte)1, shouldGlow ? 32760: 0));
    }
}