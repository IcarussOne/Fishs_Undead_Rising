package com.Fishmod.mod_LavaCow.entities.flying;

import java.util.Random;

import javax.annotation.Nullable;

import com.Fishmod.mod_LavaCow.core.SpawnUtil;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityFishTameable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFlyingMob extends EntityFishTameable {

    private int attackTimer;
    private int hoverTimer;
    private int landTimer;
    public static int heightLimit;

    public EntityFlyingMob(World worldIn, int heightLimit) {
        super(worldIn);

        EntityFlyingMob.heightLimit = heightLimit;
        this.moveHelper = new EntityFlyingMob.FlyingMoveHelper(this);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 16.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.setNoGravity(true);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new AIFlyingAttackMelee(this, 1.0D, true));
        this.tasks.addTask(5, new EntityFlyingMob.AIRandomFly(this));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.FLYING_SPEED);
    }

    @Override
    public boolean getCanSpawnHere() {
        return SpawnUtil.isAllowedDimension(this.dimension)
                && this.world.canSeeSky(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ))
                && super.getCanSpawnHere();
    }

    protected void doSitCommand(EntityPlayer playerIn) {
        if (SpawnUtil.getHeight(this).getY() > 0 || this.isChild()) {
            this.setNoGravity(false);
        }

        super.doSitCommand(playerIn);
    }

    protected void doFollowCommand(EntityPlayer playerIn) {
        if (!this.isChild()) {
            this.setNoGravity(true);
        }

        super.doFollowCommand(playerIn);
    }

    protected void doWanderCommand(EntityPlayer playerIn) {
        if (!this.isChild()) {
            this.setNoGravity(true);
        }

        super.doWanderCommand(playerIn);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.attackTimer > 0) {
            --this.attackTimer;
        }

        if (!this.world.isRemote && !this.isChild()) {
            if (this.onGround) {
                if (this.getLandTimer() < 20) {
                    this.setLandTimer(this.getLandTimer() + 1);
                    this.world.setEntityState(this, (byte) 40);
                }

                if (this.hasNoGravity()) {
                    this.setNoGravity(this.getAttackTarget() != null);
                }

                if (!this.hasNoGravity() && !this.isSitting() && this.rand.nextFloat() < 0.15F) {
                    this.setNoGravity(true);
                    this.motionY += 0.25F;
                }
            } else {
                if (this.getLandTimer() > 0) {
                    this.setLandTimer(this.getLandTimer() - 1);
                    this.world.setEntityState(this, (byte) 41);
                }

                if (!this.hasNoGravity()) {
                    this.setNoGravity(true);
                }
            }

            if (this.isSitting() && this.getAttackTarget() == null && !this.onGround && SpawnUtil.getHeight(this).getY() > 0) {
                this.motionY += -0.025F;
            }
        }
    }

    public boolean attackEntityAsMob(Entity entityIn) {
        this.attackTimer = 20;
        this.world.setEntityState(this, (byte) 4);

        return super.attackEntityAsMob(entityIn);
    }

    public int getAttackTimer() {
        return this.attackTimer;
    }

    public void setAttackTimer(int i) {
        this.attackTimer = i;
    }

    public int getHoverTimer() {
        return this.hoverTimer;
    }

    public void setHoverTimer(int i) {
        this.hoverTimer = i;
    }

    public int getLandTimer() {
        return this.landTimer;
    }

    public void setLandTimer(int i) {
        this.landTimer = i;
    }

    /**
     * Handler for {@link World#setEntityState}
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
            case 4:
                this.attackTimer = 20;
                break;
            case 40:
                if (this.getLandTimer() < 20) {
                    this.setLandTimer(this.getLandTimer() + 1);
                }
                break;
            case 41:
                if (this.getLandTimer() > 0) {
                    this.setLandTimer(this.getLandTimer() - 1);
                }
                break;
            default:
                super.handleStatusUpdate(id);
                break;
        }
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }

    @Override
    protected PathNavigate createNavigator(World world) {
        PathNavigateFlying pathnavigateflying = new PathNavigateFlying(this, world);
        pathnavigateflying.setCanOpenDoors(false);
        pathnavigateflying.setCanFloat(true);
        pathnavigateflying.setCanEnterDoors(true);
        return pathnavigateflying;
    }

    public Entity getLowestPassenger() {
        // Learned the hard way: don't trust getLowestRidingEntity.
        // It will literally return the last entity in the passenger list -- useless.
        Entity lowestPassenger = null;

        for (Entity passenger : this.getPassengers()) {
            if (lowestPassenger == null || passenger.getEntityBoundingBox().minY < lowestPassenger.getEntityBoundingBox().minY) {
                lowestPassenger = passenger;
            }
        }

        return lowestPassenger;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block block) {
    }

    protected double VehicleSpeedMod() {
        return 1.0D;
    }

    public void travel(float strafe, float vertical, float forward) {
		/*if (this.isInWeb) {
			this.getMoveHelper().setMoveTo(this.posX, this.posY - 1, this.posZ, 1.0D);
		}
		
    	if (this.onGround) {
			this.getMoveHelper().setMoveTo(this.posX, this.posY + 1, this.posZ, 1.0D);
		}*/

        // If the lowest passenger is colliding with the ground, get them out!
        Entity lowestPassenger = this.getLowestPassenger();

        if (lowestPassenger != null && !(lowestPassenger instanceof EntityPlayer)) {
            AxisAlignedBB passengerBounds = lowestPassenger.getEntityBoundingBox();

            if (!lowestPassenger.world.getCollisionBoxes(lowestPassenger, passengerBounds).isEmpty()) {
                this.getMoveHelper().setMoveTo(this.posX, this.posY + lowestPassenger.height, this.posZ, 1.0D);
            }
        }

        if (!hasNoGravity() && !isBeingRidden() && !(getControllingPassenger() instanceof EntityPlayer)) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionY = -0.15D;
        }

        if (this.getAttackTarget() != null) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 1.05D;
            this.motionY *= 1.05D;
            this.motionZ *= 1.05D;
        }

        if (this.isInWater()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8D;
            this.motionY *= 0.8D;
            this.motionZ *= 0.8D;
        } else if (this.isInLava()) {
            this.moveRelative(strafe, vertical, forward, 0.02F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.5D;
            this.motionY *= 0.5D;
            this.motionZ *= 0.5D;
        } else {
            BlockPos ground = new BlockPos(this.posX, this.posY - 1.0D, this.posZ);
            IBlockState underState = this.world.getBlockState(ground);
            float f = 0.91F;

            if (this.onGround) {
                f = underState.getBlock().getSlipperiness(underState, this.world, ground, this) * 0.91F;
            }

            float f1 = 0.16277136F / (f * f * f);
            f = 0.91F;

            if (this.onGround) {
                f = underState.getBlock().getSlipperiness(underState, this.world, ground, this) * 0.91F;
            }

            this.moveRelative(strafe, vertical, forward, this.onGround ? 0.05F * f1 : 0.1F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double) f;
            this.motionY *= (double) f;
            this.motionZ *= (double) f;
        }

        this.prevLimbSwingAmount = this.limbSwingAmount;
        double d1 = this.posX - this.prevPosX;
        double d0 = this.posZ - this.prevPosZ;
        float f2 = MathHelper.sqrt(d1 * d1 + d0 * d0) * 4.0F;

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        this.limbSwingAmount += (f2 - this.limbSwingAmount) * 0.4F;
        this.limbSwing += this.limbSwingAmount;
    }

    /**
     * Returns true if this entity should move as if it were on a ladder (either because it's actually on a ladder, or
     * for AI reasons)
     */
    @Override
    public boolean isOnLadder() {
        return this.isChild() ? super.isOnLadder() : false;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData) {
        this.motionY += 0.5D;

        return super.onInitialSpawn(difficulty, entityLivingData);
    }

    class EntityAIWander extends EntityAIBase {
        public EntityAIWander(EntityFlyingMob entityFlyingMob) {
            this.setMutexBits(1);
        }

        public boolean shouldExecute() {
            return EntityFlyingMob.this.navigator.noPath() && EntityFlyingMob.this.rand.nextInt(10) == 0;
        }

        public boolean shouldContinueExecuting() {
            return false;
        }
    }

    static class AIRandomFly extends EntityAIBase {
        private final EntityFlyingMob parentEntity;

        public AIRandomFly(EntityFlyingMob entityFlyingMob) {
            this.parentEntity = entityFlyingMob;
            this.setMutexBits(1);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            EntityMoveHelper entitymovehelper = this.parentEntity.getMoveHelper();

            if (this.parentEntity.getNavigator() instanceof PathNavigateGround) {
                return false;
            }
            if (!entitymovehelper.isUpdating()) {
                return true;
            } else if (this.parentEntity.getAttackTarget() != null) {
                return false;
            } else {
                double d0 = entitymovehelper.getX() - this.parentEntity.posX;
                double d1 = entitymovehelper.getY() - this.parentEntity.posY;
                double d2 = entitymovehelper.getZ() - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                return d3 < 1.0D || d3 > 3600.0D;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return false;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            Random random = this.parentEntity.getRNG();
            BlockPos blockpos = new BlockPos(parentEntity).add(random.nextInt(15) - 7, 0, random.nextInt(15) - 7);
            double y = this.parentEntity.posY + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);

            // Stop calculating height in end dimension
            // TODO: A way to calculate other modded dimensions with floating islands?
            if (this.parentEntity.world.provider.getDimension() != 1) {
                int groundHeight = SpawnUtil.getHeight(this.parentEntity).getY();

                if (groundHeight > 0) {
                    if (this.parentEntity.isWet()) {
                        y = Math.min(SpawnUtil.getHeight(parentEntity).getY() + 3, y);
                    } else if (heightLimit != 0) {
                        y = Math.min(SpawnUtil.getHeight(parentEntity).getY() + heightLimit, y);
                    }
                }
            }

            this.parentEntity.moveHelper.setMoveTo((double) blockpos.getX() + 0.5D, y + 0.5D, (double) blockpos.getZ() + 0.5D, 1.0D);
        }
    }

    static class FlyingMoveHelper extends EntityMoveHelper {
        private final EntityFlyingMob parentEntity;
        private int courseChangeCooldown;
        IAttributeInstance entityMoveSpeedAttribute = this.entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FLYING_SPEED);
        double entityMoveSpeed = entityMoveSpeedAttribute != null ? entityMoveSpeedAttribute.getAttributeValue() : 0.1D;

        public FlyingMoveHelper(EntityFlyingMob flyer) {
            super(flyer);
            this.parentEntity = flyer;
        }

        public void onUpdateMoveHelper() {
            if (this.action == EntityMoveHelper.Action.MOVE_TO) {
                double d0 = this.posX - this.parentEntity.posX;
                double d1 = this.posY - this.parentEntity.posY;
                double d2 = this.posZ - this.parentEntity.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (this.courseChangeCooldown-- <= 0) {
                    this.courseChangeCooldown += this.parentEntity.getRNG().nextInt(5) + 2;
                    d3 = (double) MathHelper.sqrt(d3);

                    if (this.isNotColliding(this.posX, this.posY, this.posZ, d3) && this.isNotPassengerColliding(this.posX, this.posY, this.posZ, d3)) {
                        this.parentEntity.motionX += d0 / d3 * entityMoveSpeed;
                        this.parentEntity.motionY += d1 / d3 * entityMoveSpeed;
                        this.parentEntity.motionZ += d2 / d3 * entityMoveSpeed;

                        float yaw = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                        this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, yaw, Movement2RotationAngle(entityMoveSpeed));
                    } else {
                        this.action = EntityMoveHelper.Action.WAIT;
                    }
                }


                if (this.parentEntity.isSitting() && this.parentEntity.getAttackTarget() == null) {
                    this.action = EntityMoveHelper.Action.WAIT;
                }
            }
        }

        private float Movement2RotationAngle(double movement) {
            return (float) (movement * 1214.2857F - 31.428571428571427F);
        }

        /**
         * Checks if the lowest passenger's entity bounding box is not colliding with terrain
         */
        private boolean isNotPassengerColliding(double x, double y, double z, double distance) {
            if (this.parentEntity.getPassengers().isEmpty()) {
                return true;
            }

            double d0 = (x - this.parentEntity.posX) / distance;
            double d1 = (y - this.parentEntity.posY) / distance;
            double d2 = (z - this.parentEntity.posZ) / distance;
            Entity lowestPassenger = this.parentEntity.getLowestPassenger();
            AxisAlignedBB axisalignedbb = lowestPassenger.getEntityBoundingBox();

            for (int i = 1; (double) i < distance; ++i) {
                axisalignedbb = axisalignedbb.offset(d0, d1, d2);

                if (!lowestPassenger.world.getCollisionBoxes(lowestPassenger, axisalignedbb).isEmpty()) {
                    return false;
                }
            }

            return true;
        }


        /**
         * Checks if entity bounding box is not colliding with terrain
         */
        private boolean isNotColliding(double x, double y, double z, double distance) {
            double d0 = (x - this.parentEntity.posX) / distance;
            double d1 = (y - this.parentEntity.posY) / distance;
            double d2 = (z - this.parentEntity.posZ) / distance;
            AxisAlignedBB axisalignedbb = this.parentEntity.getEntityBoundingBox();

            for (int i = 1; (double) i < distance; ++i) {
                axisalignedbb = axisalignedbb.offset(d0, d1, d2);

                if (!this.parentEntity.world.getCollisionBoxes(this.parentEntity, axisalignedbb).isEmpty()) {
                    return false;
                }
            }

            return true;
        }
    }

    static class AIFlyingAttackMelee extends EntityAIAttackMelee {

        public AIFlyingAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
            super(creature, speedIn, useLongMemory);
        }

        protected double getAttackReachSqr(EntityLivingBase attackTarget) {
            return (double) (this.attacker.width * this.attacker.width + attackTarget.width * attackTarget.width);
        }
    }
}
