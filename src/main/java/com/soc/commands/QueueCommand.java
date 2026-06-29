package com.soc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.soc.commands.argumenttypes.GameTypeArgumentType;
import com.soc.game.manager.GameType;
import com.soc.game.manager.GamesManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface QueueCommand {
    String QUEUE_COMMAND_ID = "queue";
    String QUEUE_COMPLETE_ARGUMENT = "complete";
    String QUEUE_ALLOW_SINGLE_PLAYER_ARGUMENT = "allowsingleplayer";
    String QUEUE_ALLOW_SINGLE_PLAYER_BOOL_ARGUMENT = "allowsingleplayer";
    String QUEUE_JOIN_ARGUMENT = "join";
    String QUEUE_LEAVE_ARGUMENT = "leave";
    String QUEUE_TYPE_ARGUMENT = "gametype";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal(QUEUE_COMMAND_ID)
            .then(
                CommandManager.literal(QUEUE_COMPLETE_ARGUMENT)
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(QueueCommand::executeComplete)
                    .then(
                        CommandManager.argument(QUEUE_TYPE_ARGUMENT, new GameTypeArgumentType())
                            .executes(QueueCommand::executeComplete)
                    )
            )
            .then(
                CommandManager.literal(QUEUE_ALLOW_SINGLE_PLAYER_ARGUMENT)
                    .executes(QueueCommand::executeAllowSinglePlayer)
                    .then(
                        CommandManager.argument(QUEUE_TYPE_ARGUMENT, new GameTypeArgumentType())
                            .executes(QueueCommand::executeAllowSinglePlayer)
                            .then(
                                CommandManager.argument(QUEUE_ALLOW_SINGLE_PLAYER_BOOL_ARGUMENT, BoolArgumentType.bool())
                                    .executes(QueueCommand::executeAllowSinglePlayer)
                            )
                    )
            )
            .then(
                CommandManager.literal(QUEUE_JOIN_ARGUMENT)
                    .requires(QueueCommand::sourceIsNotInGame)
                    .then(
                        CommandManager.argument(QUEUE_TYPE_ARGUMENT, new GameTypeArgumentType())
                            .executes(QueueCommand::join)
                    )
            )
            .then(
                CommandManager.literal(QUEUE_LEAVE_ARGUMENT)
                    .requires(QueueCommand::sourceIsNotInGame)
                    .executes(QueueCommand::leave)
                    .then(
                        CommandManager.argument(QUEUE_TYPE_ARGUMENT, new GameTypeArgumentType())
                            .executes(QueueCommand::leave)
                    )
            )
        );
    }

    static int executeComplete(CommandContext<ServerCommandSource> context) {
        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        final GameType gameType = resolveGameType(context, player);
        if (gameType == null) return -1;

        final boolean success = GamesManager.getInstance().completeQueue(gameType);

        if (success) {
            player.sendMessage(Text.translatable("message.queue.finished_queue", gameType.getVariantName()), false);
            return 0;
        } else {
            player.sendMessage(Text.translatable("message.queue.game_empty"), false);
            return -1;
        }
    }

    static int executeAllowSinglePlayer(CommandContext<ServerCommandSource> context) {
        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        final GameType gameType = resolveGameType(context, player);
        if (gameType == null) return -1;

        boolean success;
        try {
            final boolean allow = BoolArgumentType.getBool(context, QUEUE_ALLOW_SINGLE_PLAYER_BOOL_ARGUMENT);
            success = GamesManager.getInstance().allowSinglePlayer(gameType, allow);
        } catch (Exception ignored) {
            success = GamesManager.getInstance().allowSinglePlayer(gameType);
        }

        if (success) player.sendMessage(Text.translatable("message.queue.allowing_single_player", gameType.getVariantName()), false);

        return 0;
    }

    private static @Nullable GameType resolveGameType(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) {
        @Nullable final GameType gameType = GameTypeArgumentType.getGameType(context, QUEUE_TYPE_ARGUMENT);

        final Collection<GameType> playerQueues = GamesManager.getInstance().getPlayerQueues(player);

        if (gameType == null && playerQueues.size() != 1) {
            player.sendMessage(playerQueues.isEmpty() ? Text.translatable("message.queue.not_in_queue") : Text.translatable("message.queue.in_multiple_queues"), false);
            return null;
        }

        return gameType != null ? gameType : playerQueues.iterator().next();
    }

    static int join(CommandContext<ServerCommandSource> context) {
        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        final GameType gameType = GameTypeArgumentType.getGameType(context, QUEUE_TYPE_ARGUMENT);
        if (gameType == null) return -1;

        GamesManager.getInstance().queuePlayer(player, gameType);
        player.sendMessage(Text.translatable("message.queue.joined_queue", gameType.getVariantName()), false);

        return 0;
    }

    static int leave(CommandContext<ServerCommandSource> context) {
        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        final GameType gameType = resolveGameType(context, player);
        if (gameType == null) return -1;

        if (GamesManager.getInstance().isPlayerInQueue(player, gameType)) {
            GamesManager.getInstance().unqueuePlayer(player, gameType);
            player.sendMessage(Text.translatable("message.queue.left_queue", gameType.getVariantName()), false);
        } else {
            player.sendMessage(Text.translatable("message.queue.not_in_queue.queue", gameType.getVariantName()), false);
        }

        return 0;
    }

    private static boolean sourceIsNotInGame(ServerCommandSource source) {
        return source.getEntity() instanceof ServerPlayerEntity player && !GamesManager.getInstance().isPlayerInGame(player);
    }
}
