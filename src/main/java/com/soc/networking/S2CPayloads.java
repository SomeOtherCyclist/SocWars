package com.soc.networking;

import com.soc.networking.s2c.JoinQueuePayload;
import com.soc.networking.s2c.LeaveQueuePayload;
import com.soc.networking.s2c.PlayerDataPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class S2CPayloads {
    public static void initialise() {
        PayloadTypeRegistry.playS2C().register(JoinQueuePayload.ID, JoinQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveQueuePayload.ID, LeaveQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerDataPayload.ID, PlayerDataPayload.CODEC);
    }
}
