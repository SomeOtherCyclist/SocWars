package com.soc.items.util;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class StatArmourBonus {
    private final boolean[] piecesEquipped = {false, false, false, false};
    private boolean[] lastPiecesEquipped = {false, false, false, false};
    private final RegistryEntry<EntityAttribute> attribute;

    public StatArmourBonus(final RegistryEntry<EntityAttribute> attribute) {
        this.attribute = attribute;
    }

    public void setPiece(EquipmentSlot slot, boolean isEquipped) {
        this.piecesEquipped[slot.ordinal() - 2] = isEquipped;
    }

    private float getBonus() {
        int count = 0;
        for (boolean piece : piecesEquipped) {
            if (piece) { count++; }
        }

        return count == 4 ? 1f : count / 5f;
    }

    private Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeMap(double value) {
        return ImmutableMultimap.of(attribute, new EntityAttributeModifier(Identifier.ofVanilla("armour." + attribute.getIdAsString().split(":")[1]), value, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    public void setBonus(PlayerEntity playerEntity) {
        if (this.lastPiecesEquipped == this.piecesEquipped) {
            return;
        }

        playerEntity.getAttributes().removeModifiers(this.attributeMap(0));
        playerEntity.getAttributes().addTemporaryModifiers(this.attributeMap(getBonus()));

        this.lastPiecesEquipped = this.piecesEquipped.clone();
    }
}