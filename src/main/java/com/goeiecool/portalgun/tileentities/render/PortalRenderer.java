package com.goeiecool.portalgun.tileentities.render;

import com.goeiecool.portalgun.render.StoreFramebuffer;
import com.goeiecool.portalgun.tileentities.Portal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by Klaas on 19-10-2015.
 */
public class PortalRenderer extends TileEntitySpecialRenderer {
    private static int frames = 0;
    public static boolean renderingTile = false;

    public static StoreFramebuffer storeFrame;
    public static StoreFramebuffer texFrame;

    public PortalRenderer(){
        Framebuffer mcfb = mc.getFramebuffer();
        storeFrame = new StoreFramebuffer(mcfb.framebufferWidth,mcfb.framebufferHeight);
        texFrame = new StoreFramebuffer(mcfb.framebufferWidth,mcfb.framebufferHeight);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double posX, double posY, double posZ, float partialTicks, int p_180535_9_) {
        if(!(tileEntity instanceof Portal) || renderingTile){
            return;
        }
        renderingTile = true;

        Portal portal = (Portal) tileEntity;
        if(portal.renderer == null){
            portal.renderer = new RenderGlobal(mc);
        }


        RenderGlobal renderer = portal.renderer;
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX,posY,posZ);
        GlStateManager.disableTexture2D();
        glBegin(GL_QUADS);
        glColor3d(255,255,255);
        glVertex3d(0,0.01,0);
        glVertex3d(0,0.01,1);
        glVertex3d(1,0.01,1);
        glVertex3d(1,0.01,0);
        glEnd();
        GlStateManager.popMatrix();

        if(portal.linked != null){
            //store current depth and color buffer for later
            storeFrame.storeComp(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
                //clear the color and depth buffer
                glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
                //render world on clear screen
                translateAndRenderWorld(renderer,partialTicks,portal,posX,posY,posZ);
                //store the colors of the world in texFrame.
                texFrame.storeComp(GL_COLOR_BUFFER_BIT);
            //recall the depth and color buffers.
            storeFrame.recallComp(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

            //stop writing to color buffer.
            //Start writing to stencil buffer.
            glEnable(GL_STENCIL_TEST);
            glStencilMask(0xFF);

            //setup stencil operations to increase stencil buffer wherever we draw.
            glStencilFunc(GL_ALWAYS, 0, 0xFF);
            glStencilOp(GL_KEEP,GL_KEEP,GL_INCR);

            //write portal as stencil mask.
            GlStateManager.pushMatrix();
            GlStateManager.translate(posX, posY, posZ);
            //clear stencil buffer before writing new mask.
            glClear(GL_STENCIL_BUFFER_BIT);
            renderPortalMask();
            GlStateManager.popMatrix();

            //disable stencil writing and enable color writing.
            glColorMask(true,true,true,true);
            glStencilMask(0);

            //Pass if stencil > 0
            glStencilFunc(GL_LESS, 0,0xFF);
            glStencilOp(GL_KEEP,GL_KEEP,GL_KEEP);

            //Do an Orthographic projection.
            GlStateManager.matrixMode(GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0,1,1,0,-1,1);
            GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();

            //enable texture and bind earlier stored world texture.
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(texFrame.framebufferTexture);

            GlStateManager.disableFog();

            //render texture on fullscreen regardless of depth. (without modifying depth buffer).
            //glDepthMask probably not even necessary lol.
            glDepthMask(false);

            //DONT USE IMMEDIATE MODE FOR ANYTHING THIS IS JUST FOR THE DEMO
            glDisable(GL_DEPTH_TEST);
            glBegin(GL_QUADS);
            //top left
            glColor3d(255,255,255);
            glTexCoord2d(0,1);
            glVertex2d(0, 0);
            //bottom left
            glTexCoord2d(0,0);
            glVertex2d(0, 1);
            //bottom right
            glTexCoord2d(1,0);
            glVertex2d(1, 1);
            //top right
            glTexCoord2d(1,1);
            glVertex2d(1, 0);
            glEnd();
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);

            GlStateManager.enableFog();

            //return to perspective projection.
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL_PROJECTION);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL_MODELVIEW);

            glDisable(GL_STENCIL_TEST);


        }
        GlStateManager.enableTexture2D();
        renderingTile = false;
    }
    private static Minecraft mc = Minecraft.getMinecraft();




    public static void renderPortalMask(){
        GlStateManager.disableTexture2D();
        glBegin(GL_QUADS);
        glColor3d(255,255,255);
        glVertex3d(0, 2, 0);
        glVertex3d(0, 0, 0);
        glVertex3d(1, 0, 0);
        glVertex3d(1, 2, 0);
        glEnd();
        GlStateManager.enableTexture2D();
    }

    private static void translateAndRenderWorld(RenderGlobal renderer, float partialTicks, Portal portal, double posX, double posY, double posZ){
        BlockPos thisPortal = portal.getPos();
        BlockPos otherPortal = portal.linked.getPos();
        BlockPos diff = otherPortal.subtract(thisPortal);

        GlStateManager.pushMatrix();

        GlStateManager.translate(posX+.5,posY,posZ);
        GlStateManager.rotate(180,0,1,0);
        GlStateManager.translate(-posX-.5,-posY,-posZ);
        GlStateManager.translate(-diff.getX(),-diff.getY(),-diff.getZ());
        renderWorld(renderer, partialTicks);
        GlStateManager.popMatrix();
    }

    private static void renderWorld(RenderGlobal renderer, float partialTicks){
        //this function is mostly Copy/pasted from another MC class.
        //I had to copy paste to make sure it calls only the functions I need.
        //MIP mapping seems to be a bit broken as a result.

        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        ClippingHelperImpl.getInstance();
        ActiveRenderInfo.updateRenderInfo(mc.thePlayer, mc.gameSettings.thirdPersonView == 2);

        Frustum frustum = new Frustum();
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        frustum.setPosition(d0, d1, d2);

        try{
            int i = Math.max(Minecraft.getDebugFPS(), 30);
            long nanotime = System.nanoTime() + (long)(1000000000 / i);


            renderer.setupTerrain(entity,(double)partialTicks,frustum,frames++,mc.thePlayer.isSpectator());
            renderer.updateChunks(nanotime);
            GlStateManager.shadeModel(7425);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.disableAlpha();
            renderer.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double)partialTicks, 0, entity);
            GlStateManager.enableAlpha();
            renderer.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double) partialTicks, 0, entity);
            mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            renderer.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double)partialTicks, 0, entity);
            mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();

            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            renderer.renderEntities(entity, frustum, partialTicks);
            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.disableLightmap();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();

            GlStateManager.enableCull();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.shadeModel(7425);

            if (mc.gameSettings.fancyGraphics)
            {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                renderer.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, 0, entity);
                GlStateManager.disableBlend();
            }
            else
            {
                renderer.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, 0, entity);
            }
        } catch (NullPointerException ex){
            renderer.setWorldAndLoadRenderers(mc.theWorld);
        }
    }
}
