package net.aufdemrand.denizen.events.entity;

import net.aufdemrand.denizen.BukkitScriptEntryData;
import net.aufdemrand.denizen.events.BukkitScriptEvent;
import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.NMSVersion;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dMaterial;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.ScriptEntryData;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.BlockIterator;

public class ProjectileHitsScriptEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // projectile hits block
    // projectile hits <material>
    // <projectile> hits block
    // <projectile> hits <material>
    //
    // @Regex ^on [^\s]+ hits [^\s]+$
    // @Switch in <area>
    //
    // @Triggers when a projectile hits a block.
    //
    // @Context
    // <context.projectile> returns the dEntity of the projectile.
    // <context.shooter> returns the dEntity of the shooter, if there is one.
    // <context.location> returns the dLocation of the block that was hit.
    //
    // -->

    // <--[event]
    // @Events
    // entity shoots block
    // entity shoots <material> (with <projectile>)
    // <entity> shoots block
    // <entity> shoots <material> (with <projectile>)
    //
    // @Regex ^on [^\s]+ shoots [^\s]+$
    // @Switch in <area>
    //
    // @Triggers when a projectile shot by an entity hits a block.
    //
    // @Context
    // <context.projectile> returns the dEntity of the projectile.
    // <context.shooter> returns the dEntity of the shooter, if there is one.
    // <context.location> returns the dLocation of the block that was hit.
    //
    // -->
    public ProjectileHitsScriptEvent() {
        instance = this;
    }

    public static ProjectileHitsScriptEvent instance;
    public dEntity projectile;
    public dEntity shooter;
    public dLocation location;
    private dMaterial material;
    public ProjectileHitEvent event;

    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String cmd = CoreUtilities.getXthArg(1, lower);
        return cmd.equals("hits") || cmd.equals("shoots");
    }

    @Override
    public boolean matches(ScriptPath path) {
        String cmd = path.eventArgLowerAt(1);
        String pTest = "";

        if (cmd.equals("hits")) {
            pTest = path.eventArgLowerAt(0);
        }
        else if (cmd.equals("shoots") && path.eventArgLowerAt(3).equals("with")) {
            pTest = path.eventArgLowerAt(4);
        }
        if (!pTest.isEmpty() && !pTest.equals("projectile") && !tryEntity(projectile, pTest)) {
            return false;
        }

        if (!tryMaterial(material, path.eventArgLowerAt(2))) {
            return false;
        }

        if (!runInCheck(path, location)) {
            return false;
        }
        return true;

    }


    @Override
    public String getName() {
        return "ProjectileHits";
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(shooter != null && shooter.isPlayer() ? dEntity.getPlayerFrom(event.getEntity()) : null,
                shooter != null && shooter.isCitizensNPC() ? dEntity.getNPCFrom(event.getEntity()) : null);
    }

    @Override
    public dObject getContext(String name) {
        if (name.equals("projectile")) {
            return projectile.getDenizenObject();
        }
        else if (name.equals("location")) {
            return location;
        }
        else if (name.equals("shooter") && shooter != null) {
            return shooter.getDenizenObject();
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onProjectileHits(ProjectileHitEvent event) {
        projectile = new dEntity(event.getEntity());
        if (projectile.getLocation() == null) {
            return; // No, I can't explain how or why this would ever happen... nonetheless, it appears it does happen sometimes.
        }

        if (Double.isNaN(projectile.getLocation().getDirection().normalize().getX())) {
            return; // I can't explain this one either. It also chooses to happen whenever it pleases.
        }

        Block block = null;
        if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_11_R1)) {
            block = event.getHitBlock();
        }
        else {
            try {
                BlockIterator bi = new BlockIterator(projectile.getLocation().getWorld(),
                        projectile.getLocation().toVector(), projectile.getLocation().getDirection().normalize(), 0, 4);
                while (bi.hasNext()) {
                    block = bi.next();
                    if (block.getType() == Material.AIR) {
                        break;
                    }
                }
            }
            catch (IllegalStateException ex) {
                // This happens because it can. Also not explainable whatsoever.
                // As this error happens on no fault of the user, display no error message... just cancel the event.
                return;
            }
        }

        if (block == null) {
            return;
        }
        material = new dMaterial(block);
        shooter = projectile.getShooter();
        location = new dLocation(block.getLocation());
        this.event = event;
        fire(event);
    }
}
