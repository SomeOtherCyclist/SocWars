package com.soc;

import com.soc.blocks.blockentities.ModBlockEntities;
import com.soc.blocks.util.ModBlocks;
import com.soc.commands.ModCommands;
import com.soc.database.Database;
import com.soc.effects.util.ModEffects;
import com.soc.entities.util.ModEntities;
import com.soc.events.ModEvents;
import com.soc.game.manager.GamesManager;
import com.soc.game.manager.bedwars.traps.Abilities;
import com.soc.game.manager.bedwars.shopitems.*;
import com.soc.game.manager.bedwars.tickfunctions.TickFunctions;
import com.soc.game.manager.bedwars.traps.Traps;
import com.soc.items.components.ModComponents;
import com.soc.items.util.ModItems;
import com.soc.lib.Coroutines;
import com.soc.lib.EntityAttributes;
import com.soc.lib.Events;
import com.soc.networking.C2SPayloads;
import com.soc.networking.C2SReceivers;
import com.soc.networking.S2CPayloads;
import com.soc.player.CollectiblesManager;
import com.soc.player.PlayerDataManager;
import com.soc.resourcedata.ResourceManager;
import com.soc.screenhandler.ScreenHandlers;
import com.soc.util.BlockTags;
import com.soc.util.ItemTags;
import com.soc.util.Sounds;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocWars implements ModInitializer {
	public static final String MOD_ID = "socwars";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("SocWars");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in options mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		ModItems.initialise();
		Sounds.initialise();
		ModComponents.initialise();
		ModBlocks.initialise();
		ModEntities.initialise();
		ModBlockEntities.initialise();
		ModEffects.initialise();
		GamesManager.initialise();
		C2SPayloads.initialise();
		S2CPayloads.initialise();
		C2SReceivers.initialise();
		Coroutines.initialise();
		Events.initialise();
		ModEvents.initialise();
		ResourceManager.initialise();
		ModCommands.initialise();
		ScreenHandlers.initialise();
		EntityAttributes.initialise();

		ItemTags.initialise();
		BlockTags.initialise();

		SimpleShopItem.initialise();
		UpgradeableShopItem.initialise();
		EnchantmentUpgradeShopItem.initialise();
		TeamShopItem.initialise();
		TrapShopItem.initialise();
		AbilityShopItem.initialise();
		DisplayShopItem.initialise();
		GeneratorUpgradeShopItem.initialise();
		TickFunctionUpgradeShopItem.initialise();

		Traps.initialise();
		Abilities.initialise();
		TickFunctions.initialise();

		Database.initialise();
		CollectiblesManager.initialise();
		PlayerDataManager.initialise();
	}
}