package com.soc.commands;

import com.mojang.brigadier.CommandDispatcher;
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

public interface Queue {
    String QUEUE_COMMAND_ID = "queue";
    String QUEUE_COMPLETE_ARGUMENT = "complete";
    String QUEUE_TYPE_ARGUMENT = "full list";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal(QUEUE_COMMAND_ID)
            .requires(source -> source.hasPermissionLevel(2))
                .then(
                    CommandManager.literal(QUEUE_COMPLETE_ARGUMENT)
                        .executes(Queue::execute)
                        .then(
                            CommandManager.argument(QUEUE_TYPE_ARGUMENT, new GameTypeArgumentType())
                                .executes(Queue::execute)
                        )
                )
        );
    }

    static int execute(CommandContext<ServerCommandSource> context) {
        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        @Nullable final GameType gameType = GameTypeArgumentType.getGameType(context, QUEUE_TYPE_ARGUMENT);

        final Collection<GameType> playerQueues = GamesManager.getInstance().getPlayerQueues(player);

        if (gameType == null && playerQueues.size() != 1) {
            player.sendMessage(playerQueues.isEmpty() ? Text.translatable("message.queue.not_in_queue") : Text.translatable("message.queue.in_multiple_queues"));
            return -1;
        }

        final GameType finalGameType = gameType != null ? gameType : playerQueues.iterator().next();
        final boolean success = GamesManager.getInstance().completeQueue(finalGameType);

        if (success) {
            player.sendMessage(Text.translatable("message.queue.finished_queue", finalGameType.getVariantName()));
            return 0;
        } else {
            player.sendMessage(Text.translatable("message.queue.game_empty"));
            return -1;
        }
    }
}
