package the_fireplace.overlord.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import the_fireplace.overlord.client.gui.*;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.container.ContainerBabySkeleton;
import the_fireplace.overlord.container.ContainerBabySkeletonMaker;
import the_fireplace.overlord.container.ContainerSkeleton;
import the_fireplace.overlord.container.ContainerSkeletonMaker;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;
import the_fireplace.overlord.network.packets.SetConfigsMessage;
import the_fireplace.overlord.tileentity.TileEntityBabySkeletonMaker;
import the_fireplace.overlord.tileentity.TileEntitySkeletonMaker;
import the_fireplace.overlord.tools.Squads;

/**
 * @author The_Fireplace
 */
public class OverlordGuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    PacketDispatcher.sendTo(new SetConfigsMessage((byte)ConfigValues.BONEREQ_WARRIOR, (byte)ConfigValues.BONEREQ_BABY), (EntityPlayerMP)player);
                    return new ContainerSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    return null;
                }
            case -1:
            case -2:
                return null;
            case -3:
                if (entity != null && entity instanceof TileEntityBabySkeletonMaker) {
                    PacketDispatcher.sendTo(new SetConfigsMessage((byte)ConfigValues.BONEREQ_WARRIOR, (byte)ConfigValues.BONEREQ_BABY), (EntityPlayerMP)player);
                    return new ContainerBabySkeletonMaker(player.inventory, (TileEntityBabySkeletonMaker) entity);
                } else {
                    return null;
                }
            default:
                if(world.getEntityByID(ID) != null){
                    if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                        return new ContainerSkeleton(player.inventory, (EntitySkeletonWarrior)world.getEntityByID(ID));
                    }else if(world.getEntityByID(ID) instanceof EntityBabySkeleton){
                        return new ContainerBabySkeleton(player.inventory, (EntityBabySkeleton)world.getEntityByID(ID));
                    }
                }
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case 0:
                if (entity != null && entity instanceof TileEntitySkeletonMaker) {
                    return new GuiSkeletonMaker(player.inventory, (TileEntitySkeletonMaker) entity);
                } else {
                    return null;
                }
            case -1:
                return new GuiRing(Squads.getInstance().getSquadsFor(player.getUniqueID()));
            case -2:
                return new GuiSquadEditor(player.getUniqueID().toString());
            case -3:
                if (entity != null && entity instanceof TileEntityBabySkeletonMaker) {
                    return new GuiBabySkeletonMaker(player.inventory, (TileEntityBabySkeletonMaker) entity);
                } else {
                    return null;
                }
            default:
                if(world.getEntityByID(ID) != null){
                    if(world.getEntityByID(ID) instanceof EntitySkeletonWarrior){
                        return new GuiSkeleton(player.inventory, (EntitySkeletonWarrior)world.getEntityByID(ID), Squads.getInstance().getSquadsFor(player.getUniqueID()));
                    }else if(world.getEntityByID(ID) instanceof EntityBabySkeleton){
                        return new GuiBabySkeleton(player.inventory, (EntityBabySkeleton)world.getEntityByID(ID), Squads.getInstance().getSquadsFor(player.getUniqueID()));
                    }
                }
                return null;
        }
    }
}
