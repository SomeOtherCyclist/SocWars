package com.soc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.soc.player.CollectiblesManager;
import com.soc.player.PlayerDataManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public interface CollectiblesCommand {
    String COLLECTIBLES_COMMAND_ID = "collectibles";
    String FULL_LIST_ARGUMENT = "full list";

    static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal(COLLECTIBLES_COMMAND_ID)
            .executes(CollectiblesCommand::execute)
                .then(
                    CommandManager.argument(FULL_LIST_ARGUMENT, BoolArgumentType.bool())
                        .executes(CollectiblesCommand::execute)
                )
        );
    }

    static int execute(CommandContext<ServerCommandSource> context) {
        boolean fullList = false;

        try {
            fullList = BoolArgumentType.getBool(context, FULL_LIST_ARGUMENT);
        } catch (Exception ignored) {}

        final Entity executor = context.getSource().getEntity();
        if (!(executor instanceof ServerPlayerEntity player)) return -1;

        final List<Boolean> collectibles = PlayerDataManager.getPlayerData(player).getCollectibles();
        final int collectedCollectibles = Collections.frequency(collectibles, true);
        final int numCollectibles = CollectiblesManager.getPersistentState(player.getWorld()).getNumCollectibles();

        final Text header = Text.translatable("message.collectibles", collectedCollectibles, numCollectibles).formatted(Formatting.GOLD);
        final MutableText message = header.copy();

        if (fullList) {
            final DecimalFormat indexFormat = new DecimalFormat("0".repeat(String.valueOf(numCollectibles).length())); //I hate this so much but it works - why couldn't the simple .* regex have worked out

            for (int i = 0; i < numCollectibles; i++) {
                message.append("\n  " + indexFormat.format(i + 1) + ": ").append(Text.translatable(i < collectibles.size() && collectibles.get(i) ? "message.collectibles.collected" : "message.collectibles.not_collected"));
            }
        } else {
            for (int i = 0; i < collectibles.size(); i++) {
                if (collectibles.get(i)) message.append(Text.literal("\n  " + (i + 1)).formatted(Formatting.DARK_GREEN));
            }
            message.append("\n").append(header);
        }

        player.sendMessage(message);

        return 0;
    }
}
