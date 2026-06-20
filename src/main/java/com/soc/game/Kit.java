package com.soc.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.game.manager.GameType;
import com.soc.networking.c2s.KitSelectionPayload;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.List;

public class Kit implements Inventory {
    public static int ITEM_SLOTS = 10;
    public static final String DEFAULT_NAME = "unnamed";

    public static final PacketCodec<RegistryByteBuf, Kit> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_LIST_PACKET_CODEC, kit -> kit.items,
            PacketCodecs.collection(ArrayList::new, StatusEffectInstance.PACKET_CODEC), kit -> kit.effects,
            PacketCodecs.STRING, kit -> kit.name,
            Kit::new
    );

    public static final Codec<Kit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("items").orElse(DefaultedList.ofSize(0, ItemStack.EMPTY)).forGetter(kit -> kit.items),
            Codec.list(StatusEffectInstance.CODEC).fieldOf("effects").orElse(new ArrayList<>()).forGetter(kit -> kit.effects),
            Codecs.ESCAPED_STRING.fieldOf("name").orElse(DEFAULT_NAME).forGetter(kit -> kit.name)
    ).apply(instance, Kit::new));

    private DefaultedList<ItemStack> items;
    private List<StatusEffectInstance> effects;
    private String name;

    private boolean isDirty;

    public Kit(DefaultedList<ItemStack> items, List<StatusEffectInstance> effects, String name) {
        this.items = items;
        this.effects = effects;
        this.name = name;
    }

    private Kit(List<ItemStack> items, List<StatusEffectInstance> effects, String name) {
        this.items = DefaultedList.ofSize(ITEM_SLOTS, ItemStack.EMPTY);
        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i));
        }
        this.effects = effects;
        this.name = name;
    }

    public Kit() {
        this(DefaultedList.ofSize(ITEM_SLOTS, ItemStack.EMPTY), new ArrayList<>(), DEFAULT_NAME);
    }

	public void generateSelectionMessage(KitSelectionPayload payload, boolean playerOwnsKit, ServerPlayerEntity player) {
		final MutableText kitSelectionMessage;

        main:
        {
            if (payload.selectedGameTypes().isEmpty() && payload.removedGameTypes().isEmpty()) {
                kitSelectionMessage = Text.translatable("message.kit_selection.empty");
                break main;
            } else {
                kitSelectionMessage = this.getRemovalMessage(payload);
            }

            if (playerOwnsKit) {
                kitSelectionMessage.append(this.getSelectionMessage(payload));
            } else if (!payload.selectedGameTypes().isEmpty()) {
                kitSelectionMessage.append(Text.translatable("message.kit_selection.not_owned"));
            }
        }

		player.sendMessage(kitSelectionMessage);
	}

    private MutableText getRemovalMessage(KitSelectionPayload payload) {
        if (payload.removedGameTypes().isEmpty()) {
            return Text.empty();
        } else {
            final MutableText message = Text.translatable("message.kit_removal", this.getTextName());
            for (GameType gameType : payload.removedGameTypes()) {
                message.append("\n  ").append(gameType.getVariantName().formatted(Formatting.GOLD));
            }
            return message;
        }
    }

    private MutableText getSelectionMessage(KitSelectionPayload payload) {
        if (payload.selectedGameTypes().isEmpty()) {
            return Text.empty();
        } else {
            final MutableText message = Text.translatable("message.kit_selection", this.getTextName());
            for (GameType gameType : payload.selectedGameTypes()) {
                message.append("\n  ").append(gameType.getVariantName().formatted(Formatting.GOLD));
            }
            return message;
        }
    }

    public void apply(ServerPlayerEntity player) {
        this.items.forEach(item -> {
            final EquippableComponent equippableComponent = item.get(DataComponentTypes.EQUIPPABLE);
            if (equippableComponent == null || !player.getEquippedStack(equippableComponent.slot()).isEmpty()) {
                player.giveItemStack(item.copy());
            } else {
                player.equipStack(equippableComponent.slot(), item.copy());
            }
        });
        this.effects.forEach(effect -> player.addStatusEffect(new StatusEffectInstance(effect)));
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.items, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        final ItemStack stack = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.items.set(slot, stack);
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.items.clear();
        this.effects.clear();
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.items;
    }

    public void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.items = inventory;
    }

    public String getName() {
        return this.name;
    }

    public MutableText getTextName() {
        return Text.literal(this.getName()).formatted(Formatting.BOLD);
    }

    public void setName(String name) {
        this.name = name;
    }
}
