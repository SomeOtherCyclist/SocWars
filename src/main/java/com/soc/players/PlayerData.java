package com.soc.players;

import com.soc.items.util.StatArmourBonus;
import net.minecraft.entity.attribute.EntityAttributes;

public class PlayerData {
    public int lives;
    public boolean invisible;

    public StatArmourBonus steadfastBonus = new StatArmourBonus(EntityAttributes.KNOCKBACK_RESISTANCE);
}
