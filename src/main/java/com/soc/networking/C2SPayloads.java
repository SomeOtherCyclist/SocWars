package com.soc.networking;

import com.soc.networking.c2s.MapBlockUpdatePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class C2SPayloads {
    public static void initialise() {
        PayloadTypeRegistry.playC2S().register(MapBlockUpdatePayload.ID, MapBlockUpdatePayload.CODEC);
    }
}
