package com.soc.resourcedata.containers;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.*;
import com.soc.game.manager.bedwars.shopitems.*;
import com.soc.resourcedata.deserialisation.BedwarsShopSlot;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.registry.tag.ItemTags.BOW_ENCHANTABLE;

public class BedwarsShopDataContainer implements CachedData {

    private BedwarsShopDataContainer() {}

    public static final BedwarsShopDataContainer INSTANCE = new BedwarsShopDataContainer();

    private final Map<Identifier, ShopItem<?>> resourceItemMap = new HashMap<>();
    private final Map<Identifier, PreSelectionBedwarsShopCategory> categoryStockSlotsMap = new TreeMap<>();
    private PreSelectionBedwarsShopCategory[] teamStockCategories = new PreSelectionBedwarsShopCategory[3];

    public final void addSlotResource(Identifier id, ShopItem<?> shopItem) {
        this.resourceItemMap.put(id, shopItem);
    }

    public final void addCategorySlot(Identifier id, PreSelectionBedwarsShopCategory stockSlots) {
        this.categoryStockSlotsMap.put(id, stockSlots);
    }

    public final void setTeamStockCategory(int index, PreSelectionBedwarsShopCategory stockSlots) {
        this.teamStockCategories[index] = stockSlots;
    }

    public final BedwarsShopContents getIndividualBedwarsShop(long shopSeed, DyeColor team, World world) {
        final Random random = Random.create(shopSeed);
        final List<BedwarsShopCategory> categories = this.categoryStockSlotsMap.entrySet().stream().map(entry -> new BedwarsShopCategory(
                this.resolveItems(entry.getValue(), random, team, BedwarsIndividualShopScreenHandler.STOCK_WIDTH, BedwarsIndividualShopScreenHandler.STOCK_HEIGHT, world),
                entry.getValue().icon(),
                entry.getValue().name()
        ))
                .limit(BedwarsIndividualShopScreenHandler.CATEGORIES_WIDTH * BedwarsIndividualShopScreenHandler.CATEGORIES_HEIGHT - 1)
                .collect(Collectors.toList());

        categories.addFirst(BedwarsShopCategory.DEFAULT_QUICK_BUY);

        return new BedwarsShopContents(categories);
    }

    public final BedwarsShopContents getTeamBedwarsShop(long shopSeed, DyeColor team, World world) {
        final Random random = Random.create(shopSeed);
        final List<BedwarsShopCategory> categories = new ArrayList<>();
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[2], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT, world), Items.ARROW.getDefaultStack(), Text.of("Upgrades")));
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[0], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT, world), Items.TRIPWIRE_HOOK.getDefaultStack(), Text.of("Traps")));
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[1], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT, world), Items.ANVIL.getDefaultStack(), Text.of("Abilities")));

        return new BedwarsShopContents(categories);
    }

    private List<ShopItem<?>> resolveItems(PreSelectionBedwarsShopCategory preSelectionCategory, Random random, DyeColor team, int width, int height, World world) {
        final BedwarsShopSlot[][] preSelection = preSelectionCategory.contents();
        final int itemsSize = width * height;
        final List<ShopItem<?>> items = new ArrayList<>(itemsSize);
        for (int i = 0; i < itemsSize; i++) {
            items.add(SimpleShopItem.EMPTY);
        }

        for (int i = 0; i < preSelection.length; i++) {
            for (int j = 0; j < preSelection[i].length; j++) {
                final BedwarsShopSlot shopSlot = preSelection[i][j];
                final int index = i + j * width;

                if (index >= items.size()) {
                    SocWars.LOGGER.warn("Skipped loading shop item {}, {} as it is out of bounds", i, j);
                    continue;
                }

                if (shopSlot != null) items.set(index, this.resolveRandomItem(shopSlot, random, team, world));
            }
        }

        return items;
    }

    private ShopItem<?> resolveRandomItem(BedwarsShopSlot slot, Random random, DyeColor team, World world) {
        final List<Identifier> options = slot.options();
        if (options.isEmpty()) {
            SocWars.LOGGER.warn("Skipping loading slot as the options pool was empty");
            return SimpleShopItem.EMPTY;
        }

        final Identifier choice = options.get(random.nextBetween(0, options.size() - 1));
        final ShopItem<?> item = this.resourceItemMap.get(choice);
        final ShopItem<?> lazilyClonedItem = item == null ? SimpleShopItem.EMPTY : (ShopItem<?>)item.lazyClone();
        if (lazilyClonedItem instanceof TeamItem teamItem) {
            teamItem.setTeam(team);
        }
        if (lazilyClonedItem instanceof SimpleShopItem simpleShopItem) {
            simpleShopItem.trim(team, world);
            if (simpleShopItem.getIcon().isIn(BOW_ENCHANTABLE)) {
                final Registry<Enchantment> enchantmentRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
                simpleShopItem.getIcon().addEnchantment(enchantmentRegistry.getOrThrow(Enchantments.INFINITY), 1);
            }
            simpleShopItem.getIcon().set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
        }

        return lazilyClonedItem;
    }

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.resourceItemMap.clear();
        this.categoryStockSlotsMap.clear();
        this.teamStockCategories = new PreSelectionBedwarsShopCategory[3];
    }
}
