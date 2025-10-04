package com.soc.lib;

import com.soc.SocWars;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;

public class Coroutines {
    private static final Coroutines INSTANCE = new Coroutines();
    public static Coroutines getInstance() {
        return INSTANCE;
    }

    private final ArrayList<Coroutine<?>> coroutines = new ArrayList<>();

    private Coroutines() {}

    public static void initialise() {
        final Coroutines instance = getInstance();
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (!server.getTickManager().isFrozen()) instance.runCoroutines();
        });

        SocWars.LOGGER.warn("This coroutines implementation is utter garbage and has ");
    }

    private void runCoroutines() {
        this.coroutines.removeIf(Coroutine::run);
    }

    public void startCoroutine(final Coroutine<?> coroutine) {
        this.coroutines.add(coroutine);
    }
}
