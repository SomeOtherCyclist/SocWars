package com.soc.commands;

import com.soc.commands.argumenttypes.GameIdArgumentType;
import com.soc.commands.argumenttypes.GameTypeArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public interface ModCommands {
    static void initialise() {
        CommandRegistrationCallback.EVENT.register(EndGameCommand::register);
        CommandRegistrationCallback.EVENT.register(CollectiblesCommand::register);
        CommandRegistrationCallback.EVENT.register(QueueCommand::register);

        GameIdArgumentType.initialise();
        GameTypeArgumentType.initialise();
    }
}
