package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.game.manager.BedwarsGameManager;
import com.soc.game.manager.GamesManager;
import com.soc.game.manager.bedwars.ShopType;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.OptionalInt;

import static com.soc.lib.SocWarsLib.damageSource;

public class BedwarsShopEntity extends LivingEntity {
    private static final String SHOP_TYPE_STRING = "shop_type";

    static {
        FabricDefaultAttributeRegistry.register(ModEntities.INDIVIDUAL_BEDWARS_SHOP, BedwarsShopEntity.createBedwarsShopAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TEAM_BEDWARS_SHOP, BedwarsShopEntity.createBedwarsShopAttributes());
    }

    private ShopType shopType;

    public BedwarsShopEntity(EntityType<? extends BedwarsShopEntity> entityType, World world, ShopType shopType) {
        super(entityType, world);
        this.shopType = shopType;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static BedwarsShopEntity spawnWithPos(World world, Vec3d pos, ShopType shopType) {
        final BedwarsShopEntity entity = new BedwarsShopEntity(shopType.getEntityType(), world, shopType);
        entity.setPosition(pos);
        world.spawnEntity(entity);
        return entity;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.shopType = ShopType.fromOrdinal(view.getInt(SHOP_TYPE_STRING, 0));
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt(SHOP_TYPE_STRING, this.shopType.ordinal());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (hand == Hand.OFF_HAND || GamesManager.getInstance().getGame(player).isEmpty()) return ActionResult.PASS;

        if (player instanceof ServerPlayerEntity serverPlayer) {
            final OptionalInt syncId = player.openHandledScreen(new SimpleNamedScreenHandlerFactory(this.shopType.getFactory(), Text.translatable("game.bedwars.shop." + this.shopType.toString().toLowerCase())));
            BedwarsGameManager.sendShopData(serverPlayer, syncId, this.shopType);
        }
        return player.distanceTo(this) < 10 ? ActionResult.CONSUME : ActionResult.PASS;
    }

    //region Make Invulnerable and Immobile
    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }

    @Override
    public void tickMovement() {}

    @Override
    public boolean isInvulnerable() {
        return this.getPos().x > -256d;
    }

    @Override
    public void kill(ServerWorld world) {
        this.discard();
    }
    //endregion

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (source.getSource() instanceof ServerPlayerEntity player) {
            player.damage(player.getWorld(), damageSource(this.getWorld(), DamageTypes.BAD_RESPAWN_POINT, this), 8f);
            if (world.random.nextFloat() < 0.5f) {
                player.dropItem(player.getStackInHand(Hand.MAIN_HAND), false, false);
                player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        }
        return false;
    }

    public static DefaultAttributeContainer.Builder createBedwarsShopAttributes() {
        return LivingEntity.createLivingAttributes();
    }
}

