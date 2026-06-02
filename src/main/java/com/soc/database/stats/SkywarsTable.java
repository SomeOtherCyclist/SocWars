package com.soc.database.stats;

import java.util.UUID;

public class SkywarsTable extends CombatTable {
    @Override
    public void win() {
        this.wins++;
        this.addXp(1500);
    }
    @Override
    public void lose() {
        this.losses++;
        this.addXp(30);
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
        this.addXp(8);
    }

    @Override
    public void shootFireball() {
        this.fireballsShot++;
        this.addXp(1);
    }

    protected int t1chestsOpened = 0;
    public void openT1Chest() {
        this.t1chestsOpened++;
        this.addXp(5);
    }
    protected int t2chestsOpened = 0;
    public void openT2Chest() {
        this.t2chestsOpened++;
        this.addXp(10);
    }
    protected int t3chestsOpened = 0;
    public void openT3Chest() {
        this.t3chestsOpened++;
        this.addXp(20);
    }
    protected int t4chestsOpened = 0;
    public void openT4Chest() {
        this.t4chestsOpened++;
        this.addXp(30);
    }
    public void openChest(int tier) {
        switch (tier) {
            case 1 -> this.openT1Chest();
            case 2 -> this.openT2Chest();
            case 3 -> this.openT3Chest();
            case 4 -> this.openT4Chest();
        }
    }

    protected int timesEliminated = 0;
    public void eliminate() {
        this.timesEliminated++;
        this.addXp(5);
    }
    protected int eliminations = 0;
    public void grantElimination() {
        this.eliminations++;
        this.addXp(150);
    }

    protected int voidDeaths = 0;
    public void fallInVoid() {
        this.voidDeaths++;
    }

    public SkywarsTable(UUID player) {
        super(player);
    }

    public SkywarsTable() {
        this(null);
    }

    @Override
    public String getTableName() {
        return "SKYWARS";
    }

    @Override
    public int getKills() {
        return super.kills + this.eliminations;
    }
}
