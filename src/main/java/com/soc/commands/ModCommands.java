package com.soc.commands;

import com.soc.commands.argumenttypes.GameIdArgumentType;
import com.soc.commands.argumenttypes.GameTypeArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public interface ModCommands {
    static void initialise() {
        CommandRegistrationCallback.EVENT.register(EndGame::register);
        CommandRegistrationCallback.EVENT.register(Collectibles::register);
        CommandRegistrationCallback.EVENT.register(Queue::register);

        GameIdArgumentType.initialise();
        GameTypeArgumentType.initialise();
    }
}
