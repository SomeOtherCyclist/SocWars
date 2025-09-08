package com.soc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/*
public interface BedBreakCallback {
    Event<BedBreakCallback> EVENT = EventFactory.createArrayBacked(BedBreakCallback.class,
            (listeners) -> (player, sheep) -> {
                for (BedBreakCallback listener : listeners) {
                    ActionResult result = listener.interact(player, sheep);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player, SheepEntity sheep);
}
 */
