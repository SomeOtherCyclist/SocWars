package com.soc.networking;

import com.soc.networking.c2s.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class C2SPayloads {
    public static void initialise() {
        PayloadTypeRegistry.playC2S().register(MapBlockUpdatePayload.ID, MapBlockUpdatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MapBlockStructureCheckPayload.ID, MapBlockStructureCheckPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MapBlockSaveMapPayload.ID, MapBlockSaveMapPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(KitBlockUpdatePayload.ID, KitBlockUpdatePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(KitSelectionPayload.ID, KitSelectionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(OnAttackButtonPressedPayload.ID, OnAttackButtonPressedPayload.CODEC);
    }
}
