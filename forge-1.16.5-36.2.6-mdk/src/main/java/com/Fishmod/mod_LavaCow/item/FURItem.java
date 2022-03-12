package com.Fishmod.mod_LavaCow.item;

import java.util.List;

import javax.annotation.Nullable;

import com.Fishmod.mod_LavaCow.config.FURConfig;
import com.Fishmod.mod_LavaCow.init.FURItemRegistry;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FURItem extends Item {
	private UseAction UseAction;
	private int UseDuration;
	private int Tooltip = 0;
	
	public FURItem(Properties p_i48487_1_, int UseDurationIn, UseAction UseActionIn, int TooltipIn) {
		super(p_i48487_1_);
		this.UseDuration = UseDurationIn;
		this.UseAction = UseActionIn;
		this.Tooltip = TooltipIn;
	}

	@Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return this.UseDuration;
    }

	@Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return this.UseAction;
    }
 
	@Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if(stack.getItem().equals(FURItemRegistry.MOOTENHEART))
			tooltip.add(new TranslationTextComponent("tootip." + this.getRegistryName(), FURConfig.MootenHeart_Damage.get()).withStyle(TextFormatting.YELLOW).append(new TranslationTextComponent("item.mod_lavacow.potion_of_mooten_lava").withStyle(TextFormatting.YELLOW)));
		else if(stack.getItem().equals(FURItemRegistry.HALO_NECKLACE))
			tooltip.add(new TranslationTextComponent("tootip." + this.getRegistryName(), FURConfig.HaloNecklace_Damage.get()).withStyle(TextFormatting.YELLOW));
		else if(this.Tooltip == 2)
			tooltip.add(new TranslationTextComponent("tootip." + this.getRegistryName()));			
		else if(this.Tooltip == 1)
			tooltip.add(new TranslationTextComponent("tootip." + this.getRegistryName()).withStyle(TextFormatting.YELLOW));
	}	
}
