package com.soc.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.soc.commands.argumenttypes.GameIdArgumentType;
import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.GamesManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Optional;

public interface EndGameCommand {
    String END_GAME_COMMAND_ID = "endgame";
    String GAME_ID_ARGUMENT = "game id";
    String GAME_IMMEDIATE_ARGUMENT = "immediate";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal(END_GAME_COMMAND_ID)
            .requires(source -> source.hasPermissionLevel(1))
            .executes(EndGameCommand::execute)
            .then(
                CommandManager.argument(GAME_ID_ARGUMENT, new GameIdArgumentType())
                    .executes(EndGameCommand::execute)
                    .then(
                        CommandManager.argument(GAME_IMMEDIATE_ARGUMENT, BoolArgumentType.bool())
                            .executes(EndGameCommand::execute)
                    )
            )
        );
    }

    static int execute(CommandContext<ServerCommandSource> context) {
        int gameId;
        boolean immediate;
        try {
            gameId = GameIdArgumentType.get(context, GAME_ID_ARGUMENT);
        } catch (Exception ignored) {
            final Entity source = context.getSource().getEntity();
            if (!(source instanceof PlayerEntity)) {
                context.getSource().sendMessage(Text.translatable("command.fail.end_game.no_entity"));
                return 0;
            }

            final Optional<AbstractGameManager<?, ?, ?>> playerGame = GamesManager.getInstance().getGame((PlayerEntity)source);
            if (playerGame.isEmpty()) {
                context.getSource().sendMessage(Text.translatable("command.fail.end_game.not_in_game"));
                return 0;
            }
            gameId = playerGame.get().getGameId();
        }

        try {
            immediate = BoolArgumentType.getBool(context, GAME_IMMEDIATE_ARGUMENT);
        } catch (Exception ignored) {
            immediate = false;
        }

        GamesManager.getInstance().getGame(gameId).get().endGame(immediate);

        return Command.SINGLE_SUCCESS;
    }
}
