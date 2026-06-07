package com.soc.model;

import com.soc.SocWars;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public interface EntityModelLayers {
    static void initialise () {}

    static EntityModelLayer registerModelLayer(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        final EntityModelLayer layer = new EntityModelLayer(Identifier.of(SocWars.MOD_ID, id), id);
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    EntityModelLayer HOLY_HAND_GRENADE = registerModelLayer("holy_hand_grenade", HolyHandGrenadeModel::getTexturedModelData);
    EntityModelLayer HAND_GRENADE = registerModelLayer("hand_grenade", HandGrenadeModel::getTexturedModelData);
    EntityModelLayer MOLOTOV_COCKTAIL = registerModelLayer("molotov_cocktail", MolotovCocktailModel::getTexturedModelData);
    EntityModelLayer JET_SHOPPING_TROLLEY = registerModelLayer("jet_shopping_trolley", JetShoppingTrolleyModel::getTexturedModelData);
    EntityModelLayer POWERUP = registerModelLayer("powerup", PowerupModel::getTexturedModelData);
}
