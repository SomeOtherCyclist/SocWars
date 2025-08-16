package com.soc.items;

import com.soc.entities.BWFireballEntity;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.awt.*;
import java.util.function.Consumer;

import static com.soc.items.util.ModItems.addItemToGroups;

public class Fireball extends Item {
    public static World world;

    public enum FireballType {
        NORMAL,
        SNAIL,
        DRAGON,
        TNT,
    }

    private final FireballType fireballType;

    public Fireball(Settings settings, FireballType fireballType) {
        super(settings);
        this.fireballType = fireballType;
    }

    public static void initialise() {
        addItemToGroups(FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(DRAGON_FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(SNAIL_FIREBALL, ItemGroups.COMBAT);
        addItemToGroups(THROWABLE_TNT, ItemGroups.COMBAT);

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.LOAD.register((a, b) -> world = b);
    }

    public static final Item FIREBALL = ModItems.register("fireball", (settings) -> new Fireball(settings, FireballType.NORMAL), new Settings().useCooldown(0.75f));
    public static final Item DRAGON_FIREBALL = ModItems.register("dragon_fireball", (settings) -> new Fireball(settings, FireballType.DRAGON), new Settings().useCooldown(0.75f).rarity(Rarity.UNCOMMON));
    public static final Item SNAIL_FIREBALL = ModItems.register("snail_fireball", (settings) -> new Fireball(settings, FireballType.SNAIL), new Settings().useCooldown(0.75f).rarity(Rarity.EPIC));
    public static final Item THROWABLE_TNT = ModItems.register("throwable_tnt", (settings) -> new Fireball(settings, FireballType.TNT), new Settings().useCooldown(0.75f));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (world instanceof ServerWorld serverWorld) {
            Entity entity = switch (this.fireballType) {
                case NORMAL -> spawnEntityWithVelocity(new BWFireballEntity(world, user, Vec3d.ZERO, 3), serverWorld, user, 0.8f);
                case SNAIL -> spawnEntityWithVelocity(new BWFireballEntity(world, user, Vec3d.ZERO, 25), serverWorld, user, 0.2f);
                case DRAGON -> spawnEntityWithVelocity(new DragonFireballEntity(world, user, Vec3d.ZERO), serverWorld, user, 0.8f);
                case TNT -> spawnEntityWithVelocity(new TntEntity(EntityType.TNT, world), serverWorld, user, 0.6f);
            };

            if (this.fireballType == FireballType.TNT) {
                ((TntEntity)entity).setFuse(40);
            }
        }

        itemStack.decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    private Entity spawnEntityWithVelocity(Entity entity, ServerWorld world, PlayerEntity user, float speed) {
        entity.setPosition(user.getEyePos());
        entity.setVelocity(user.getRotationVector().multiply(speed));

        world.spawnEntity(entity);

        return entity;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Text text = switch (this.fireballType) {
            case NORMAL, TNT -> null;
            case SNAIL -> Text.literal("WHERE IS OMNI MAN").withColor(0xe6e475);
            case DRAGON -> {
                MutableText baseText = Text.literal("nuts across your face");
                if (world == null) {
                    yield baseText;
                }
                yield baseText.withColor(Color.HSBtoRGB(world.getTime() / 50f, 1f, 1f));
            }
        };

        if (text != null) {
            textConsumer.accept(text);
        }
    }
}
