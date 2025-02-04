package com.Fishmod.mod_LavaCow.client.renders.entity;

import com.Fishmod.mod_LavaCow.client.model.entity.ModelZombiePiranha;
import com.Fishmod.mod_LavaCow.entities.aquatic.EntityPiranha;
import com.Fishmod.mod_LavaCow.entities.aquatic.EntityZombiePiranha;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class RenderZombiePiranha extends RenderLiving<EntityZombiePiranha>{
	
	private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
			new ResourceLocation("mod_lavacow:textures/mobs/swarmer/swarmer.png"),
			new ResourceLocation("mod_lavacow:textures/mobs/piranha.png"),
			new ResourceLocation("mod_lavacow:textures/mobs/swarmer/swarmer2.png")
	};
	
	static{
		for(ResourceLocation texture: TEXTURES)
			System.out.println(texture.getResourcePath());
    }

    public RenderZombiePiranha(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelZombiePiranha(), 0.3F);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityZombiePiranha entity) {
    	return TEXTURES[(entity instanceof EntityPiranha) ? 1 : entity.getSkin()];
    }
    
    protected void applyRotations(EntityZombiePiranha entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
        float f = 4.3F * MathHelper.sin(0.6F * ageInTicks);
        GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
        
        if(entityLiving instanceof EntityPiranha) 
        	GlStateManager.scale(0.8F, 0.8F, 0.8F);
        
        if (!entityLiving.isInWater() && !entityLiving.isAggressive()) {
           GlStateManager.translate(0.1F, -0.3F, -0.1F);
           GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        }
        
     }
}
