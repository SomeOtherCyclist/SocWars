package com.soc.game.manager;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public class GamesManager {
    public final HashMap<Identifier, AbstractGameManager> games = new HashMap<>();
}
