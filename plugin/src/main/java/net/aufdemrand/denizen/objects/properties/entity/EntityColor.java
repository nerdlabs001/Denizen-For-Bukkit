package net.aufdemrand.denizen.objects.properties.entity;

import net.aufdemrand.denizen.nms.NMSHandler;
import net.aufdemrand.denizen.nms.NMSVersion;
import net.aufdemrand.denizen.objects.dEntity;
import net.aufdemrand.denizen.utilities.debugging.dB;
import net.aufdemrand.denizen.utilities.entity.LlamaHelper;
import net.aufdemrand.denizen.utilities.entity.ParrotHelper;
import net.aufdemrand.denizen.utilities.entity.RabbitType;
import net.aufdemrand.denizen.utilities.entity.ShulkerHelper;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Mechanism;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.objects.properties.Property;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.DyeColor;
import org.bukkit.entity.*;

public class EntityColor implements Property {


    public static boolean describes(dObject entity) {
        if (!(entity instanceof dEntity)) {
            return false;
        }
        EntityType type = ((dEntity) entity).getBukkitEntityType();
        return type == EntityType.SHEEP ||
                type == EntityType.HORSE ||
                type == EntityType.WOLF ||
                type == EntityType.OCELOT ||
                type == EntityType.RABBIT ||
                (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_11_R1) && type == EntityType.LLAMA) ||
                (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_12_R1)
                        && (type == EntityType.PARROT || type == EntityType.SHULKER));
    }

    public static EntityColor getFrom(dObject entity) {
        if (!describes(entity)) {
            return null;
        }
        else {
            return new EntityColor((dEntity) entity);
        }
    }

    public static final String[] handledTags = new String[] {
            "color"
    };

    public static final String[] handledMechs = new String[] {
            "color"
    };


    ///////////////////
    // Instance Fields and Methods
    /////////////

    private EntityColor(dEntity entity) {
        colored = entity;
    }

    dEntity colored;

    private String getColor() {
        EntityType type = colored.getBukkitEntityType();

        if (type == EntityType.HORSE) {
            Horse horse = (Horse) colored.getBukkitEntity();
            return horse.getColor().name() + "|" + horse.getStyle().name() +
                    (NMSHandler.getVersion().isAtMost(NMSVersion.v1_10_R1) ? "|" + horse.getVariant().name() : "");
        }
        else if (type == EntityType.SHEEP) {
            return ((Sheep) colored.getBukkitEntity()).getColor().name();
        }
        else if (type == EntityType.WOLF) {
            return ((Wolf) colored.getBukkitEntity()).getCollarColor().name();
        }
        else if (type == EntityType.OCELOT) {
            return ((Ocelot) colored.getBukkitEntity()).getCatType().name();
        }
        else if (type == EntityType.RABBIT) {
            return ((Rabbit) colored.getBukkitEntity()).getRabbitType().name();
        }
        else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_11_R1) && type == EntityType.LLAMA) {
            return LlamaHelper.llamaColorName(colored);
        }
        else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_12_R1) && type == EntityType.PARROT) {
            return ParrotHelper.parrotColor(colored);
        }
        else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_12_R1) && type == EntityType.SHULKER) {
            return ShulkerHelper.getColor(colored).name();
        }
        else // Should never happen
        {
            return null;
        }
    }

    /////////
    // Property Methods
    ///////

    @Override
    public String getPropertyString() {
        return CoreUtilities.toLowerCase(getColor());
    }

    @Override
    public String getPropertyId() {
        return "color";
    }


    ///////////
    // dObject Attributes
    ////////

    // <--[language]
    // @name Horse Types
    // @group Properties
    // @description
    // This is a quick rundown of the styling information used to create a horse,
    // used for both <@link tag e@entity.color> and <@link mechanism e@entity.color>.
    //
    // The output/input is formatted as COLOR|STYLE(|VARIANT)
    // Where color is:
    // BLACK, BROWN, CHESTNUT, CREAMY, DARK_BROWN, GRAY, or WHITE.
    // and where style is:
    // WHITE, WHITE_DOTS, WHITEFIELD, BLACK_DOTS, or NONE.
    // and where variant is:
    // DONKEY, MULE, SKELETON_HORSE, UNDEAD_HORSE, or HORSE.
    //  NOTE: HORSE VARIANTS DEPRECATED SINCE 1.11, use spawn instead
    // -->

    // <--[language]
    // @name Rabbit Types
    // @group Properties
    // @description
    // Denizen includes its own user-friendly list of rabbit type names, instead
    // of relying on Bukkit names which did not exist at the time of writing.
    //
    // Types currently available:
    // BROWN, WHITE, BLACK, WHITE_SPLOTCHED, GOLD, SALT, KILLER.
    //
    // Note: The KILLER rabbit type is a hostile rabbit type. It will attempt to kill
    //       nearby players and wolves. Use at your own risk.
    // -->


    @Override
    public String getAttribute(Attribute attribute) {

        if (attribute == null) {
            return null;
        }

        // <--[tag]
        // @attribute <e@entity.color>
        // @returns Element
        // @mechanism dEntity.color
        // @group properties
        // @description
        // If the entity can have a color, returns the entity's color.
        // Currently, only Horse, Wolf, Ocelot, Sheep, Rabbit, Llama, and Parrot type entities can have a color.
        // For horses, the output is COLOR|STYLE(|VARIANT), see <@link language horse types>.
        //  NOTE: HORSE VARIANTS DEPRECATED SINCE 1.11, use spawn instead
        // For ocelots, the types are BLACK_CAT, RED_CAT, SIAMESE_CAT, or WILD_OCELOT.
        // For rabbit types, see <@link language rabbit types>.
        // For parrots, the types are BLUE, CYAN, GRAY, GREEN, or RED.
        // For llamas, the types are CREAMY, WHITE, BROWN, and GRAY.
        // For sheep, wolf, and shulker entities, see <@link url https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/DyeColor.html>
        // -->
        if (attribute.startsWith("color")) {
            return new Element(CoreUtilities.toLowerCase(getColor()))
                    .getAttribute(attribute.fulfill(1));
        }

        return null;
    }

    @Override
    public void adjust(Mechanism mechanism) {

        // <--[mechanism]
        // @object dEntity
        // @name color
        // @input Element
        // @description
        // Changes the entity's color.
        // Currently, only Horse, Wolf, Ocelot, Sheep, Rabbit, Llama, Parrot, and Shulker type entities can have a color.
        // For horses, the input is COLOR|STYLE(|VARIANT), see <@link language horse types>
        //  NOTE: HORSE VARIANTS DEPRECATED SINCE 1.11, use spawn instead
        // For ocelots, the types are BLACK_CAT, RED_CAT, SIAMESE_CAT, or WILD_OCELOT.
        // For rabbit types, see <@link language rabbit types>.
        // For parrots, the types are BLUE, CYAN, GRAY, GREEN, or RED.
        // For llamas, the types are CREAMY, WHITE, BROWN, and GRAY.
        // For sheep, wolf, and shulker entities, see <@link url https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/DyeColor.html>
        // @tags
        // <e@entity.color>
        // <e@entity.is_colorable>
        // -->

        if (mechanism.matches("color")) {
            EntityType type = colored.getBukkitEntityType();

            if (type == EntityType.HORSE) {
                dList horse_info = mechanism.valueAsType(dList.class);
                if (horse_info.size() > 0 && new Element(horse_info.get(0)).matchesEnum(Horse.Color.values())) {
                    ((Horse) colored.getBukkitEntity())
                            .setColor(Horse.Color.valueOf(horse_info.get(0).toUpperCase()));
                }
                if (horse_info.size() > 1 && new Element(horse_info.get(1)).matchesEnum(Horse.Style.values())) {
                    ((Horse) colored.getBukkitEntity())
                            .setStyle(Horse.Style.valueOf(horse_info.get(1).toUpperCase()));
                }
                if (NMSHandler.getVersion().isAtMost(NMSVersion.v1_10_R1)
                        && horse_info.size() > 2 && new Element(horse_info.get(2)).matchesEnum(Horse.Variant.values())) {
                    ((Horse) colored.getBukkitEntity())
                            .setVariant(Horse.Variant.valueOf(horse_info.get(2).toUpperCase()));
                }
            }
            else if (type == EntityType.SHEEP
                    && mechanism.getValue().matchesEnum(DyeColor.values())) {
                ((Sheep) colored.getBukkitEntity())
                        .setColor(DyeColor.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (type == EntityType.WOLF
                    && mechanism.getValue().matchesEnum(DyeColor.values())) {
                ((Wolf) colored.getBukkitEntity())
                        .setCollarColor(DyeColor.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (type == EntityType.OCELOT
                    && mechanism.getValue().matchesEnum(Ocelot.Type.values())) {
                ((Ocelot) colored.getBukkitEntity())
                        .setCatType(Ocelot.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (type == EntityType.RABBIT
                    && mechanism.getValue().matchesEnum(RabbitType.values())) {
                ((Rabbit) colored.getBukkitEntity()).setRabbitType(RabbitType.valueOf(mechanism.getValue().asString().toUpperCase()).getType());
            }
            else if (type == EntityType.RABBIT
                    && mechanism.getValue().matchesEnum(Rabbit.Type.values())) {
                ((Rabbit) colored.getBukkitEntity()).setRabbitType(Rabbit.Type.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_11_R1) && type == EntityType.LLAMA) {
                LlamaHelper.setLlamaColor(colored, mechanism.getValue().asString());
            }
            else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_12_R1) && type == EntityType.PARROT) {
                ParrotHelper.setParrotColor(colored, mechanism);
            }
            else if (NMSHandler.getVersion().isAtLeast(NMSVersion.v1_12_R1) && type == EntityType.SHULKER
                    && mechanism.getValue().matchesEnum(DyeColor.values())) {
                ShulkerHelper.setColor(colored, DyeColor.valueOf(mechanism.getValue().asString().toUpperCase()));
            }
            else {
                dB.echoError("Could not apply color '" + mechanism.getValue().toString() + "' to entity of type " + type.name() + ".");
            }

        }
    }
}
