package com.goeiecool.portalgun.items.debug;

import com.goeiecool.portalgun.tileentities.Portal;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

/**
 * Created by Klaas on 21-10-2015.
 */
public class Linker extends Item{
    public Linker(){
        setCreativeTab(CreativeTabs.tabAllSearch);
        setMaxStackSize(1);
    }


    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof Portal){
            Portal portal = (Portal) tileEntity;
            NBTTagCompound comp = stack.getTagCompound();
            if (comp == null) {
                comp = new NBTTagCompound();
                stack.setTagCompound(comp);
            }
            if(portal.linked == null) {
                if (!comp.hasKey("portal1")) {
                    stack.setTagInfo("portal1", new NBTTagString(pos.getX() + ";" + pos.getY() + ";" + pos.getZ()));
                } else {
                    String raw = comp.getString("portal1");
                    String[] split = raw.split(";");
                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    int z = Integer.parseInt(split[2]);
                    TileEntity ent = worldIn.getTileEntity(new BlockPos(x, y, z));
                    if (ent instanceof Portal) {
                        ((Portal) ent).setLinked(portal);
                    }
                    comp.removeTag("portal1");
                }
            } else {
                portal.unlink();
                comp.removeTag("portal1");
            }

        }


        return super.onItemUse(stack, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
    }
}
