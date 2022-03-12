package com.Fishmod.mod_LavaCow.item;

import java.util.List;
import javax.annotation.Nullable;

import com.Fishmod.mod_LavaCow.client.model.armor.ModelCrown;
import com.Fishmod.mod_LavaCow.init.FURItemRegistry;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkeletonKingCrownItem extends ArmorItem {

	public SkeletonKingCrownItem(Item.Properties p_i48534_3_) {
		super(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, p_i48534_3_);
	}
	
    /**
     * Returns true if this item has an enchantment glint. By default, this returns
     * <code>stack.isItemEnchanted()</code>, but other items can override it (for instance, written books always return
     * true).
     *  
     * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
     * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
     */
	@Override
	public boolean isFoil(ItemStack stack) {
        return true;
    }
    
	@Override
	public boolean isValidRepairItem(ItemStack armour, ItemStack material) {
		return material.getItem() == FURItemRegistry.HATRED_SHARD;
	}
    
    /**
     * Called to tick armor in the armor slot. Override to do something
     */
	@Override
    public void onArmorTick(ItemStack itemStack, World world, PlayerEntity player){
    	if(player.tickCount % 20 == 0) {
    		/*boolean modified = false;
    		
	    	for (AbstractSkeletonEntity Skeleton : world.getEntitiesOfClass(AbstractSkeletonEntity.class, player.getBoundingBox().inflate(16.0D))) {
	    		for (EntityAITasks.EntityAITaskEntry Task : Skeleton.tasks.taskEntries) {
	    			if (Task.action instanceof EntityAIFollowEntity)
	    				modified = true;
	    		}
	    		
	    		if(!modified) {
	    			Skeleton.setAttackTarget(null);
	    			Skeleton.playSound(SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, 1.0F, 1.0F);
		        	for (int i = 0; i < 16; ++i)
		            {
		                double d0 = new Random().nextGaussian() * 0.02D;
		                double d1 = new Random().nextGaussian() * 0.02D;
		                double d2 = new Random().nextGaussian() * 0.02D;
		                Skeleton.world.spawnParticle(EnumParticleTypes.SPELL_MOB, Skeleton.posX + (double)(new Random().nextFloat() * Skeleton.width) - (double)Skeleton.width, Skeleton.posY + (double)(new Random().nextFloat() * Skeleton.height), Skeleton.posZ + (double)(new Random().nextFloat() * Skeleton.width) - (double)Skeleton.width, d0, d1, d2);
		            }
		        	
		    		Skeleton.tasks.taskEntries.clear();
		    		Skeleton.targetTasks.taskEntries.clear();

		    		Skeleton.tasks.addTask(1, new EntityAISwimming(Skeleton));
		    		Skeleton.tasks.addTask(2, new EntityAIRestrictSun(Skeleton));
		    		Skeleton.tasks.addTask(3, new EntityAIFleeSun(Skeleton, 1.0D));
		    		Skeleton.tasks.addTask(6, new EntityAIFollowEntity(Skeleton, player.getUniqueID(), 1.0D, 10.0F, 2.0F));
		    		Skeleton.tasks.addTask(6, new EntityAIWatchClosest(Skeleton, PlayerEntity.class, 8.0F));
		    		Skeleton.tasks.addTask(6, new EntityAILookIdle(Skeleton));
		    		Skeleton.targetTasks.addTask(1, new EntityAIGuardingEntity(Skeleton, player.getUniqueID()));
		    		Skeleton.targetTasks.addTask(1, new EntityAIHurtByTarget(Skeleton, false, new Class[0]));
		    		Skeleton.setCombatTask();
		    		
		    		break;
	    		}
	        }*/
    	}
    }
    
	@Override
    @OnlyIn(Dist.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType armorSlot, String type) {
		return "mod_lavacow:textures/mobs/skeletonking.png";
	}
	
	@SuppressWarnings("unchecked")
	@Override
    @OnlyIn(Dist.CLIENT)
	public <E extends BipedModel<?>> E getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, E _default) {
		return (E) new ModelCrown<>(1.0F);
	}
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tootip.mod_lavacow:skeletonking_crown").withStyle(TextFormatting.YELLOW));
	}

}
