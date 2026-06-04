package com.soc.commands.argumenttypes;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.soc.SocWars;
import com.soc.game.manager.GamesManager;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class GameIdArgumentType implements ArgumentType<Integer> {
    public static void initialise() {
        ArgumentTypeRegistry.registerArgumentType(
                Identifier.of(SocWars.MOD_ID, "game_id"),
                GameIdArgumentType.class, ConstantArgumentSerializer.of(GameIdArgumentType::new)
        );
    }

    public GameIdArgumentType() {}

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        final Collection<Integer> activeIds = GamesManager.getInstance().getActiveGameIds();

        final int start = reader.getCursor();
        final int value = reader.readInt();
        if (value < 0) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(reader, value, 0);
        }
        if (!activeIds.contains(value)) {
            reader.setCursor(start);
            throw new CommandSyntaxException(new CommandExceptionType() {}, new LiteralMessage(String.format("There is no active game with id %s", value)));
        }
        return value;
    }

    public static int get(CommandContext<?> context, String name) {
        return context.getArgument(name, int.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CompletableFuture.completedFuture(Suggestions.create("I have no idea what this string is supposed to be for so I'm writing random garbage in here so that I will recognise it if I see it", GamesManager.getInstance().getGameIdSuggestions(builder.getStart())));
    }
}
