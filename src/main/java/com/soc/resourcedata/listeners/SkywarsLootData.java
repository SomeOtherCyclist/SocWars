package com.soc.resourcedata.listeners;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import com.soc.resourcedata.containers.SkywarsLootDataContainer;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import com.soc.resourcedata.deserialisation.SkywarsItemData;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import static com.soc.resourcedata.ResourceManager.BASE_PATH_PREDICATE;
import static com.soc.resourcedata.ResourceManager.readResources;
import static com.soc.resourcedata.deserialisation.SkywarsItemData.POOL_KEY;

public class SkywarsLootData implements SimpleSynchronousResourceReloadListener {
    public static final SkywarsLootData INSTANCE = new SkywarsLootData();

    public static final String ITEM_ID_KEY = "id";

    private final SkywarsLootDataContainer skywarsItemDataContainer = new SkywarsLootDataContainer();
    public SkywarsLootDataContainer getSkywarsItemData() { return this.skywarsItemDataContainer; }

    private SkywarsLootData() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "item_data_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.skywarsItemDataContainer.clear();

        readResources(manager, "skywars_item_data", BASE_PATH_PREDICATE, (reader, id) -> {
            switch(id.toString()) {
                case "socwars:skywars_item_data/population.json" -> this.skywarsItemDataContainer.readPoolPopulations(reader);
            }
        });

        readResources(manager, "skywars_item_data/pools", BASE_PATH_PREDICATE, (reader, id) -> {
            for (JsonElement jsonElement : JsonHelper.deserializeArray(reader)) {
                final JsonObject object = jsonElement.getAsJsonObject();
                final Item item = Registries.ITEM.get(Identifier.of(object.get(ITEM_ID_KEY).getAsString()));
                final Integer pool = object.has(POOL_KEY) ? object.get(POOL_KEY).getAsInt() : 0;

                if (item == Items.AIR) SocWars.LOGGER.warn("Skipped loading weights for {} as there is no corresponding registered item", object.get(ITEM_ID_KEY).getAsString());

                final SkywarsItemData skywarsItemData = new SkywarsItemData(jsonElement.getAsJsonObject());

                this.skywarsItemDataContainer.acceptData(skywarsItemData, pool, item);
            }
        });

        this.skywarsItemDataContainer.cache();
    }
}
