package com.soc.renderer;

import com.soc.SocWars;
import com.soc.entities.util.ModEntities;
import com.soc.game.manager.bedwars.ShopType;
import com.soc.renderer.entity.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public interface EntityRenderers {
    static void initialise() {
        EntityRendererRegistry.register(ModEntities.NUCLEAR_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.HYDROGEN_BOMB, BigTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIREBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.ofVanilla("textures/item/fire_charge.png")));
        EntityRendererRegistry.register(ModEntities.WATERBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/waterball.png")));
        EntityRendererRegistry.register(ModEntities.SNAIL_FIREBALL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/snail_fireball.png")));
        EntityRendererRegistry.register(ModEntities.LIGHTNING_ORB, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/lightning_orb.png")));
        EntityRendererRegistry.register(ModEntities.INDIVIDUAL_BEDWARS_SHOP, context -> new BedwarsShopEntityRenderer(context, ShopType.INDIVIDUAL));
        EntityRendererRegistry.register(ModEntities.TEAM_BEDWARS_SHOP, context -> new BedwarsShopEntityRenderer(context, ShopType.TEAM));
        EntityRendererRegistry.register(ModEntities.HAND_GRENADE, HandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.HOLY_HAND_GRENADE, HolyHandGrenadeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MOLOTOV_COCKTAIL, MolotovCocktailEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.RED_SHELL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/red_shell.png")));
        EntityRendererRegistry.register(ModEntities.BLUE_SHELL, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/blue_shell.png")));
        EntityRendererRegistry.register(ModEntities.ENDER_BEAM, context -> new SimpleBillboardEntityRenderer(context, Identifier.of(SocWars.MOD_ID, "textures/item/ender_beam.png")));
        EntityRendererRegistry.register(ModEntities.POCKET_SAND, context -> new SimpleCubeEntityRenderer(context, Blocks.SAND.getDefaultState(), 0.3f));
        EntityRendererRegistry.register(ModEntities.JET_SHOPPING_TROLLEY, JetShoppingTrolleyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.POWERUP, PowerupEntityRenderer::new);
    }
}
