package com.soc.items;

import com.soc.SocWars;
import com.soc.effects.AntiGravity;
import com.soc.effects.Flight;
import com.soc.items.util.ModItems;
import com.soc.items.util.UseFunction;
import com.soc.materials.ToolMaterials;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
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
        addItemToGroups(VELOCITY_STAFF, ItemGroups.TOOLS);
        addItemToGroups(VEXING_STAFF, ItemGroups.COMBAT);
        addItemToGroups(YELLOW_SWORD, ItemGroups.COMBAT);
        addItemToGroups(GRAVITY_ORB, ItemGroups.COMBAT);
        addItemToGroups(GOD_COMPLEX, ItemGroups.COMBAT);
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
    public static final Item VELOCITY_STAFF = ModItems.register("velocity_staff", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                float pitchClosenessToHorizontal = 1f - Math.abs(user.getPitch() / 90f);
                float pitchStrength = pitchClosenessToHorizontal * 0.5f + 0.5f;
                float dashStrength = (float) Math.sqrt(pitchStrength) * (user.isOnGround() ? 2f : 0.75f) * 0.85f;

                user.addVelocity(user.getRotationVector().multiply(dashStrength));

                user.getStackInHand(hand).damage(Math.round(pitchStrength * 10), user, hand);

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(300)
            .useCooldown(1.2f)
            .rarity(Rarity.RARE)
    );
    public static final Item VEXING_STAFF = ModItems.register("vexing_staff", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                for (int i = 0; i < 2; i++) {
                    VexEntity vex = new VexEntity(EntityType.VEX, world);
                    vex.setPosition(user.getEyePos().add(user.getRotationVector()));

                    Scoreboard scoreboard = world.getScoreboard();
                    if (user.getScoreboardTeam() == null) {
                        user.sendMessage(Text.literal("You are not assigned to a team, go yell at Liam"), false);
                    } else {
                        scoreboard.addScoreHolderToTeam(vex.getUuidAsString(), user.getScoreboardTeam());
                    }

                    world.spawnEntity(vex);

                    user.getStackInHand(hand).damage(1, user, hand);
                }

                return ActionResult.SUCCESS;
            }), new Settings()
            .maxDamage(5)
            .useCooldown(45f)
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
    public static final Item GRAVITY_ORB = ModItems.register("gravity_orb", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(AntiGravity.ANTI_GRAVITY, (int) 7.5 * 20, 2, false, false));

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(7.5f)
            .rarity(Rarity.UNCOMMON)
    );
    public static final Item GOD_COMPLEX = ModItems.register("god_complex", (settings) -> new UseFunctionWeapon(settings, (world, user, hand) -> {
                user.addStatusEffect(new StatusEffectInstance(Flight.FLIGHT, 5 * 20, 0, false, false));

                return ActionResult.SUCCESS;
            }), new Settings()
            .useCooldown(5f)
            .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .rarity(Rarity.EPIC)
    );

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        return useFunction.use(world, user, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        switch (stack.getItem().toString()) {
            case "socwars:knockforward_sword" -> textConsumer.accept(Text.literal("The Hypixel special").formatted(Formatting.GOLD));
            case "socwars:god_complex" -> textConsumer.accept(Text.translatable("tooltip.god_complex"));
        };
    }
}