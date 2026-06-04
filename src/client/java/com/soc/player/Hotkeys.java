package com.soc.player;

import com.soc.gui.screen.QueueScreen;
import com.soc.networking.c2s.RequestOpenQueueScreenPayload;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import com.soc.resourcedata.listeners.SkywarsLootData;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class Hotkeys {
	private static final KeyBinding PRINT_HELD_COMPONENTS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.socwars.print_held_components",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			"category.socwars.debug"
	));
	private static final KeyBinding OPEN_QUEUE_SCREEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.socwars.open_queue_screen",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_U,
			"category.socwars.debug"
	));

	public static void initialise() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (PRINT_HELD_COMPONENTS.wasPressed()) {
				final ClientPlayerEntity player = client.player;
				player.getStackInHand(Hand.MAIN_HAND).getComponents().forEach(component -> player.sendMessage(Text.literal(component.toString()), false));

				final Map<Integer, SkywarsItemData> dataMap = SkywarsLootData.INSTANCE.getSkywarsItemData().getPoolsForKey(player.getStackInHand(Hand.MAIN_HAND).getItem());
				dataMap.forEach((pool, data) -> player.sendMessage(Text.translatable("debug.skywars_item_weights", pool, data.weightT1(), data.weightT2(), data.weightT3(), data.weightT4()), false));
			}

			while (OPEN_QUEUE_SCREEN.wasPressed()) {
				ClientPlayNetworking.send(new RequestOpenQueueScreenPayload());
			}
		});
	}
}
