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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.soc.game.map.AbstractHidingGameMap.HIDER_COLOUR;
import static com.soc.game.map.AbstractHidingGameMap.SEEKER_COLOUR;

public class Powerup {
	private interface ApplicationFunction {
		boolean apply(ServerPlayerEntity player);
	}

	private interface AttributeIntensityMapper {
		double map(double currentValue);
	}

	private final Identifier id;
	private final ApplicationFunction applicationFunction;

	private Powerup(String name, ApplicationFunction applicationFunction) {
		this(Identifier.of(SocWars.MOD_ID, name), applicationFunction);
	}

	private Powerup(Identifier id, ApplicationFunction applicationFunction) {
		this.id = id;
		this.applicationFunction = applicationFunction;
	}

	boolean apply(ServerPlayerEntity player) {
		player.sendMessage(Text.translatable("game.powerup.collect", this.getName()), false);
		player.sendMessage(this.getInfo(), false);
		return this.applicationFunction.apply(player);
	}

	public Identifier getId() {
		return this.id;
	}

	public Text getName() {
		return Text.translatable(this.id.toTranslationKey("powerup"));
	}

	public Text getInfo() {
		return Text.translatable(this.id.toTranslationKey("powerup") + ".info");
	}

	private static Powerup effect(String name, StatusEffectInstance effect) {
		return new Powerup(name, player -> player.addStatusEffect(new StatusEffectInstance(effect)));
	}

	private static Powerup attribute(String name, RegistryEntry<EntityAttribute> attribute, AttributeIntensityMapper attributeIntensityMapper, EntityAttributeModifier.Operation operation, int duration) {
		return new Powerup(name, player -> {
			final Identifier modifierId = Identifier.of(SocWars.MOD_ID, "random" + player.getRandom().nextLong());
			final EntityAttributeInstance attributeInstance = Objects.requireNonNull(player.getAttributeInstance(attribute));

			attributeInstance.addTemporaryModifier(new EntityAttributeModifier(modifierId, attributeIntensityMapper.map(attributeInstance.getValue()), operation));

			Events.getInstance().scheduleEvent(() -> attributeInstance.removeModifier(modifierId), duration);

			return true;
		});
	}

	private static Powerup createGameStored(String name, int duration) {
		return new Powerup(name, null) {
			@Override
			public boolean apply(ServerPlayerEntity player) {
				player.sendMessage(Text.translatable("game.powerup.collect", this.getName()), false);
				player.sendMessage(this.getInfo(), false);

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

	private static Powerup createGameStored(String name) {
		return createGameStored(name, 0);
	}

	private static Powerup createGameInstant(String name, Consumer<AbstractHidingGameManager<?, ?, ?>> function) {
		return new Powerup(name, player -> GamesManager.getInstance().getGame(player).map(manager -> {
			if (manager instanceof AbstractHidingGameManager<?, ?, ?> hidingManager) {
				function.accept(hidingManager);
				return true;
			} else {
				return false;
			}
		}).orElse(false));
	}

	public static final Powerup SHRINKING = attribute("shrinking", EntityAttributes.SCALE, current -> current * -0.5d, EntityAttributeModifier.Operation.ADD_VALUE, 30 * 20);

	public static final Powerup ECCENTRIC = createGameStored("eccentric", 60 * 20);

	public static final Powerup STRENGTH = effect("strength", new StatusEffectInstance(StatusEffects.STRENGTH, 30 * 20, 1, false, true));

	public static final Powerup FLIGHT = effect("flight", new StatusEffectInstance(ModEffects.FLIGHT, 15 * 20, 0, false, true));

	public static final Powerup INVISIBILITY_10S = effect("invisibility_10s", new StatusEffectInstance(StatusEffects.INVISIBILITY, 10 * 20, 0, false, true));

	public static final Powerup INVISIBILITY_20S = effect("invisibility_20s", new StatusEffectInstance(StatusEffects.INVISIBILITY, 20 * 20, 0, false, true));

	public static final Powerup EXTRA_LIFE = createGameStored("extra_life");

	public static final Powerup EXTRA_RANGE = attribute("extra_range", EntityAttributes.ENTITY_INTERACTION_RANGE, current -> 2d, EntityAttributeModifier.Operation.ADD_VALUE, 30 * 20);

	public static final Powerup SEEKER_GLOWING = createGameInstant("seeker_glowing", manager -> {
		for (ServerPlayerEntity seeker : manager.getPlayers(SEEKER_COLOUR)) {
			seeker.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15 * 20, 0, false, true));
		}
	});

	public static final Powerup HIDER_GLOWING = createGameInstant("hider_glowing", manager -> {
		for (ServerPlayerEntity hider : manager.getPlayers(HIDER_COLOUR)) {
			hider.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 5 * 20, 0, false, true));
		}
	});

	public static final List<Powerup> HIDER_POWERUPS = List.of(
			SHRINKING,
			ECCENTRIC,
			STRENGTH,
			FLIGHT,
			INVISIBILITY_10S,
			SEEKER_GLOWING,
			EXTRA_LIFE
	);

	public static final List<Powerup> SEEKER_POWERUPS = List.of(
			EXTRA_RANGE,
			HIDER_GLOWING,
			FLIGHT,
			INVISIBILITY_20S
	);
}