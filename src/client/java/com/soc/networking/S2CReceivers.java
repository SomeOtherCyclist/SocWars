package com.soc.networking;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.gui.hud.JumpscareHud;
import com.soc.gui.hud.sidebar.BedwarsTeamsHud;
import com.soc.gui.hud.BlockProtectionManagerAndHud;
import com.soc.gui.hud.sidebar.EventsHud;
import com.soc.gui.hud.sidebar.SkywarsTeamsHud;
import com.soc.gui.screen.KitBlockCreationScreen;
import com.soc.gui.screen.QueueScreen;
import com.soc.lib.Coroutine;
import com.soc.lib.Coroutines;
import com.soc.lib.Events;
import com.soc.mixin.client.GetOptionsVolumes;
import com.soc.networking.s2c.*;
import com.soc.networking.s2c.bedwars.*;
import com.soc.networking.s2c.skywars.JoinSkywarsPayload;
import com.soc.networking.s2c.skywars.LeaveSkywarsPayload;
import com.soc.networking.s2c.skywars.SetTeamLivesPayload;
import com.soc.player.ClientPlayerDataManager;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.lib.SocWarsLib.iterateInSphere;
import static com.soc.lib.SocWarsLib.randomCentredVec3d;

public class S2CReceivers {
    public static void initialise() {
        queues();
        player();
        genericGame();
        traps();
        bedwars();
        skywars();

        ClientPlayNetworking.registerGlobalReceiver(UpdateHotbarPayload.ID, (payload, context) -> {
            final PlayerEntity player = context.player();
            final PlayerScreenHandler screenHandler = player.playerScreenHandler;
            final List<ItemStack> contents = payload.contents();

            for (int i = 0; i < payload.contents().size(); i++) {
                screenHandler.setStackInSlot(i + 36, payload.revision(), contents.get(i));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(SmokescreenPayload.ID, (payload, context) -> {
            final World world = context.player().getWorld();
            final int randomOffset = world.random.nextInt();

            final AtomicInteger count = new AtomicInteger(0);
            Coroutines.getInstance().startCoroutine(new Coroutine<>(count, t -> {
                final int currentTime = t.getAndIncrement();

                if (currentTime % 5 == 0) {
                    final Random random = new LocalRandom(currentTime + randomOffset);
                    context.player().playSound(SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 1f, currentTime / 200f + 1.1f);

                    iterateInSphere(payload.pos(), 5f, 0.5f, currentPos -> {
                        final Vec3d randomPosOffset = randomCentredVec3d(random).multiply(0.5d);

                        final double x = currentPos.getX() + randomPosOffset.x + 0.5d; //How about I don't forget to centre it from the blockpos
                        final double y = currentPos.getY() + randomPosOffset.y + 0.5d;
                        final double z = currentPos.getZ() + randomPosOffset.z + 0.5d;

                        world.addParticleClient(ParticleTypes.CLOUD, true, true, x, y, z, random.nextFloat() * 0.05f - 0.025f, random.nextFloat() * 0.05f - 0.025f, random.nextFloat() * 0.05f - 0.025f);
                    });
                }

                return currentTime > 80;
            }));
        });
        ClientPlayNetworking.registerGlobalReceiver(BatchParticlePayload.ID, ((payload, context) -> {
            final World world = context.player().getWorld();
            final Vec3d velocity = payload.velocity();
            payload.positions().forEach(pos -> world.addParticleClient(payload.particleType(), pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z));
        }));
        ClientPlayNetworking.registerGlobalReceiver(KitBlockEntityAssignment.ID, ((payload, context) -> {
            if (
                    MinecraftClient.getInstance().currentScreen instanceof KitBlockCreationScreen kitBlockCreationScreen &&
                    MinecraftClient.getInstance().world.getBlockEntity(payload.block().pos()) instanceof KitBlockEntity kitBlockEntity
            ) {
                kitBlockCreationScreen.setBlockEntity(kitBlockEntity);
            }
        }));
    }

    private static void queues() {
        ClientPlayNetworking.registerGlobalReceiver(OpenQueueScreenPayload.ID, (payload, context) -> {
            MinecraftClient.getInstance().setScreen(new QueueScreen(payload.queues()));
        });
    }

    private static void player() {
        ClientPlayNetworking.registerGlobalReceiver(AddVelocityPayload.ID, (payload, context) -> {
            context.player().addVelocity(payload.velocity());
        });
        ClientPlayNetworking.registerGlobalReceiver(SetAnglesPayload.ID, ((payload, context) -> {
            final PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null && player.getId() == payload.entityId()) {
                player.setAngles(payload.yaw(), payload.pitch());
            }
        }));
        ClientPlayNetworking.registerGlobalReceiver(SinglePlayerDataPayload.ID, (payload, context) -> {
            final PlayerEntity player = MinecraftClient.getInstance().player;
            if (context.player() == player) { //context.player() should never return null, right?
				assert MinecraftClient.getInstance().player != null;
				ClientPlayerDataManager.setPlayerData(MinecraftClient.getInstance().player.getUuid(), payload.playerData());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(AllSyncPlayerDataPayload.ID, (payload, context) -> {
            ClientPlayerDataManager.setMultiplePlayerData(payload.playerDataMap());
        });
    }

    private static void traps() {
        ClientPlayNetworking.registerGlobalReceiver(UseTrapOrAbilityPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler instanceof BedwarsTeamShopScreenHandler shopScreenHandler) {
                if (payload.isAbility()) {
                    shopScreenHandler.useAbility(payload.nextTime(), payload.duration());
                } else {
                    shopScreenHandler.useTrap(payload.nextTime(), payload.duration());
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(JumpscarePayload.ID, ((payload, context) -> {
            JumpscareHud.triggerJumpscare(payload);
        }));
        ClientPlayNetworking.registerGlobalReceiver(SilencePayload.ID, ((payload, context) -> {
            final SimpleOption<Double> masterVolume = ((GetOptionsVolumes)MinecraftClient.getInstance().options).getSoundVolumeLevels().get(SoundCategory.MASTER);
            final Double startingVolume = masterVolume.getValue();
            if (startingVolume < 10e-5d) return;

            masterVolume.setValue(0d);
            Events.getInstance().scheduleEvent(() -> {
                if (masterVolume.getValue() < 10e-5d) masterVolume.setValue(startingVolume);
            }, payload.time());
        }));
    }

    private static void genericGame() {
        ClientPlayNetworking.registerGlobalReceiver(TeamEliminatedPayload.ID, (payload, context) -> {
            switch (payload.gameType()) {
                case BEDWARS -> BedwarsTeamsHud.eliminateTeam(payload.team());
                case SKYWARS -> SkywarsTeamsHud.eliminateTeam(payload.team());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveGamePayload.ID, ((payload, context) -> {
            BlockProtectionManagerAndHud.INSTANCE.clearBlockProtection();
            EventsHud.clear();
        }));
        ClientPlayNetworking.registerGlobalReceiver(EventQueuePayload.ID, ((payload, context) -> {
            EventsHud.receivePayload(payload);
        }));
        ClientPlayNetworking.registerGlobalReceiver(BlockProtectionPayload.ID, ((payload, context) -> {
            BlockProtectionManagerAndHud.INSTANCE.setBlockProtection(payload);
        }));
    }

    private static void bedwars() {
        ClientPlayNetworking.registerGlobalReceiver(BedwarsIndividualShopDataPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler.syncId == payload.syncId() && screenHandler instanceof BedwarsIndividualShopScreenHandler shopScreenHandler) {
                shopScreenHandler.setShopContents(payload.shopContents());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(BedwarsTeamShopDataPayload.ID, (payload, context) -> {
            final ScreenHandler screenHandler = context.player().currentScreenHandler;
            if (screenHandler.syncId == payload.syncId() && screenHandler instanceof BedwarsTeamShopScreenHandler shopScreenHandler) {
                shopScreenHandler.setShopContents(payload.shopContents());
                shopScreenHandler.setTrapProgress(payload.trapProgressStats());
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(JoinBedwarsPayload.ID, (payload, context) -> {
            BedwarsTeamsHud.joinGame(payload.teams());
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveBedwarsPayload.ID, (payload, context) -> {
            BedwarsTeamsHud.leaveGame();
        });
        ClientPlayNetworking.registerGlobalReceiver(BedBreakPayload.ID, (payload, context) -> {
            BedwarsTeamsHud.breakBed(payload.team());
        });
    }

    private static void skywars() {
        ClientPlayNetworking.registerGlobalReceiver(JoinSkywarsPayload.ID, (payload, context) -> {
            SkywarsTeamsHud.joinGame(payload.teams());
        });
        ClientPlayNetworking.registerGlobalReceiver(LeaveSkywarsPayload.ID, (payload, context) -> {
            SkywarsTeamsHud.leaveGame();
        });
        ClientPlayNetworking.registerGlobalReceiver(SetTeamLivesPayload.ID, (payload, context) -> {
            SkywarsTeamsHud.setTeamLives(payload);
        });
    }
}
