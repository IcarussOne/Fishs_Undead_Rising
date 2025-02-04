package com.Fishmod.mod_LavaCow.client.layer;

import com.Fishmod.mod_LavaCow.entities.EntityAmberLord;
import com.Fishmod.mod_LavaCow.entities.EntityCactyrant;
import com.Fishmod.mod_LavaCow.entities.EntityLavaCow;
import com.Fishmod.mod_LavaCow.entities.EntitySkeletonKing;
import com.Fishmod.mod_LavaCow.entities.EntitySludgeLord;
import com.Fishmod.mod_LavaCow.entities.EntitySoulWorm;
import com.Fishmod.mod_LavaCow.entities.EntityZombieMushroom;
import com.Fishmod.mod_LavaCow.entities.floating.EntityGhostSwarmer;
import com.Fishmod.mod_LavaCow.entities.flying.EntityEnigmoth;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityScarab;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityEnigmothLarva;
import com.Fishmod.mod_LavaCow.entities.tameable.EntitySalamander;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerGenericGlowing<T extends EntityLiving> implements LayerRenderer<T> {
    private ResourceLocation GLOW_LAYER;
    private final RenderLiving<T> Renderer;
    private static ResourceLocation[] TEXTURES_GLOW_SALAMANDER = {
            new ResourceLocation("mod_lavacow:textures/mobs/salamander/salamander_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/salamander/salamander_glow1.png")
    };

    private static ResourceLocation[] TEXTURES_GLOW_SALAMANDER_CHILD = {
            new ResourceLocation("mod_lavacow:textures/mobs/salamander/salamanderlesser_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/salamander/salamanderlesser_glow1.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_SLUDGE_LORD = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/sludgelord/sludgelord_glow.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_AMBER_LORD = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/amberlord/amberlord_glow.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_SCARAB = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/scarab/scarab_glow.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_CACTYRANT = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/cactyrant/cactyrant_glow.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_SOUL_WORM = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/soulworm/soulworm_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/soulworm/soulworm_glow1.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_GHOST_SWARMER = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/ghostswarmer/ghostswarmer_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/ghostswarmer/ghostswarmer_glow1.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_ENIGMOTH = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/enigmoth/enigmoth_eyes.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/enigmoth/enigmoth_eyes1.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/enigmoth/enigmoth_eyes2.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_ENIGMOTH_LARVA = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/enigmoth/enigmoth_larva_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/enigmoth/enigmoth_larva_glow1.png")
    };

    private static final ResourceLocation[] TEXTURES_GLOW_SKELETON_KING = new ResourceLocation[]{
            new ResourceLocation("mod_lavacow:textures/mobs/skeletonking/skeletonking_glow.png"),
            new ResourceLocation("mod_lavacow:textures/mobs/skeletonking/skeletonking_angry_glow.png")
    };

    public LayerGenericGlowing(RenderLiving<T> RendererIn, ResourceLocation TextureIn) {
        this.Renderer = RendererIn;
        GLOW_LAYER = TextureIn;
    }

    public void doRenderLayer(T entitylivingIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingIn instanceof EntityLavaCow && ((EntityLavaCow) entitylivingIn).getSkin() == 0)
            return;

        if (entitylivingIn instanceof EntityZombieMushroom && ((EntityZombieMushroom) entitylivingIn).getSkin() == 0)
            return;

        if (entitylivingIn instanceof EntitySalamander) {
            if (((EntitySalamander) entitylivingIn).isNymph()) {
                GLOW_LAYER = TEXTURES_GLOW_SALAMANDER_CHILD[((EntitySalamander) entitylivingIn).getSkin()];
            } else {
                GLOW_LAYER = TEXTURES_GLOW_SALAMANDER[((EntitySalamander) entitylivingIn).getSkin()];
            }
        }

        if (entitylivingIn instanceof EntitySoulWorm) {
            GLOW_LAYER = TEXTURES_GLOW_SOUL_WORM[((EntitySoulWorm) entitylivingIn).getSkin()];
        }

        if (entitylivingIn instanceof EntitySludgeLord && !(entitylivingIn instanceof EntityAmberLord)) {
            GLOW_LAYER = TEXTURES_GLOW_SLUDGE_LORD[0];
        }

        if (entitylivingIn instanceof EntityEnigmoth) {
            GLOW_LAYER = TEXTURES_GLOW_ENIGMOTH[((EntityEnigmoth) entitylivingIn).getSkin()];
        }

        if (entitylivingIn instanceof EntityCactyrant) {
            if (((EntityCactyrant) entitylivingIn).getSkin() == 2 && !((EntityCactyrant) entitylivingIn).isCamouflaging()) {
                GLOW_LAYER = TEXTURES_GLOW_CACTYRANT[0];
            } else {
                return;
            }
        }

        if (entitylivingIn instanceof EntityGhostSwarmer) {
            GLOW_LAYER = TEXTURES_GLOW_GHOST_SWARMER[((EntityGhostSwarmer) entitylivingIn).getSkin()];
        }

        if (entitylivingIn instanceof EntityAmberLord) {
            GLOW_LAYER = TEXTURES_GLOW_AMBER_LORD[0];
        }

        if (entitylivingIn instanceof EntityScarab) {
            GLOW_LAYER = TEXTURES_GLOW_SCARAB[0];
        }

        if (entitylivingIn instanceof EntityEnigmoth) {
            GLOW_LAYER = TEXTURES_GLOW_ENIGMOTH[((EntityEnigmoth) entitylivingIn).getSkin()];
        }

        if (entitylivingIn instanceof EntityEnigmothLarva) {
            GLOW_LAYER = TEXTURES_GLOW_ENIGMOTH_LARVA[((EntityEnigmothLarva) entitylivingIn).getSkin()];
        }

        if (entitylivingIn instanceof EntitySkeletonKing) {
            GLOW_LAYER = TEXTURES_GLOW_SKELETON_KING[((EntitySkeletonKing) entitylivingIn).isAngered() ? 1 : 0];
        }

        this.Renderer.bindTexture(GLOW_LAYER);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        int i = 61680;
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.enableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.Renderer.getMainModel().render(entitylivingIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        i = entitylivingIn.getBrightnessForRender();
        j = i % 65536;
        k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        this.Renderer.setLightmap(entitylivingIn);
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
    }

    public boolean shouldCombineTextures() {
        return true;
    }
}
