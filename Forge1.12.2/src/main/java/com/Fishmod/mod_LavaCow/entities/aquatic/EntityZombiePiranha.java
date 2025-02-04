package com.Fishmod.mod_LavaCow.entities.aquatic;

import javax.annotation.Nullable;

import com.Fishmod.mod_LavaCow.client.Modconfig;
import com.Fishmod.mod_LavaCow.core.SpawnUtil;
import com.Fishmod.mod_LavaCow.entities.ai.EntityAIPickupMeat;
import com.Fishmod.mod_LavaCow.init.FishItems;
import com.Fishmod.mod_LavaCow.util.LootTableHandler;
import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityZombiePiranha extends EntityAquaMob {
	protected static final DataParameter<Byte> DATA_FLAGS_ID = EntityDataManager.createKey(EntityZombiePiranha.class, DataSerializers.BYTE);
	private static final DataParameter<Integer> SKIN_TYPE = EntityDataManager.<Integer>createKey(EntityZombiePiranha.class, DataSerializers.VARINT);
	private boolean isAggressive = false;
	public EntityZombiePiranha(World worldIn) {
        super(worldIn);   
        this.setSize(1.0F, 0.8F);
        this.BeachedLife = -1;
    }
    
    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataManager().register(DATA_FLAGS_ID, (byte)0);
        this.getDataManager().register(SKIN_TYPE, Integer.valueOf(0));
	}
    
    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(1, new AIPiranhaLeapAtTarget(this, 0.6F));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D, 80));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
        
        this.applyEntityAI();
     }
    
    protected void applyEntityAI() {
    	this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
    	
    	if(Modconfig.Piranha_AnimalAttack) {
    		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<EntitySquid>(this, EntitySquid.class, true));
    		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<>(this, EntityAgeable.class, 0, true, true, new Predicate<Entity>()
            {
                public boolean apply(@Nullable Entity p_apply_1_)
                {
                    return !(p_apply_1_ instanceof EntityTameable) && ((EntityAgeable)p_apply_1_).getHealth() < ((EntityAgeable)p_apply_1_).getMaxHealth();
                }
            }));
    	}
    	this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityMob.class, 0, true, true, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
                return !(p_apply_1_ instanceof EntityZombiePiranha || p_apply_1_ instanceof EntityCreeper) && ((EntityMob)p_apply_1_).getHealth() < ((EntityMob)p_apply_1_).getMaxHealth();
            }
        }));
    	this.targetTasks.addTask(5, new EntityAIPickupMeat<>(this, EntityItem.class, true));
    }

    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Modconfig.ZombiePiranha_Health);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Modconfig.ZombiePiranha_Attack);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)1.2F);
    }
    
    public boolean getCanSpawnHere() {
    	return SpawnUtil.isAllowedDimension(this.dimension) && super.getCanSpawnHere();
	}
    
    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void onLivingUpdate() {
    	super.onLivingUpdate();
    	
    	if (this.ticksExisted >= 8 * 20 && this.getIsAmmo()) {
    		this.attackEntityFrom(DamageSource.causeMobDamage(this).setDamageIsAbsolute().setDamageBypassesArmor() , this.getMaxHealth());
    	}
    }
    
    @Override
	public boolean attackEntityAsMob(Entity entity) {
    	this.playSound(FishItems.ENTITY_ZOMBIEPIRANHA_ATTACK, 1.0F, 1.0F);
    	return super.attackEntityAsMob(entity);
	}
    
    static class AIPiranhaLeapAtTarget extends EntityAIBase {
    	   /** The entity that is leaping. */
    	   private final EntityLiving leaper;
    	   /** The entity that the leaper is leaping towards. */
    	   private EntityLivingBase leapTarget;
    	   /** The entity's motionY after leaping. */
    	   private final float leapMotionY;

    	   public AIPiranhaLeapAtTarget(EntityLiving leapingEntity, float leapMotionYIn) {
    	      this.leaper = leapingEntity;
    	      this.leapMotionY = leapMotionYIn;
    	      this.setMutexBits(5);
    	   }
    	   
    	   /**
    	    * Returns whether the EntityAIBase should begin execution.
    	    */
    	   public boolean shouldExecute() {
    	      this.leapTarget = this.leaper.getAttackTarget();
    	      if (this.leapTarget == null) {
    	         return false;
    	      } else {
    	         double d0 = this.leaper.getDistanceSq(this.leapTarget);
    	         if (!(d0 < 4.0D) && !(d0 > 24.0D)) {
    	               return true;
    	         } else {
    	            return false;
    	         }
    	      }
    	   }
    	   
    	   /**
    	    * Returns whether an in-progress EntityAIBase should continue executing
    	    */
    	   public boolean shouldContinueExecuting() {
    	      return !this.leaper.onGround && !this.leaper.isInWater();
    	   }

    	   /**
    	    * Execute a one shot task or start executing a continuous task
    	    */
    	   public void startExecuting() {
    	      double d0 = this.leapTarget.posX - this.leaper.posX;
    	      double d1 = this.leapTarget.posZ - this.leaper.posZ;
    	      float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
    	      if ((double)f >= 1.0E-4D) {
    	         this.leaper.motionX += d0 / (double)f * 0.5D * (double)0.8F + this.leaper.motionX * (double)0.2F;
    	         this.leaper.motionZ += d1 / (double)f * 0.5D * (double)0.8F + this.leaper.motionZ * (double)0.2F;
    	      }
    	      this.leaper.motionY = (double)this.leapMotionY;
    	   }  	   
    }
    
    protected void updateAITasks()
    {
        if(this.getAttackTarget() != null)
    	{       		
        	isAggressive = true;
    		this.world.setEntityState(this, (byte)11);
    	}
	    else 
    	{
    		isAggressive = false;
    		this.world.setEntityState(this, (byte)34);
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id)
    {
        if (id == 11)
        {
            this.isAggressive = true;
        }
        else if (id == 34)
        {
            this.isAggressive = false;
        }
        else
        {
            super.handleStatusUpdate(id);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isAggressive()
    {
    	return this.isAggressive;
    }
    
    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
       return super.attackEntityFrom(source, amount);
    }
    
    /**
     * Called only once on an entity when first time spawned, via egg, mob spawner, natural spawning etc, but not called
     * when entity is reloaded from nbt. Mainly used for initializing attributes and inventory
     */
    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        if(BiomeDictionary.hasType(this.getEntityWorld().getBiome(this.getPosition()), Type.SWAMP) && !this.getIsAmmo()) {
     	   this.setSkin(2);
        }
        
    	return super.onInitialSpawn(difficulty, livingdata);
    }
    
    protected SoundEvent getAmbientSound() {
        return FishItems.ENTITY_ZOMBIEPIRANHA_AMBIENT;
     }

     protected SoundEvent getDeathSound() {
        return FishItems.ENTITY_ZOMBIEPIRANHA_DEATH;
     }

     protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_GUARDIAN_FLOP;
     }
     
     @Override
     public int getTalkInterval() {
     	return 150;
     }
     
     /**
      * Get this Entity's EnumCreatureAttribute
      */
     public EnumCreatureAttribute getCreatureAttribute()
     {
         return EnumCreatureAttribute.UNDEAD;
     }
	
	public float getEyeHeight() {
		return this.height * 0.5F;
	}
	
    public boolean getIsAmmo() {
    	return (this.getDataManager().get(DATA_FLAGS_ID) & 1) != 0;
    }

    public void setIsAmmo(boolean t) {
        byte b0 = this.getDataManager().get(DATA_FLAGS_ID);
        if (t) {
           this.getDataManager().set(DATA_FLAGS_ID, (byte)(b0 | 1));
        } else {
           this.getDataManager().set(DATA_FLAGS_ID, (byte)(b0 & -2));
        }
    }
    
    public boolean getIsInfinite() {
    	return (this.getDataManager().get(DATA_FLAGS_ID) & 4) != 0;
    }

    public void setIsInfinite(boolean t) {
        byte b0 = this.getDataManager().get(DATA_FLAGS_ID);
        if (t) {
           this.getDataManager().set(DATA_FLAGS_ID, (byte)(b0 | 4));
        } else {
           this.getDataManager().set(DATA_FLAGS_ID, (byte)(b0 & -5));
        }
    }
    
    public int getSkin() {
        return this.dataManager.get(SKIN_TYPE).intValue();
    }

    public void setSkin(int skinType) {
        this.dataManager.set(SKIN_TYPE, Integer.valueOf(skinType));
    }
    
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("is_Ammo", this.getIsAmmo());
        compound.setBoolean("is_Infinite", this.getIsInfinite());
        compound.setInteger("Variant", getSkin());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
		this.setIsAmmo(compound.getBoolean("is_Ammo"));
		this.setIsInfinite(compound.getBoolean("is_Infinite"));
    	this.setSkin(compound.getInteger("Variant"));
    }
    
    @Override
    @Nullable
    protected ResourceLocation getLootTable() {
        return LootTableHandler.ZOMBIEPIRANHA;
    }
    
    /**
     * Entity won't drop items or experience points if this returns false
     */
    @Override
    protected boolean canDropLoot() {
		if(this.getIsAmmo()) {
			this.experienceValue = 0;
		}
		
		// Prevents infinite item farming
    	return !this.getIsAmmo();
    }
}
