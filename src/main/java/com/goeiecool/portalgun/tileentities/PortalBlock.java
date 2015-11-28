package com.goeiecool.portalgun.tileentities;

import com.goeiecool.portalgun.PortalgunMod;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by Klaas on 19-10-2015.
 */
public class PortalBlock extends BlockContainer {
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    public PortalBlock(Material materialIn) {
        super(materialIn);
        setUnlocalizedName("portal");
        setCreativeTab(CreativeTabs.tabAllSearch);
        setBlockBounds(0,0,0,1,1,0.01f);
    }



    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);







    }

    @Override
    public boolean requiresUpdates() {
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        super.updateTick(worldIn, pos, state, rand);
        System.out.println("tick");
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {
        return;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new Portal();
    }
}
