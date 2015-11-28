package com.goeiecool.portalgun;

import com.goeiecool.portalgun.items.debug.Linker;
import com.goeiecool.portalgun.tileentities.Portal;
import com.goeiecool.portalgun.tileentities.PortalBlock;
import com.goeiecool.portalgun.tileentities.render.PortalRenderer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = PortalgunMod.MODID, version = PortalgunMod.VERSION)
public class PortalgunMod
{
    public static final String MODID = "portalgun";
    public static final String VERSION = "1.0";
    public static ArrayList<Portal> portalList = new ArrayList<Portal>();
    public static PortalBlock portalBlock;

    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerTileEntity(Portal.class,"portal",new PortalRenderer());
        portalBlock = new PortalBlock(Material.rock);
        GameRegistry.registerBlock(portalBlock, "portal");
        GameRegistry.registerItem(new Linker(), "Linker");
        Framebuffer framebuffer =  Minecraft.getMinecraft().getFramebuffer();
        if(!framebuffer.isStencilEnabled())
            framebuffer.enableStencil();
    }
}
