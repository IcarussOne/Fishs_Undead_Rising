package com.Fishmod.mod_LavaCow.enchantment;

import com.Fishmod.mod_LavaCow.mod_LavaCow;
import com.Fishmod.mod_LavaCow.client.Modconfig;
import com.Fishmod.mod_LavaCow.init.ModMobEffects;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentCorrosive extends Enchantment {
	public EnchantmentCorrosive(String unlocalizedName, String registryName, EnumEnchantmentType type) {
		super(Rarity.VERY_RARE, type, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND});
		this.setName(mod_LavaCow.MODID + "." + unlocalizedName);
		this.setRegistryName(registryName);
	}
	
    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int enchantmentLevel) {
        return super.getMinEnchantability(enchantmentLevel) + 50;
    }
	
	@Override
	public int getMaxLevel() {
		return 5;
	}
	
    /**
     * Determines if this enchantment can be applied to a specific ItemStack.
     */
    public boolean canApply(ItemStack stack) {
        return Modconfig.Enchantment_Enable ? super.canApply(stack) : false;
    }
	
    /**
     * This applies specifically to applying at the enchanting table. The other method {@link #canApply(ItemStack)}
     * applies for <i>all possible</i> enchantments.
     * @param stack
     * @return
     */
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return Modconfig.Enchantment_Enable ? super.canApplyAtEnchantingTable(stack) : false;
    }
	
    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
	@Override
    public boolean isAllowedOnBooks() {
        return Modconfig.Enchantment_Enable;
    }
	
	@Override
	public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
		if(target instanceof EntityLivingBase)
			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ModMobEffects.CORRODED, 4 * 20, level - 1));
	}
}
