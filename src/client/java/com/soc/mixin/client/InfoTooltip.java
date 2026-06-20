package com.soc.mixin.client;

import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.ifNotNull;

@Mixin(ItemStack.class)
abstract class InfoTooltip {
	@Inject(method = "appendTooltip", at = @At("TAIL"))
	private void appendInfoTooltip(Item.TooltipContext context, TooltipDisplayComponent displayComponent, @Nullable PlayerEntity player, TooltipType type, Consumer<Text> textConsumer, CallbackInfo ci) {
		ifNotNull(((ComponentHolder)(this)).get(DataComponentTypes.ITEM_NAME), name -> {
			final TextContent content = name.getContent();
			if (content.getType() != TranslatableTextContent.TYPE) return;

			final String infoKey = ((TranslatableTextContent)content).getKey() + ".info";
			if (Language.getInstance().hasTranslation(infoKey)) {
				textConsumer.accept(Text.empty());
				textConsumer.accept(Text.translatable(infoKey));
			}
		});
	}
}
