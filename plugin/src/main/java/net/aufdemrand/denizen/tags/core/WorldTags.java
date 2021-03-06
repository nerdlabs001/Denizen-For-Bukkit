package net.aufdemrand.denizen.tags.core;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.objects.dWorld;
import net.aufdemrand.denizencore.objects.TagRunnable;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.ReplaceableTagEvent;
import net.aufdemrand.denizencore.tags.TagManager;
import net.aufdemrand.denizencore.utilities.CoreUtilities;

public class WorldTags {

    public WorldTags(Denizen denizen) {
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                worldTags(event);
            }
        }, "world");
    }

    public void worldTags(ReplaceableTagEvent event) {

        if (!event.matches("world") || event.replaced()) {
            return;
        }

        dWorld world = null;

        if (event.hasNameContext()) {
            world = dWorld.valueOf(event.getNameContext(), event.getAttributes().context);
        }

        if (world == null) {
            return;
        }

        Attribute attribute = event.getAttributes();
        event.setReplacedObject(CoreUtilities.autoAttrib(world, attribute.fulfill(1)));

    }
}
