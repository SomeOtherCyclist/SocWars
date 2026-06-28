package com.soc.game.manager;

import com.google.common.collect.*;
import com.soc.database.stats.BedwarsTable;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.game.manager.bedwars.ShopType;
import com.soc.game.manager.bedwars.TeamStats;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.traps.AbstractAbility;
import com.soc.game.manager.bedwars.traps.AbstractTrap;
import com.soc.game.manager.bedwars.traps.TrapManager;
import com.soc.game.map.*;
import com.soc.items.components.ModComponents;
import com.soc.lib.Events;
import com.soc.networking.helper.BedwarsTeam;
import com.soc.networking.s2c.bedwars.LeaveBedwarsPayload;
import com.soc.networking.s2c.TeamEliminatedPayload;
import com.soc.networking.s2c.bedwars.*;
import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.resourcedata.deserialisation.IslandGeneratorUpgrade;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;
import com.soc.util.ModItemTags;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.manager.bedwars.traps.TrapManager.TRAP_DETECTION_RANGE;
import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.*;
import static net.minecraft.item.Items.*;

public class BedwarsGameManager extends AbstractGameManager<BedwarsGameMap, BedwarsTable, BedwarsGameManager> implements TrapGame {
    protected static final Item[] RESOURCES = { IRON_INGOT, GOLD_INGOT, Items.DIAMOND, Items.EMERALD };
    protected static final Map<Item, Formatting> RESOURCE_TO_COLOUR_MAP = Map.of(
            IRON_INGOT, Formatting.GRAY,
            GOLD_INGOT, Formatting.GOLD,
            DIAMOND, Formatting.BLUE,
            EMERALD, Formatting.GREEN
    );
    @SuppressWarnings("DataFlowIssue")
    private static final Map<ComponentType<?>, TriConsumer<ItemStack, ServerPlayerEntity, BedwarsGameManager>> ITEM_PICKUP_COMPONENT_FUNCTION_MAP = Map.of(
            ModComponents.RESOURCE_SPLIT, (stack, player, manager) -> {
                stack.remove(ModComponents.RESOURCE_SPLIT);
                if (manager.map.isWithinSplitRange(player)) {
                    manager.getPlayers().stream().filter(pickUpPlayer -> manager.map.isWithinSplitRange(pickUpPlayer) && pickUpPlayer.isTeammate(player) && pickUpPlayer != player).forEach(otherPlayer -> {
                        final ItemStack giveStack = stack.copy();
                        manager.onItemPickup(otherPlayer, giveStack);
                        otherPlayer.giveOrDropStack(giveStack);
                    });
                }
            },
            ModComponents.RESOURCE_COUNTED, (stack, player, manager) -> {
                stack.remove(ModComponents.RESOURCE_COUNTED);
                manager.getDbTable(player).collectItem(stack);
            },
            ModComponents.GENERATOR_REFERENCE, (stack, player, manager) -> {
                manager.map.getResourceGenerator(stack.get(ModComponents.GENERATOR_REFERENCE)).ifPresent(ResourceGenerator::checkItemCap);
                stack.remove(ModComponents.GENERATOR_REFERENCE);
            }
    );

    private final Map<UUID, PlayerStats> playerStatsMap;
    private final Map<DyeColor, TeamStats> teamStatsMap;

    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(GameType.BEDWARS, world, players, spreadRules, gameId);

