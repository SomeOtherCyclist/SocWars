package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ModItems;
import com.soc.items.util.UseFunction;
import com.soc.materials.ToolMaterials;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

public class UseFunctionWeapon extends Item {
    private final UseFunction useFunction;

    public UseFunctionWeapon(Settings settings, UseFunction useFunction) {
        super(settings);
        this.useFunction = useFunction;
    }

    public static void initialise() {
        addItemToGroups(DASHREND, ItemGroups.COMBAT);
        addItemToGroups(YELLOW_SWORD, ItemGroups.COMBAT);
    }

    public static final Item DASHREND = ModItems.register("dashrend", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.5f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

                ItemStack item = user.getStackInHand(hand);
                item.damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.DASH, 2f, -2f)
            .useCooldown(3.5f)
            .rarity(Rarity.RARE)
    );
    public static final Item YELLOW_SWORD = ModItems.register("yellow_sword", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                ItemStack itemStack = user.getStackInHand(hand);

                if (world instanceof ServerWorld serverWorld) {
                    SpectralArrowEntity arrow = ProjectileEntity.spawn(new SpectralArrowEntity(world, user, Items.AIR.getDefaultStack(), itemStack), serverWorld, itemStack);
                    arrow.setPosition(user.getEyePos());
                    arrow.setVelocity(user.getRotationVector().multiply(2f));
                }

                itemStack.damage(3, user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .sword(ToolMaterials.BASE, 6f, -2.1f)
            .useCooldown(1.5f)
            .maxDamage(600)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return useFunction.use(world, user, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Text text = switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> Text.literal("The Hypixel special").formatted(Formatting.GOLD);
            default -> null;
        };

        SocWars.LOGGER.info(stack.getItem().toString());

        if (text != null) {
            textConsumer.accept(text);
        }
    }
}
