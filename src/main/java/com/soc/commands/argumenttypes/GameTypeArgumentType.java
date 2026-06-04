package com.soc.commands.argumenttypes;

import com.mojang.brigadier.context.CommandContext;
import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.EnumArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class GameTypeArgumentType extends EnumArgumentType<GameType> {
	public static void initialise() {
		ArgumentTypeRegistry.registerArgumentType(
				Identifier.of(SocWars.MOD_ID, "game_type"),
				GameTypeArgumentType.class, ConstantArgumentSerializer.of(GameTypeArgumentType::new)
		);
	}

	public GameTypeArgumentType() {
		super(GameType.CODEC, GameType::values);
	}

	@Nullable
	public static GameType getGameType(CommandContext<ServerCommandSource> context, String id) {
		try {
			return context.getArgument(id, GameType.class);
		} catch (Exception ignored) {
			return null;
		}
	}
}