        final long shopSeed = world.random.nextLong();
        this.playerStatsMap = players.stream().collect(Collectors.toMap(ServerPlayerEntity::getUuid, player -> new PlayerStats(player, this.getTeam(player), shopSeed)));
        this.teamStatsMap = this.teams.keySet().stream().collect(Collectors.toMap(Function.identity(), team -> new TeamStats(team, this.teams.get(team).stream().map(this.playerStatsMap::get).collect(Collectors.toSet()), world, shopSeed, this.map.getSpawnPositions(team))));
    }

    @Override
    protected BedwarsGameMap buildMap() {
        final Optional<BedwarsGameMap> map = AbstractGameMap.loadRandomMap(this.world, this.generateCentrePosition(), BedwarsGameMap::fromNbt, BedwarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Bedwars map found");

        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();

        this.map.getBedPositions().forEach((team, pos) -> {
            if (!this.teams.containsKey(team)) {
                this.world.breakBlock(this.map.pos(pos), false);
            }
        });
    }

    @Override
    protected void onFinishCountdown() {
        super.onFinishCountdown();

        for (ServerPlayerEntity player : this.getPlayers()) {
            final DyeColor team = this.getTeam(player);

            for (EquipmentSlot equipmentSlot : ARMOUR_SLOTS) {
                if (!player.getEquippedStack(equipmentSlot).isEmpty()) continue;

                final ItemStack armour = new ItemStack(leatherArmour(equipmentSlot));
                armour.addEnchantment(enchantmentEntry(player.getWorld(), Enchantments.BINDING_CURSE), 1);
                armour.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
                armour.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(team.getEntityColor()));

                player.equipStack(equipmentSlot, armour);
            }
            player.giveItemStack(new ItemStack(Items.WOODEN_SWORD));
        }
    }

    @Override
    public void endGame(boolean immediate) {
        this.eventQueue.cancelEvents();
        this.setGameMode(GameMode.SPECTATOR);

        final Collection<DyeColor> winningTeams = this.getWinningTeams();

        this.playerStatsMap.forEach((uuid, stats) -> {
            final Text message;
            final SoundEvent sound;
            final BedwarsTable dbTable = this.dbTables.get(uuid);
            if (winningTeams.contains(this.getTeam(stats.getPlayer())) && stats.isAlive()) {
                message = Text.translatable("game.bedwars.win");
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.bedwars.lose");
                sound = SoundEvents.BLOCK_BELL_USE;
                dbTable.lose();
            }

            Events.getInstance().scheduleEvent(() -> {
                if (this.world.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(new TitleS2CPacket(message));

                    player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                }
            }, 10);
        });

        if (immediate) {
            super.endGame(true);
        } else {
            Events.getInstance().scheduleEvent(() -> super.endGame(false), 5 * 20);
        }
    }

    private Collection<DyeColor> getWinningTeams() {
        final List<Map.Entry<DyeColor, TeamStats>> entries = new ArrayList<>(this.teamStatsMap.entrySet()); //Want a nicely ordered list to ensure stable sorts
        final List<Map.Entry<DyeColor, TeamStats>> sortedEntries = entries.stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getNumPlayersAlive())).sorted(Comparator.comparingInt(entry -> entry.getValue().hasBed() ? 1 : 0)).toList();

        final TeamStats finalEntry = sortedEntries.getLast().getValue();
        int i = sortedEntries.size() - 1;
        for (; i >= 0; i--) {
            final TeamStats entry = sortedEntries.get(i).getValue();
            if ((finalEntry.hasBed() && !entry.hasBed()) || finalEntry.getNumPlayersAlive() > entry.getNumPlayersAlive()) break;
        }

        return sortedEntries.subList(i + 1, sortedEntries.size()).stream().map(Map.Entry::getKey).toList();
    }

    protected final PlayerStats getPlayerStats(ServerPlayerEntity player) {
        return this.playerStatsMap.get(player.getUuid());
    }

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        final Stack<UUID> playerStack = getRandomPlayerStack(players);

        Set<DyeColor> teamColours = this.map.getTeamColours();

        teamColours = teamColours.stream().limit(Math.max(2, (int)Math.ceil(players.size() / 4d))).collect(Collectors.toSet());

        final int numTeams = Math.min(spreadRules.numTeams(), teamColours.size());

        final ImmutableMultimap.Builder<DyeColor, UUID> builder = ImmutableMultimap.builder();

        final ArrayList<DyeColor> teamsUnlimited = new ArrayList<>(teamColours);
        Collections.shuffle(teamsUnlimited);
        final List<DyeColor> teams = teamsUnlimited.stream().limit(numTeams).toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teams.get(i % numTeams), playerStack.pop());
        }

        return builder.build();
    }

    @Override
    protected EventQueue<BedwarsGameManager> buildEventQueue() {
        final EventQueue<BedwarsGameManager> queue = super.buildEventQueue();

        final BedwarsGeneratorDataContainer bedwarsGeneratorDataContainer = BedwarsGeneratorDataContainer.INSTANCE;
        for (int i = 0; i < bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().size(); i++) {
            final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().get(i); //TODO: Redo these texts, they are kind of ugly at the moment
            queue.addEvent(upgrade.time(), manager -> manager.upgradeDiamondGens(upgrade.getStats()), Text.translatable("events.bedwars.diamond_generator", romanNumeralsText(i).formatted(Formatting.AQUA)));
        }
        for (int i = 0; i < bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().size(); i++) {
            final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().get(i);
            queue.addEvent(upgrade.time(), manager -> manager.upgradeEmeraldGens(upgrade.getStats()), Text.translatable("events.bedwars.emerald_generator", romanNumeralsText(i).formatted(Formatting.DARK_GREEN)));
        }
        for (int i = 0; i < bedwarsGeneratorDataContainer.getIslandGeneratorUpgrades().size(); i++) {
            final IslandGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getIslandGeneratorUpgrades().get(i);
            int finalI = i;
            queue.addEvent(upgrade.autoUpgradeTime(), manager -> this.teamStatsMap.forEach((team, stats) -> {
                if (stats.getNumPlayersAlive() == 0) manager.buyGeneratorUpgrade(team, finalI);
            }), Text.translatable("events.bedwars.island_generator", romanNumeralsText(i).formatted(Formatting.GOLD)));
        }

        return queue;
    }

    @Override
    protected Function<UUID, BedwarsTable> dbTableBuilder() {
        return BedwarsTable::new;
    }

    @Override
    protected void sendJoinGamePayload(ServerPlayerEntity player) {
        super.sendJoinGamePayload(player);
        ServerPlayNetworking.send(player, new JoinBedwarsPayload(this.getGameId(), this.teamStatsMap.values().stream().collect(Collectors.toMap(TeamStats::getTeam, BedwarsTeam::new))));
    }

    @Override
    protected void sendLeaveGamePayload(ServerPlayerEntity player) {
        super.sendLeaveGamePayload(player);
        ServerPlayNetworking.send(player, new LeaveBedwarsPayload());
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        if (player.isSpectator()) return false;

        final DyeColor team = this.getTeam(player);
        if (!this.teamStatsMap.get(team).getTrapManager().onPlayerDeath(player, this)) return false;

        final boolean canRespawn = this.canRespawn(player);
        this.broadcastDeath(player, source, !canRespawn);

        getPlayerAttacker(player).ifPresentOrElse(attacker -> this.giveResourcesToPlayer(player, (ServerPlayerEntity) attacker), () -> dropResources(player));

        final PlayerStats playerStats = this.getPlayerStats(player);
        playerStats.onDeath(canRespawn, this.world);

        player.getInventory().getMainStacks().clear();
        player.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);

        playerStats.returnToolsToSlots(this.world);

        if (!this.teamStatsMap.get(team).isAlive()) this.onTeamElimination(team);

        super.onPlayerDeath(player, source, amount);

        if (this.getAliveTeams().size() < (this.teams.keySet().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> this.respawnPlayer(player), this, 5, 20, SoundEvents.BLOCK_FUNGUS_STEP, true, player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars.eliminate")));
        }

        return false;
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.teamStatsMap.get(this.getTeam(player)).hasBed();
    }

    protected Set<ServerPlayerEntity> getAlivePlayers() {
        return this.playerStatsMap.values().stream().filter(PlayerStats::isAlive).map(playerStats -> (ServerPlayerEntity)this.world.getPlayerByUuid(playerStats.getPlayer())).collect(Collectors.toSet());
    }

    protected Set<DyeColor> getAliveTeams() {
        return this.teamStatsMap.values().stream().filter(TeamStats::isAlive).map(TeamStats::getTeam).collect(Collectors.toSet());
    }

    @Override
    protected void trackDeathStats(ServerPlayerEntity player, DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) this.getDbTable(player).fallInVoid();

        final BedwarsTable targetTable = this.getDbTable(player);

        final boolean isFinal = !this.canRespawn(player);

        if (isFinal) {
            targetTable.grantFinalDeath();
        } else {
            targetTable.grantDeath();
        }

        getPlayerAttacker(player).ifPresent(killer -> {
            final BedwarsTable killerTable = this.getDbTable(killer);
            if (killerTable == null) return;

            if (isFinal) {
                targetTable.grantFinalKill();
            } else {
                targetTable.grantKill();
            }
        });
    }

    protected void giveResourcesToPlayer(ServerPlayerEntity giver, ServerPlayerEntity receiver) {
        final BedwarsTable table = this.getDbTable(receiver);
        for (Item resource : RESOURCES) {
            final int count = giver.getInventory().count(resource);
            final ItemStack stack = new ItemStack(resource, count);

            table.collectItem(stack);
            receiver.giveItemStack(stack);

            if (count > 0) receiver.sendMessage(Text.translatable("game.bedwars.receive_items", count, resource.getDefaultStack().toHoverableText().copy().formatted(RESOURCE_TO_COLOUR_MAP.get(resource))).formatted(Formatting.DARK_GREEN), false);
        }
    }

    protected static void dropResources(ServerPlayerEntity player) {
        for (Item resource : RESOURCES) {
            player.getInventory().forEach(stack -> {
                if (stack.isOf(resource)) player.dropStack(player.getWorld(), stack);
            });
        }
    }

    @Override
    public boolean onChestOpened(ServerPlayerEntity player, BlockPos pos) {
        final Optional<Pair<DyeColor, BlockPos>> closestSpawn = this.map.getClosestSpawn(pos);

        final boolean allow = closestSpawn.map(entry -> {
                final boolean isSameTeam = entry.getLeft() == this.getTeam(player);
                final boolean isClosestTeamDead = !this.teamStatsMap.containsKey(entry.getLeft()) || !this.teamStatsMap.get(entry.getLeft()).isAlive();
                final boolean isChestTooFar = !entry.getRight().isWithinDistance(pos, 20); //Gross check since I only keep track of teams that exist
                return isSameTeam || isClosestTeamDead || isChestTooFar;
        }).orElse(true);

        if (!allow) {
            player.sendMessage(Text.translatable("game.bedwars.chest_locked", colouredTextFromColour(closestSpawn.get().getLeft())));
        }

        return allow;
    }

    @Override
    public void onItemPickup(ServerPlayerEntity player, ItemStack stack) {
        ITEM_PICKUP_COMPONENT_FUNCTION_MAP.forEach((component, function) -> {
            if (stack.get(component) != null) function.accept(stack, player, this);
        });
        if (stack.isIn(ItemTags.SWORDS) && !stack.isOf(Items.WOODEN_SWORD)) player.getInventory().remove(stack2 -> stack2.isOf(Items.WOODEN_SWORD), 40, player.playerScreenHandler.getCraftingInput());
    }

    @Override
    public boolean onItemDropped(ServerPlayerEntity player, ItemStack stack) {
        if (stack.isOf(WOODEN_SWORD)) return false;

        if (stack.isIn(ItemTags.SWORDS) && !player.getInventory().containsAny(stack2 -> stack2.isIn(ItemTags.SWORDS) && !stack2.isOf(Items.WOODEN_SWORD))) {
            final ItemStack woodenSword = new ItemStack(WOODEN_SWORD);
            woodenSword.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
            player.giveItemStack(woodenSword);
        }

		return !stack.isIn(ModItemTags.TOOLS);
	}

    @Override
    public boolean onBedBroken(ServerPlayerEntity player, BlockPos pos) {
        final Optional<DyeColor> bedTeamOptional = this.map.getBedPositions().entrySet().stream().filter(entry -> super.map.pos(entry.getValue()).isWithinDistance(pos, 2d)).findFirst().map(Map.Entry::getKey);

        return bedTeamOptional.map(bedTeam -> {
            if (bedTeam == this.getTeam(player) && player.getGameMode() == GameMode.SURVIVAL) {
                player.sendMessage(Text.translatable("game.bedwars.broke_own_bed", colouredTextFromColour(bedTeam)), false);
                return false;
            }

            final boolean brokeBed = this.teamStatsMap.get(bedTeam).breakBed();
            if (brokeBed) {
                this.broadcast(Text.translatable("game.bedwars.bed_broken.chat", colouredTextFromColour(bedTeam), player.getStyledDisplayName()), false);
                this.broadcastTitle(bedTeam, Text.translatable("game.bedwars.bed_broken.title").formatted(Formatting.DARK_RED), true);
                this.broadcastSound(bedTeam, SoundEvents.ENTITY_WITHER_SPAWN);
                player.playSoundToPlayer(Sounds.AIR_HORN, SoundCategory.PLAYERS, 1, 1);

                this.getDbTable(player).grantBedBreak();
                this.getPlayers(bedTeam).stream().map(this::getDbTable).forEach(BedwarsTable::loseBed);

                this.sendPayloadToPlayers(new BedBreakPayload(bedTeam));
            }

            return true;
        }).orElse(true);
    }

    @Override
    public boolean onCraftingTableOpened(ServerPlayerEntity player, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onFurnaceOpened(ServerPlayerEntity player, BlockPos pos) {
        return false;
    }

	@Override
	protected boolean hasTeamChat() {
		return true;
	}

	public BedwarsShopContents getIndividualShopContents(UUID player) {
        return this.playerStatsMap.get(player).getShopContents();
    }

    public BedwarsShopContents getTeamShopContents(UUID player) {
        return this.teamStatsMap.get(this.getTeam(player)).getShopContents();
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(UUID player) {
        return GamesManager.getInstance().getGame(player).map(manager -> manager instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(PlayerEntity player) {
        return GamesManager.getInstance().getGame(player).map(manager -> manager instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnusedReturnValue"})
    public static boolean sendShopData(ServerPlayerEntity player, OptionalInt syncId, ShopType shopType) {
        final BedwarsGameManager manager = getBedwarsGameManager(player);

        if (syncId.isEmpty() || !(player instanceof ServerPlayerEntity) || manager == null) return false;

        switch (shopType) {
            case INDIVIDUAL -> ServerPlayNetworking.send(player, new BedwarsIndividualShopDataPayload(manager.getIndividualShopContents(player.getUuid()), syncId.getAsInt()));
            case TEAM -> ServerPlayNetworking.send(player, new BedwarsTeamShopDataPayload(manager.getTeamShopContents(player.getUuid()), syncId.getAsInt(), manager.getTrapManager(manager.getTeam(player.getUuid())).getTrapProgressStats()));
        }

        return true;
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        this.map.upgradeDiamondGens(stats);
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {
        this.map.upgradeEmeraldGens(stats);
    }

    @Override
    public void tick() {
        super.tick();
        this.checkTraps();

        this.teamStatsMap.values().forEach(stats -> stats.tick(this.time, this.world));
    }

	@Override
	protected void respawnPlayer(ServerPlayerEntity player) {
		super.respawnPlayer(player);
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 4, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 5 * 20, 1, false, false));
	}

	@Override
    @Nullable
    public Entity getWinningPlayer(@Nullable Entity except) {
        final DyeColor team = except == null ? null : this.getTeam(except.getUuid());

        return this.dbTables.values().stream().filter(table -> this.getTeam(table.getPlayer()) != team).max(Comparator.comparingInt(BedwarsTable::getKills)).map(table -> this.world.getPlayerByUuid(table.getPlayer())).orElse(null);
    }

    public void checkTraps() { //TODO: Maybe make this into a default impl in the TrapGame interface
        this.teamStatsMap.forEach((team, stats) -> {
            if (!stats.hasActiveTrap()) return;

            final Vec3d pos = this.map.getBedPosition(team).toCenterPos();
            final Multimap<DyeColor, ServerPlayerEntity> enemiesInRange = this.getPlayers()
                    .stream()
                    .filter(player -> this.getTeam(player) != team && player.getPos().isInRange(pos, TRAP_DETECTION_RANGE) && player.getGameMode().isSurvivalLike())
                    .collect(Multimaps.toMultimap(this::getTeam, Function.identity(), HashMultimap::create));

            if(!enemiesInRange.isEmpty()) stats.onPlayerInTrapRange(pos, this, enemiesInRange);
        });
    }

    public boolean buyTrap(ServerPlayerEntity player, AbstractTrap trap) {
        return this.teamStatsMap.get(this.getTeam(player)).buyTrap(trap);
    }

    public boolean buyAbility(ServerPlayerEntity player, AbstractAbility ability) {
        return this.teamStatsMap.get(this.getTeam(player)).buyAbility(ability);
    }

    public void buyEnchantmentUpgrade(ServerPlayerEntity player, RegistryEntry<Enchantment> enchantment, int tier) {
        this.teamStatsMap.get(this.getTeam(player)).buyEnchantmentUpgrade(enchantment, this.world, tier);
    }

    public void buyTickFunctionUpgrade(ServerPlayerEntity player, AbstractTickFunction function, int tier) {
        this.teamStatsMap.get(this.getTeam(player)).buyTickFunctionUpgrade(function, tier);
    }

    private boolean buyGeneratorUpgrade(DyeColor team, int tier) {
        return this.map.upgradeIslandGen(team, tier);
    }

    public boolean buyGeneratorUpgrade(ServerPlayerEntity player, int tier) {
        return this.buyGeneratorUpgrade(this.getTeam(player), tier);
    }

    @Override
    public TrapManager getTrapManager(DyeColor team) {
        return this.teamStatsMap.get(team).getTrapManager();
    }

    public void onTeamElimination(DyeColor team) {
        this.broadcastPacket(new TeamEliminatedPayload(team, GameType.BEDWARS));
        this.broadcast(Text.translatable("game.bedwars.eliminate.team", colouredTextFromColour(team)), false);
    }

	public void onBuyItem(Cost cost, PlayerEntity player) {
        ifNotNull(this.getDbTable(player), table -> table.spendCost(cost));
	}
}