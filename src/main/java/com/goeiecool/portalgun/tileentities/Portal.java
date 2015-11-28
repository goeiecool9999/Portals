package com.goeiecool.portalgun.tileentities;

import com.goeiecool.portalgun.PortalgunMod;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by Klaas on 19-10-2015.
 */
public class Portal extends TileEntity implements IUpdatePlayerListBox {
    public RenderGlobal renderer;
    public Portal linked;

    public Portal(){
        PortalgunMod.portalList.add(this);
    }

    public void setLinked(Portal link){
        this.linked = link;
        link.linked = this;
    }

    public void unlink(){
        linked.linked = null;
        this.linked = null;
    }




    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(0,1,0);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        try {
            renderer.deleteAllDisplayLists();
        } catch (NullPointerException ex){

        }
        PortalgunMod.portalList.remove(this);
    }

    @Override
    public void update() {
        AxisAlignedBB bbox = PortalgunMod.portalBlock.getCollisionBoundingBox(worldObj,this.getPos(),worldObj.getBlockState(this.getPos())).expand(0,1,0.1);
        List<Entity> ents = worldObj.getEntitiesWithinAABB(Entity.class, bbox);

        for(Entity entityIn : ents) {
            Portal portal = this;
            if (portal.linked != null) {
                Vec3 thisPortal = new Vec3(pos.getX(), pos.getY(), pos.getZ());
                BlockPos otherPortalBP = portal.linked.getPos();
                Vec3 otherPortal = new Vec3(otherPortalBP.getX(), otherPortalBP.getY(), otherPortalBP.getZ());
                Vec3 diff = otherPortal.subtract(thisPortal);

                Vec3 newPos = entityIn.getPositionVector().add(diff);
                entityIn.motionZ = -entityIn.motionZ;
                entityIn.setLocationAndAngles(newPos.xCoord, newPos.yCoord, newPos.zCoord + 0.2, entityIn.getRotationYawHead() + 180, entityIn.rotationPitch);
                entityIn.prevRotationYaw = entityIn.rotationYaw;
                entityIn.prevRotationPitch = entityIn.rotationPitch;

                if (entityIn instanceof EntityPlayerSP) {
                    EntityPlayerSP SPplayer = (EntityPlayerSP) entityIn;
                    SPplayer.prevRenderArmYaw = SPplayer.renderArmYaw += 180;
                    SPplayer.prevRotationYawHead = SPplayer.rotationYawHead += 180;
                }
            }
        }
    }
}
