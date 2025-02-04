package com.Fishmod.mod_LavaCow.entities.projectiles;

import com.Fishmod.mod_LavaCow.mod_LavaCow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySludgeJet extends EntityFireball {

    private float damage = 6.0F;

    public EntitySludgeJet(World worldIn) {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySludgeJet(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntitySludgeJet(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.collidedVertically && !this.collidedHorizontally) this.accelerationY -= 0.006D;

        if (this.getEntityWorld().isRemote)
            for (int i = 0; i < 22 + this.rand.nextInt(12); i++) {
                mod_LavaCow.PROXY.spawnCustomParticle("sludgejet", world, this.posX + this.rand.nextDouble() * 0.5D, this.posY + 0.5D + this.rand.nextDouble() * 0.5D, this.posZ + this.rand.nextDouble() * 0.5D, 0.0D, 0.0D, 0.0D, 0.0F, 0.3F, 0.5F);
            }
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor() {
        return 0.95F;
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result) {
        if (!this.world.isRemote && result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity target = result.entityHit;
            if (target != null && this.shootingEntity != null && target instanceof EntityLivingBase && target != this.shootingEntity && !target.isOnSameTeam(this.shootingEntity)) {
                this.setDamage((float) this.shootingEntity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue());

                if (target.attackEntityFrom(DamageSource.causeIndirectDamage(this, this.shootingEntity).setProjectile(), this.getDamage())) {
                    float local_difficulty = this.world.getDifficultyForLocation(new BlockPos(this)).getAdditionalDifficulty();
                    // 5 seconds * regional difficulty
                    int difficulty_calculation = (int) (100 * local_difficulty);

                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20 + difficulty_calculation, 3));
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 5 * 20 + difficulty_calculation, 1));
                    this.applyEnchantments((EntityLivingBase) this.shootingEntity, target);
                }

                this.setDead();
            }
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith() {
        return false;
    }

    public void setDamage(float damageIn) {
        this.damage = damageIn;
    }

    public float getDamage() {
        return this.damage;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    protected boolean isFireballFiery() {
        return false;
    }

    protected EnumParticleTypes getParticleType() {
        return EnumParticleTypes.SMOKE_NORMAL;
    }
}

