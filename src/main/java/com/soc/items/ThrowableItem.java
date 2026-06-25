package com.soc.items;

import com.soc.entities.*;
import com.soc.entities.util.ModEntities;
import com.soc.items.util.AppendTooltipFunction;
import com.soc.items.util.ModItems;
import com.soc.items.util.SpawnThrowableItemFunction;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;
import static net.minecraft.item.ItemGroups.COMBAT;

public class ThrowableItem extends Item {
    private final SpawnThrowableItemFunction spawnFunction;
    private final AppendTooltipFunction tooltipFunction;

    public ThrowableItem(Settings settings, SpawnThrowableItemFunction spawnFunction, AppendTooltipFunction tooltipFunction) {
        super(settings);
        this.spawnFunction = spawnFunction;
        this.tooltipFunction = tooltipFunction;
    }

    public ThrowableItem(Settings settings, SpawnThrowableItemFunction spawnFunction) {
        this(settings, spawnFunction, (a, b) -> {});
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(FIREBALL, COMBAT);
        addItemToGroupsAndBaseItemGroup(WATERBALL, COMBAT);
        addItemToGroupsAndBaseItemGroup(DRAGON_FIREBALL, COMBAT);
        addItemToGroupsAndBaseItemGroup(SNAIL_FIREBALL, COMBAT);
        addItemToGroupsAndBaseItemGroup(LIGHTNING_ORB, COMBAT);
        addItemToGroupsAndBaseItemGroup(THROWABLE_TNT, COMBAT);
        addItemToGroupsAndBaseItemGroup(ENDER_BEAM, COMBAT);
        addItemToGroupsAndBaseItemGroup(HAND_GRENADE, COMBAT);
        addItemToGroupsAndBaseItemGroup(HOLY_HAND_GRENADE, COMBAT);
        addItemToGroupsAndBaseItemGroup(MOLOTOV_COCKTAIL, COMBAT);
        addItemToGroupsAndBaseItemGroup(POCKET_SAND, COMBAT);
    }

    public static final Item FIREBALL = ModItems.register("fireball", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new BWFireballEntity(ModEntities.FIREBALL, world, user, Vec3d.ZERO, 4f, BWFireballEntity::fireballExplosion), world, user, 1.75f)), new Settings().useCooldown(0.75f));
    public static final Item WATERBALL = ModItems.register("waterball", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new BWFireballEntity(ModEntities.WATERBALL, world, user, Vec3d.ZERO, 0f, BWFireballEntity::waterballExplosion), world, user, 1.75f)), new Settings().useCooldown(0.75f));
    public static final Item SNAIL_FIREBALL = ModItems.register("snail_fireball", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new BWFireballEntity(ModEntities.SNAIL_FIREBALL, world, user, Vec3d.ZERO, 20f, BWFireballEntity::snailExplosion), world, user, 0.2f), (stack, consumer) -> consumer.accept(Text.translatable("tooltip.snail_fireball").withColor(0xe6e475))), new Settings().useCooldown(0.75f).rarity(Rarity.EPIC));
    public static final Item LIGHTNING_ORB = ModItems.register("lightning_orb", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new BWFireballEntity(ModEntities.LIGHTNING_ORB, world, user, Vec3d.ZERO, 0f, BWFireballEntity::lightningOrbExplosion), world, user, 2.0f)), new Settings().useCooldown(0.75f).rarity(Rarity.UNCOMMON));
    public static final Item DRAGON_FIREBALL = ModItems.register("dragon_fireball", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new DragonFireballEntity(world, user, Vec3d.ZERO), world, user, 1.5f), (stack, consumer) -> consumer.accept(Text.translatable("tooltip.dragon_fireball").withColor(Color.HSBtoRGB(getWorldTime() / 50f, 1f, 1f)))), new Settings().useCooldown(0.75f).rarity(Rarity.UNCOMMON));
    public static final Item THROWABLE_TNT = ModItems.register("throwable_tnt", settings -> new ThrowableItem(settings, (world, user) -> {
        final TntEntity tnt = spawnEntityWithVelocity(new TntEntity(world, 0d, 0d, 0d, user), world, user, 0.6f);
        tnt.setFuse(30);
    }), new Settings().useCooldown(0.75f));
    public static final Item ENDER_BEAM = ModItems.register("ender_beam", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new EnderBeamEntity(world, user), world, user, 1.75f)), new Settings().useCooldown(0.75f));
    public static final Item HAND_GRENADE = ModItems.register("hand_grenade", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new HandGrenadeEntity(ModEntities.HAND_GRENADE, world, 0.5f, user), world, user, 0.65f), (stack, consumer) -> consumer.accept(Text.translatable("tooltip.hand_grenade"))), new Settings().useCooldown(1f).rarity(Rarity.UNCOMMON));
    public static final Item HOLY_HAND_GRENADE = ModItems.register("holy_hand_grenade", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new HolyHandGrenadeEntity(ModEntities.HOLY_HAND_GRENADE, world, 0.5f, user), world, user, 0.65f), (stack, consumer) -> {
        for (int i = 0; i < 10; i++) {
            consumer.accept(Text.translatable("tooltip.holy_hand_grenade." + i));
        }
    }), new Settings().useCooldown(3f).rarity(Rarity.RARE));
    public static final Item MOLOTOV_COCKTAIL = ModItems.register("molotov_cocktail", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new MolotovCocktailEntity(ModEntities.MOLOTOV_COCKTAIL, world, user), world, user, 0.7f)), new Settings().useCooldown(0.5f).rarity(Rarity.UNCOMMON));
    public static final Item POCKET_SAND = ModItems.register("pocket_sand", settings -> new ThrowableItem(settings, (world, user) -> spawnEntityWithVelocity(new PocketSandEntity(ModEntities.POCKET_SAND, world), world, user, 0.9f), ((stack, consumer) -> consumer.accept(Text.translatable("tooltip.pocket_sand")))), new Settings().useCooldown(0.5f));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        final ItemStack itemStack = user.getStackInHand(hand);

        if (world instanceof ServerWorld serverWorld) {
            this.spawnFunction.spawn(serverWorld, user);
        }

        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    public static Long getWorldTime() { //Refactor this at some point because it also gets used in AttackFunctionWeapon
        //I'm setting this in a client mixin because I'm too lazy to think of a better way to do it
        return 0L;
    }

    public static <T extends Entity> T spawnEntityWithVelocity(T entity, ServerWorld world, LivingEntity user, float speed) {
        entity.setPosition(user.getEyePos().add(user.getRotationVector().multiply(0.75d)));
        entity.setVelocity(user.getRotationVector().multiply(speed));

        world.spawnEntity(entity);

        return entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        this.tooltipFunction.appendTooltip(stack, textConsumer);
    }
}
