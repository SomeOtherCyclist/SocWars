package com.soc.game.manager;

import com.soc.SocWars;
import com.soc.effects.util.ModEffects;
import com.soc.lib.Events;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.soc.game.map.AbstractHidingGameMap.HIDER_COLOUR;
import static com.soc.game.map.AbstractHidingGameMap.SEEKER_COLOUR;

public interface Powerup {
	interface AttributeIntensityMapper {
		double map(double currentValue);
	}

	boolean apply(ServerPlayerEntity player);

	private static Powerup effect(StatusEffectInstance effect) {
		return (player) -> player.addStatusEffect(new StatusEffectInstance(effect));
	}

	@SuppressWarnings("SameParameterValue")
	private static Powerup attribute(RegistryEntry<EntityAttribute> attribute, AttributeIntensityMapper attributeIntensityMapper, EntityAttributeModifier.Operation operation, int duration) {
		return (player) -> {
			final Identifier modifierId = Identifier.of(SocWars.MOD_ID, "random" + player.getRandom().nextLong());
			final EntityAttributeInstance attributeInstance = Objects.requireNonNull(player.getAttributeInstance(attribute));

			attributeInstance.addTemporaryModifier(new EntityAttributeModifier(modifierId, attributeIntensityMapper.map(attributeInstance.getValue()), operation));

			Events.getInstance().scheduleEvent(() -> attributeInstance.removeModifier(modifierId), duration);

			return true;
		};
	}

	private static Powerup createGameStored(int duration) {
		return new Powerup() {
			@Override
			public boolean apply(ServerPlayerEntity player) {
				return GamesManager.getInstance().getGame(player).map(manager -> {
					if (manager instanceof PowerupGame powerupGame) {
						return duration > 0 ? powerupGame.storePowerup(player, this, duration) : powerupGame.storePowerup(player, this);
					} else {
						return false;
					}
				}).orElse(false);
			}
		};
	}

	private static Powerup createGameStored() {
		return createGameStored(0);
	}

	private static Powerup createGameInstant(Consumer<AbstractHidingGameManager<?, ?, ?>> function) {
		return (player) -> GamesManager.getInstance().getGame(player).map(manager -> {
			if (manager instanceof AbstractHidingGameManager<?, ?, ?> hidingManager) {
				function.accept(hidingManager);
				return true;
			} else {
				return false;
			}
		}).orElse(false);
	}

	Powerup SHRINKING = attribute(EntityAttributes.SCALE, current -> current * -0.5d, EntityAttributeModifier.Operation.ADD_VALUE, 30 * 20);

	Powerup ECCENTRIC = createGameStored(60 * 20);

	Powerup STRENGTH = effect(new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 2, false, true));

	Powerup FLIGHT = effect(new StatusEffectInstance(ModEffects.FLIGHT, 15 * 20, 0, false, true));

	Powerup INVISIBILITY_10S = effect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 10 * 20, 0, false, true));

	Powerup INVISIBILITY_20S = effect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20 * 20, 0, false, true));

	Powerup EXTRA_LIFE = createGameStored();

	Powerup EXTRA_RANGE = attribute(EntityAttributes.ENTITY_INTERACTION_RANGE, current -> 2d, EntityAttributeModifier.Operation.ADD_VALUE, 30 * 20);

	Powerup SEEKER_GLOWING = createGameInstant(manager -> {
		for (ServerPlayerEntity seeker : manager.getPlayers(SEEKER_COLOUR)) {
			seeker.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15 * 20, 0, false, true));
		}
	});

	Powerup HIDERS_GLOWING = createGameInstant(manager -> {
		for (ServerPlayerEntity hider : manager.getPlayers(HIDER_COLOUR)) {
			hider.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 5 * 20, 0, false, true));
		}
	});

	List<Powerup> HIDER_POWERUPS = List.of(
			SHRINKING,
			ECCENTRIC,
			STRENGTH,
			FLIGHT,
			INVISIBILITY_10S,
			SEEKER_GLOWING,
			EXTRA_LIFE
	);

	List<Powerup> SEEKER_POWERUPS = List.of(
			EXTRA_RANGE,
			HIDERS_GLOWING,
			FLIGHT,
			INVISIBILITY_20S
	);
}