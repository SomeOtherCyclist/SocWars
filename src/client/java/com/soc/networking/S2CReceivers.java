package com.soc.networking;

import com.soc.networking.s2c.JoinQueuePayload;
import com.soc.networking.s2c.LeaveQueuePayload;
import com.soc.networking.s2c.PlayerDataPayload;
import com.soc.player.PlayerDataManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

public class S2CReceivers {
    public static void initialise() {
        ClientPlayNetworking.registerGlobalReceiver(JoinQueuePayload.ID, (payload, context) -> {
            context.player().sendMessage(Text.translatable("queue.join", payload.queue()), false);
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveQueuePayload.ID, (payload, context) -> {
            context.player().sendMessage(Text.translatable("queue.leave", payload.queue()), false);
        });
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataPayload.ID, (payload, context) -> {
            PlayerDataManager.setPlayerData(context.player(), payload.playerData());
        });
    }
}
