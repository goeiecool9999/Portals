package com.goeiecool.portalgun.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

/**
 * Created by Klaas on 23-10-2015.
 */
public class StoreFramebuffer extends Framebuffer {


    public StoreFramebuffer(int width, int height){
        super(width,height,true);
    }
    public static Minecraft mc = Minecraft.getMinecraft();

    public void storeComp(int comp){
        Framebuffer mcfb = mc.getFramebuffer();
        int width = mcfb.framebufferWidth;
        int height = mcfb.framebufferHeight;

        checkFBOSize();

        glBindFramebuffer(GL_READ_FRAMEBUFFER,mcfb.framebufferObject);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebufferObject);
        glBlitFramebuffer(0,0,width,height,0,0,width,height,comp,GL_NEAREST);

        mcfb.bindFramebuffer(false);
    }

    public void checkFBOSize(){
        Framebuffer mcfb = mc.getFramebuffer();
        int width = mcfb.framebufferWidth;
        int height = mcfb.framebufferHeight;
        if(framebufferWidth != width || framebufferHeight != height) {
            deleteFramebuffer();
            this.createFramebuffer(width,height);
        }
    }

    public void recallComp(int comp){
        Framebuffer mcfb = mc.getFramebuffer();
        int width = mcfb.framebufferWidth;
        int height = mcfb.framebufferHeight;

        checkFBOSize();

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER,mcfb.framebufferObject);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebufferObject);
        glBlitFramebuffer(0,0,width,height,0,0,width,height,comp,GL_NEAREST);

        mcfb.bindFramebuffer(false);
    }


}
