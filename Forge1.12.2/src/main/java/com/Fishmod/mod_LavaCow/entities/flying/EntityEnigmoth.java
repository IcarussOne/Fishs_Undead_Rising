package com.Fishmod.mod_LavaCow.entities.flying;

import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import com.Fishmod.mod_LavaCow.client.Modconfig;
import com.Fishmod.mod_LavaCow.entities.ai.EntityFishAITempt;
import com.Fishmod.mod_LavaCow.entities.projectiles.EntityMothScales;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityEnigmothLarva;
import com.Fishmod.mod_LavaCow.init.FishItems;
import com.Fishmod.mod_LavaCow.init.ModMobEffects;
import com.Fishmod.mod_LavaCow.util.LootTableHandler;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowOwnerFlying;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityEnigmoth extends EntityRideableFlyingMob {
	private static final DataParameter<Integer> SKIN_TYPE = EntityDataManager.<Integer>createKey(EntityEnigmoth.class, DataSerializers.VARINT);
	
	private int skinFixedTick;
	
	public EntityEnigmoth(World worldIn) {
		super(worldIn, Modconfig.Enigmoth_FlyingHeight_limit);
		this.setSize(1.6F, 1.0F);
		this.isImmuneToFire = true;
        this.experienceValue = 30;
	}
	
	@Override
	protected void initEntityAI() { 
		super.initEntityAI();
		this.wander = this.wanderGoal();
        this.follow = this.followGoal();
		
		this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
		this.tasks.addTask(3, new EntityFishAITempt(this, 1.25D, false, Sets.newHashSet(Items.CHORUS_FRUIT, Items.CHORUS_FRUIT_POPPED, new ItemStack(Blocks.END_ROD).getItem())));
		this.tasks.addTask(4, new EntityEnigmoth.AIUseSpell());  	
		
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[] {EntityEnigmoth.class}));
        this.targetTasks.addTask(2, new EntityAITargetNonTamed<>(this, EntityPlayer.class, false, new Predicate<Entity>()
        {
            public boolean apply(@Nullable Entity p_apply_1_)
            {
            	return !(p_apply_1_.isRiding() && p_apply_1_.getRidingEntity() instanceof EntityEnigmoth);
            }
        }).setUnseenMemoryTicks(160));
	}
	
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.05D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Modconfig.Enigmoth_Attack);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Modconfig.Enigmoth_Health);
		this.getEntityAttribute(SharedMonsterAttributes.FLYING_SPEED).setBaseValue(0.067D);
	}
	
    protected void entityInit() {
    	super.entityInit();
        this.getDataManager().register(SKIN_TYPE, Integer.valueOf(this.rand.nextInt(3)));
    }
    
    /**
     * Will return how many at most can spawn in a chunk at once.
     */
    @Override
    public int getMaxSpawnedInChunk() {
       return 1;
    }
    
    @Override
    public boolean getCanSpawnHere() {
    	// Middle end island check
    	if (this.world.provider.getDimension() == 1) {
    		// Only spawn above Y of 50 to prevent spawning in end caves added by other mods
    		if (!Modconfig.Enigmoth_Middle_End_Island) {
    			return super.getCanSpawnHere() && (this.posY > 50.0D) && (this.posX > 500.0D || this.posX < -500.0D || this.posZ > 500.0D || this.posZ < -500.0D);
    		}
    		else {
    			return super.getCanSpawnHere() && (this.posY > 50.0D);
    		}
    	}
       	
        return super.getCanSpawnHere();
    }
    
    @Override
    public void setTamed(boolean tamed) {
    	super.setTamed(tamed);
    	
        if (tamed) {
        	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Modconfig.Enigmoth_Health * 2.0D);
        	this.setHealth(this.getHealth() * 2.0F);
        } else {
        	this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Modconfig.Enigmoth_Health);
        	this.setHealth(this.getHealth());
        }
    }
    
    @Override
    protected EntityAIBase wanderGoal() {
        return (this.getNavigator() instanceof PathNavigateGround) ? new EntityAIWanderAvoidWater(this, 1.0D) : new EntityFlyingMob.AIRandomFly(this);
    }

    @Override
    protected EntityAIBase followGoal() {
        return new EntityAIFollowOwnerFlying(this, 1.0D, 10.0F, 4.0F);
    }
    
    public boolean processInteract(EntityPlayer player, EnumHand hand) {       
        ItemStack stack = player.getHeldItem(hand);
        Item item = stack.getItem();
        
    	if (this.isTamed() && this.isOwner(player) && !this.isChild()) {
			if (item.equals(Items.NETHER_WART) && this.getSkin() != 2) { 	
				this.skinFixedTick = 2 * 60 * 20;
				this.world.setEntityState(this, (byte)39);
				this.setSkin(2);
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}
				
	        	this.playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.0F, 1.0F);

	        	for (int i = 0; i < 16; ++i) {
	                double d0 = new Random().nextGaussian() * 0.02D;
	                double d1 = new Random().nextGaussian() * 0.02D;
	                double d2 = new Random().nextGaussian() * 0.02D;
	                this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }	
	        	
				return true;
			} else if (item.equals(Items.BLAZE_POWDER) && this.getSkin() != 1) { 	
				this.skinFixedTick = 2 * 60 * 20;
				this.world.setEntityState(this, (byte)39);
				this.setSkin(1);
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
                }

	        	this.playSound(SoundEvents.ITEM_FIRECHARGE_USE, 1.0F, 1.0F);

	        	for (int i = 0; i < 16; ++i) {
	                double d0 = new Random().nextGaussian() * 0.02D;
	                double d1 = new Random().nextGaussian() * 0.02D;
	                double d2 = new Random().nextGaussian() * 0.02D;
	                this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }	
	        	
				return true;
			} else if (item.equals(Items.ENDER_PEARL) && this.getSkin() != 0) { 	
				this.skinFixedTick = 2 * 60 * 20;
				this.world.setEntityState(this, (byte)39);
				this.setSkin(0);
				if (!player.capabilities.isCreativeMode) {
					stack.shrink(1);
				}

	        	this.playSound(SoundEvents.ENTITY_ENDEREYE_DEATH, 1.0F, 1.0F);

	        	for (int i = 0; i < 16; ++i) {
	                double d0 = new Random().nextGaussian() * 0.02D;
	                double d1 = new Random().nextGaussian() * 0.02D;
	                double d2 = new Random().nextGaussian() * 0.02D;
	                this.world.spawnParticle(EnumParticleTypes.DRAGON_BREATH, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
	            }	
	        	
				return true;
			}
        }

        return super.processInteract(player, hand);
    }
    
    @Override
    public boolean isBreedingItem(ItemStack stack) {
    	return this.isTamed() && stack.getItem().equals(Items.CHORUS_FRUIT) || stack.getItem().equals(Items.CHORUS_FRUIT_POPPED);
    }
    
    @Override
    public float getEyeHeight() {
    	return this.height * 0.45F;
    }
    
    @Override
	public int abilityCooldown() {
    	return Modconfig.Enigmoth_Ability_Cooldown_Mount * 20;
    }
    
    @Override
    public void onLivingUpdate()
    {
    	if (this.world.isRemote) {
				for(int i = 0; i < 2; ++i) {
					this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
			}
		}
		
		if (this.skinFixedTick > 0) {
			--this.skinFixedTick;
		}
    	
    	super.onLivingUpdate();
    }
    
    @Override
    public void onUpdate()
    {
    	super.onUpdate();
    	
    	if(!this.onGround && !this.isSpellcasting() && this.ticksExisted % 12 == 0) {
    		this.playSound(this.getFlyingSound(), 1.0F, 1.0F);
    	}
    }
   
   @Nullable
   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
       this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Modconfig.Enigmoth_Health);
       this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Modconfig.Enigmoth_Attack);
       this.setHealth(this.getMaxHealth());
   	
   		return super.onInitialSpawn(difficulty, livingdata);
   }
   
   public int getSkin() {
       return this.dataManager.get(SKIN_TYPE).intValue();
   }

   public void setSkin(int skinType) {
       this.dataManager.set(SKIN_TYPE, Integer.valueOf(skinType));
   }
   
   public int getSkinFixedTick() {
       return this.skinFixedTick;
    }
   
   @Override
	protected double VehicleSpeedMod() {
	    return (this.isInLava() || this.isInWater()) ? 0.2D : 2.0D;
	}
   
   @Override
   public float getBlockPathWeight(BlockPos pos) {
	   if (this.world.getBlockState(pos).getBlock().equals(Blocks.END_ROD)) {
		   return 20.0F;
	   } else {
		   return super.getBlockPathWeight(pos);
	   }
   }
   
   /**
    * Handler for {@link World#setEntityState}
    */
   @Override
   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
		switch(id) {
		case 10:
			this.spellTicks = 15;
			break;
		case 39:
			this.skinFixedTick = 2 * 60 * 20;
			break;
		default:
			super.handleStatusUpdate(id);
			break;
		}
   }
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSkin(nbt.getInteger("Variant"));
		this.skinFixedTick = nbt.getInteger("SkinFixedTick");
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Variant", getSkin());
		nbt.setInteger("SkinFixedTick", this.skinFixedTick);
	}
	
	@Override
    public int getTalkInterval() {
        return 150;
    }
	
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	protected SoundEvent getAmbientSound() {
		return FishItems.ENTITY_ENIGMOTH_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return FishItems.ENTITY_ENIGHMOTH_HURT;
	}

	protected SoundEvent getDeathSound() {
		return FishItems.ENTITY_ENIGMOTH_DEATH;
	}
	
    public SoundEvent getSpellSound() {
        return FishItems.ENTITY_ENIGMOTH_SCALES;
    }
	
	protected SoundEvent getFlyingSound() {
		return FishItems.ENTITY_ENIGMOTH_FLYING;
	}
	
	@Override
    protected void playStepSound(BlockPos pos, Block block) {
		if (this.getLandTimer() > 10) {
			this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
		}
	}
	
    /**
     * Get this Entity's EnumCreatureAttribute
     */
    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.ARTHROPOD;
    }

	@Nullable
	protected ResourceLocation getLootTable() {
		return LootTableHandler.ENIGMOTH;
	}
	
	// Immune to Corroded, Poison, and Void Dust
    @Override
	public boolean isPotionApplicable(PotionEffect effect) {
		return effect.getPotion() != ModMobEffects.CORRODED && effect.getPotion() != MobEffects.POISON && effect.getPotion() != ModMobEffects.VOID_DUST && super.isPotionApplicable(effect);
	}
	
    public class AIUseSpell extends EntityAIBase {
        protected int spellWarmup;
        protected int spellCooldown;

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
    		if (EntityEnigmoth.this.getAttackTarget() == null)
                return false;
            else if (EntityEnigmoth.this.isSpellcasting() || EntityEnigmoth.this.getAttackTimer() > 0 || EntityEnigmoth.this.getDistance(EntityEnigmoth.this.getAttackTarget()) > (3.0F))
                return false;
            else {                
            	return EntityEnigmoth.this.ticksExisted >= this.spellCooldown;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return EntityEnigmoth.this.getAttackTarget() != null && this.spellWarmup > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.spellWarmup = this.getCastWarmupTime();
            EntityEnigmoth.this.spellTicks = this.getCastingTime();
            this.spellCooldown = EntityEnigmoth.this.ticksExisted + this.getCastingInterval();
            SoundEvent soundevent = this.getSpellPrepareSound();
            EntityEnigmoth.this.world.setEntityState(EntityEnigmoth.this, (byte)10);
            if (soundevent != null) {
            	EntityEnigmoth.this.playSound(soundevent, 1.0F, 1.0F);
            }
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void updateTask() {
            --this.spellWarmup;

            if (this.spellWarmup == 0) {
            	EntityEnigmoth.this.playSound(EntityEnigmoth.this.getSpellSound(), 0.475F, 1.0F);
            	this.castSpell();
            }
        }

        protected void castSpell() {
        	for(int i = 0 ; i < Modconfig.Enigmoth_Scale_Amount; i++) {
       	 		Double d0 = new Random().nextDouble() * 8.0D - 4.0D;
       	 		Double d1 = new Random().nextDouble() * 8.0D - 4.0D;
       	 		EntityMothScales ammo = new EntityMothScales(world, EntityEnigmoth.this, d0, - 2.4D, d1);
       	 		ammo.setPosition(EntityEnigmoth.this.posX + d0 * 0.25D, EntityEnigmoth.this.posY + (double)(EntityEnigmoth.this.height / 3.0F) + 1.5D, EntityEnigmoth.this.posZ + d1 * 0.25D);	
	            
       	 		if (!(EntityEnigmoth.this.world.isRemote)) {
       	 		EntityEnigmoth.this.world.spawnEntity(ammo);	
	       	 		ammo.setScaleType(EntityEnigmoth.this.getSkin());
	       	 	}
        	}
        	
       	 	if (EntityEnigmoth.this.getSkin() == 0 && EntityEnigmoth.this.getAttackTarget() != null) {
       	 		EntityEnigmoth.this.addPotionEffect(new PotionEffect(MobEffects.SPEED, Modconfig.Enigmoth_Ability_Cooldown * 20, 0));
       	 	}
       	 	
       	 	if (EntityEnigmoth.this.getSkin() == 1 && EntityEnigmoth.this.getAttackTarget() != null) {
       	 		EntityEnigmoth.this.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, Modconfig.Enigmoth_Ability_Cooldown * 20, 0));
       	 	}
        	
       	 	if (EntityEnigmoth.this.getSkin() == 2 && EntityEnigmoth.this.getAttackTarget() != null) {
       	 		EntityEnigmoth.this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, Modconfig.Enigmoth_Ability_Cooldown * 20, 0));
       	 	}

       	 	if (EntityEnigmoth.this.getSkinFixedTick() == 0) {
       	 		EntityEnigmoth.this.setSkin(EntityEnigmoth.this.rand.nextInt(3));
       	 	}
        }

        protected int getCastWarmupTime() {
            return 5;
        }

        protected int getCastingTime() {
            return 10;
        }

        protected int getCastingInterval() {
            return Modconfig.Enigmoth_Ability_Cooldown * 20;
        }

        @Nullable
        protected SoundEvent getSpellPrepareSound() {
            return SoundEvents.ENTITY_BAT_TAKEOFF;
        }
    }
    
	public EntityEnigmothLarva createChild(EntityAgeable ageable) {
		EntityEnigmothLarva entity = new EntityEnigmothLarva(this.world);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			entity.setOwnerId(uuid);
			entity.setTamed(true);
			entity.setHealth(entity.getMaxHealth());
		}

		return entity;
	}
}
