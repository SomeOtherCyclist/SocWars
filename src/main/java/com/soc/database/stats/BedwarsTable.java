package com.soc.database.stats;

import com.soc.resourcedata.deserialisation.Cost;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.UUID;

public class BedwarsTable extends CombatTable {
    @Override
    public void win() {
        this.wins++;
        this.addXp(2500);
    }
    @Override
    public void lose() {
        this.losses++;
        this.addXp(50);
    }

    @Override
    public void grantKill() {
        this.kills++;
        this.addXp(50);
    }
    @Override
    public void grantDeath() {
        this.deaths++;
        this.addXp(5);
    }

    @Override
    public void dealDamage(int damage) {
        this.damageDealt += damage;
        this.addXp(damage);
    }

    @Override
    public void hitArrow() {
        this.arrowsHit++;
        this.addXp(10);
    }

    @Override
    public void shootFireball() {
        this.fireballsShot++;
        this.addXp(1);
    }

    protected int finalKills = 0;
    public void grantFinalKill() {
        this.finalKills++;
        this.addXp(300);
    }
    protected int finalDeaths = 0;
    public void grantFinalDeath() {
        this.finalDeaths++;
        this.addXp(5);
    }

    protected int bedsBroken = 0;
    public void grantBedBreak() {
        this.bedsBroken++;
        this.addXp(500);
    }
    protected int bedsLost = 0;
    public void loseBed() {
        this.bedsLost++;
    }

    protected int iron = 0;
    public void collectIron(int count) {
        this.iron += count;
        this.addXp(1);
    }
    protected int gold = 0;
    public void collectGold(int count) {
        this.gold += count;
        this.addXp(2);
    }
    protected int diamonds = 0;
    public void collectDiamonds(int count) {
        this.diamonds += count;
        this.addXp(3);
    }
    protected int emeralds = 0;
    public void collectEmeralds(int count) {
        this.emeralds += count;
        this.addXp(4);
    }
    public void collectItem(ItemStack stack) {
        if (stack.isOf(Items.IRON_INGOT)) this.collectIron(stack.getCount());
        if (stack.isOf(Items.GOLD_INGOT)) this.collectGold(stack.getCount());
        if (stack.isOf(Items.DIAMOND)) this.collectDiamonds(stack.getCount());
        if (stack.isOf(Items.EMERALD)) this.collectEmeralds(stack.getCount());
    }

    protected int ironSpent = 0;
    public void spendIron(int count) {
        this.ironSpent += count;
        this.addXp(1);
    }
    protected int goldSpent = 0;
    public void spendGold(int count) {
        this.goldSpent += count;
        this.addXp(2);
    }
    protected int diamondsSpent = 0;
    public void spendDiamonds(int count) {
        this.diamondsSpent += count;
        this.addXp(3);
    }
    protected int emeraldsSpent = 0;
    public void spendEmeralds(int count) {
        this.emeraldsSpent += count;
        this.addXp(4);
    }
    public void spendCost(Cost cost) {
        this.spendIron(cost.iron());
        this.spendGold(cost.gold());
        this.spendDiamonds(cost.diamonds());
        this.spendEmeralds(cost.emeralds());
    }

    protected int voidDeaths = 0;
    public void fallInVoid() {
        this.voidDeaths++;
    }

    public BedwarsTable(UUID player) {
        super(player);
    }

    public BedwarsTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "BEDWARS";
    }

    @Override
    public int getKills() {
        return super.kills + this.finalKills;
    }
}
