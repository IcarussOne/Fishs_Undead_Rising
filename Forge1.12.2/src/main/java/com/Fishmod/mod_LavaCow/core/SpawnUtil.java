package com.Fishmod.mod_LavaCow.core;

import java.util.UUID;

import com.Fishmod.mod_LavaCow.mod_LavaCow;
import com.Fishmod.mod_LavaCow.client.Modconfig;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class SpawnUtil {
	
	public static boolean isDay(World worldIn) {
		return worldIn.getWorldTime() <= 12000;
	}
	
	public static boolean isAllowedDimension(int dimensionIn) {
		for(int i : Modconfig.Spawn_AllowList) {
			if(i == dimensionIn)
				return true;
		}
		
		return false;
	}
	
	public static boolean isAllowedDimensionCemetery(int dimensionIn) {
		for(int i : Modconfig.Spawn_Cemetery_AllowList) {
			if(i == dimensionIn)
				return true;
		}
		
		return false;
	}
	
	/* Used to determine the relative height */
    public static BlockPos getHeight(Entity entityIn) {
    	World worldIn = entityIn.getEntityWorld();
    	BlockPos pos = entityIn.getPosition();
    	Chunk chunk = worldIn.getChunkFromBlockCoords(pos);  	
    	BlockPos blockpos, blockpos1;

    	if (chunk == null) {
    		blockpos = new BlockPos(pos.getX(), pos.getY() + 16, pos.getZ());
    	} else {
    		blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ());
    	}
    	
        for (; blockpos.getY() >= 0; blockpos = blockpos1) {
            blockpos1 = blockpos.down();
            IBlockState state = chunk.getBlockState(blockpos1);

            if (state.getMaterial().blocksMovement() && !state.getBlock().isLeaves(state, worldIn, blockpos1) && !state.getBlock().isFoliage(worldIn, blockpos1)) {
                break;
            }
        }

    	return pos;
    }
    
    public static boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
    	if(worldIn.getChunkProvider() instanceof ChunkProviderServer)
    		return ((ChunkProviderServer)worldIn.getChunkProvider()).isInsideStructure(worldIn, structureName, pos);
    	
    	return false;
    }
    
    public static EntityLivingBase getEntityByUniqueId(UUID uniqueId, World worldIn){
        
    	for(Entity E : worldIn.loadedEntityList) {
    		if(E instanceof EntityLivingBase && E.getUniqueID().equals(uniqueId))
    			return (EntityLivingBase) E;
    	}

        return null;
    }
    
    public static BlockPos isNearBlock(World worldIn, Block BlockIn, BlockPos pos, int r) {
        int dx = MathHelper.floor(pos.getX());
        int dy = MathHelper.floor(pos.getY());
        int dz = MathHelper.floor(pos.getZ());
        
        for(int i = dx - r; i < dx + r; i++)
        	for(int j = dy - r; j < dy + r; j++)
        		for(int k = dz - r; k < dz + r; k++)
        			if(worldIn.getBlockState(new BlockPos(i, j, k)).getBlock().equals(BlockIn)) {
                		return new BlockPos(i, j, k);
        			}

    	return null;
    }
    
    public static TextComponentTranslation TimeupDeathMessage(Entity entityIn) {
    	return new TextComponentTranslation("death." + mod_LavaCow.MODID + ".timeup", new Object[] {entityIn.getDisplayName()});
    }
}
