package com.soc.game.manager;

import com.soc.database.stats.HideAndSeekTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.HideAndSeekGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.items.AttackFunctionWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

import static com.soc.game.map.AbstractHidingGameMap.HIDER_COLOUR;
import static com.soc.game.map.AbstractHidingGameMap.SEEKER_COLOUR;
import static com.soc.lib.SocWarsLib.scaleEntity;

public class HideAndSeekGameManager extends AbstractHidingGameManager<HideAndSeekGameMap, HideAndSeekTable, HideAndSeekGameManager> {
    protected HideAndSeekGameManager(
            ServerWorld world,
            Set<ServerPlayerEntity> players,
            @Nullable SpreadRules spreadRules,
            int gameId
    ) {
        super(GameType.HIDE_AND_SEEK, world, players, spreadRules, gameId);
    }

    @Override
    protected void onFinishCountdown() {
        this.map.spawnCages(false, HIDER_COLOUR);
        this.getPlayers(HIDER_COLOUR).forEach(hider -> {
            hider.changeGameMode(GameMode.ADVENTURE);
            scaleEntity(hider, 0.75f);
        });

        final TitleS2CPacket youAreSeekingPacket = new TitleS2CPacket(Text.translatable("game.hiding.you_are_seeking"));
        this.getPlayers(SEEKER_COLOUR).forEach(seeker -> {
            seeker.giveItemStack(new ItemStack(AttackFunctionWeapon.SEEKING_STICK));
            seeker.networkHandler.sendPacket(youAreSeekingPacket);
        });

        PrescheduledEvents.playCountdown(() -> {
            this.map.spawnCages(false, SEEKER_COLOUR);
            this.getPlayers(SEEKER_COLOUR).forEach(seeker -> {
                seeker.changeGameMode(GameMode.ADVENTURE);

                final Optional<Vec3d> seekerSpawn = this.map.getSpawnPositionNoOffset(SEEKER_COLOUR).map(BlockPos::toCenterPos);
                seekerSpawn.ifPresent(pos -> seeker.requestTeleport(pos.x, pos.y, pos.z));
                seeker.fallDistance = 0d;
            });
        }, this, 15, 20, SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), true);
    }

    @Override
    protected HideAndSeekGameMap buildMap() {
        final Optional<HideAndSeekGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), HideAndSeekGameMap::fromNbt, HideAndSeekGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Hide and Seek map found");
        return map.get();
    }

    @Override
    protected Function<UUID, HideAndSeekTable> dbTableBuilder() {
        return HideAndSeekTable::new;
    }

    @Override
    protected EventQueue<HideAndSeekGameManager> buildEventQueue() {
        final EventQueue<HideAndSeekGameManager> eventQueue = super.buildEventQueue();

        for (int i = 1; i < 5; i++) {
            eventQueue.addEvent(i * 60 * 20, manager -> manager.getPlayers(HIDER_COLOUR).forEach(player -> {
                this.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), SoundCategory.MASTER, 5, 1);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
            }), Text.translatable("events.hide_and_seek.ping." + i));
        }
        eventQueue.addEvent(5 * 60 * 20, manager -> manager.endGame(false, HIDER_COLOUR), Text.translatable("events.hide_and_seek.end"));

        return eventQueue;
    }

    @Override
    public void tryFindPlayer(LivingEntity seeker, ServerPlayerEntity hider) {
        this.findPlayer(seeker, hider);
    }
}
