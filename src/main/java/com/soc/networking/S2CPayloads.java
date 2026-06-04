package com.soc.networking;

import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.*;
import com.soc.networking.s2c.skywars.JoinSkywarsPayload;
import com.soc.networking.s2c.skywars.LeaveSkywarsPayload;
import com.soc.networking.s2c.skywars.SetTeamLivesPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class S2CPayloads {
    public static void initialise() {
        PayloadTypeRegistry.playS2C().register(SinglePlayerDataPayload.ID, SinglePlayerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(QueueProgressPayload.ID, QueueProgressPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AllSyncPlayerDataPayload.ID, AllSyncPlayerDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AddVelocityPayload.ID, AddVelocityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JoinBedwarsPayload.ID, JoinBedwarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveBedwarsPayload.ID, LeaveBedwarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedBreakPayload.ID, BedBreakPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedwarsIndividualShopDataPayload.ID, BedwarsIndividualShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BedwarsTeamShopDataPayload.ID, BedwarsTeamShopDataPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateHotbarPayload.ID, UpdateHotbarPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UseTrapOrAbilityPayload.ID, UseTrapOrAbilityPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SmokescreenPayload.ID, SmokescreenPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BatchParticlePayload.ID, BatchParticlePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BlockProtectionPayload.ID, BlockProtectionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JumpscarePayload.ID, JumpscarePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SilencePayload.ID, SilencePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EventQueuePayload.ID, EventQueuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveGamePayload.ID, LeaveGamePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetAnglesPayload.ID, SetAnglesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TeamEliminatedPayload.ID, TeamEliminatedPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(JoinSkywarsPayload.ID, JoinSkywarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LeaveSkywarsPayload.ID, LeaveSkywarsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetTeamLivesPayload.ID, SetTeamLivesPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(KitBlockEntityAssignment.ID, KitBlockEntityAssignment.CODEC);
    }
}
