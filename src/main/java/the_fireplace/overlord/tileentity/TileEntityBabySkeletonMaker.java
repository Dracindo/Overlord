package the_fireplace.overlord.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.EntityBabySkeleton;

import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class TileEntityBabySkeletonMaker extends TileEntity implements ISidedInventory, ISkeletonMaker {
    private ItemStack[] inventory;
    public static final String PROP_NAME = "TileEntityBabySkeletonMaker";
    public static final int[] clearslots = new int[]{2,4,5,6,7,8,9};

    public TileEntityBabySkeletonMaker() {
        inventory = new ItemStack[10];
    }

    @Override
    public void spawnSkeleton(){
        UUID owner = null;
        if(getStackInSlot(0) != null){
            if(getStackInSlot(0).getTagCompound() != null){
                owner = UUID.fromString(getStackInSlot(0).getTagCompound().getString("Owner"));
            }
        }
        EntityBabySkeleton babySkeleton = new EntityBabySkeleton(worldObj, owner);
        babySkeleton.setLocationAndAngles(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1, 0);
        babySkeleton.setItemStackToSlot(EntityEquipmentSlot.HEAD, getStackInSlot(7));
        babySkeleton.setItemStackToSlot(EntityEquipmentSlot.CHEST, getStackInSlot(6));
        babySkeleton.setItemStackToSlot(EntityEquipmentSlot.LEGS, getStackInSlot(5));
        babySkeleton.setItemStackToSlot(EntityEquipmentSlot.FEET, getStackInSlot(4));
        babySkeleton.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, getStackInSlot(8));

        worldObj.spawnEntityInWorld(babySkeleton);
        if(getStackInSlot(9) != null)
            babySkeleton.applySkinsuit(getStackInSlot(9));
        for(int i:clearslots){
            setInventorySlotContents(i, null);
        }
        if(getStackInSlot(3) != null){
            if(getStackInSlot(3).stackSize < getStackInSlot(3).getMaxStackSize())
                getStackInSlot(3).stackSize++;
            else
                babySkeleton.entityDropItem(new ItemStack(Items.BUCKET), 0.1F);
        }else{
            setInventorySlotContents(3, new ItemStack(Items.BUCKET));
        }
        if(getStackInSlot(1) != null){
            if(getStackInSlot(1).stackSize <= ConfigValues.BONEREQ_BABY)
                setInventorySlotContents(1, null);
            else
                getStackInSlot(1).stackSize -= ConfigValues.BONEREQ_BABY;
        }
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, getBlockMetadata(), getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag(){
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public String getName() {
        return Overlord.proxy.translateToLocal("tile.baby_skeleton_maker.name");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack is = getStackInSlot(index);
        if (is != null) {
            if (is.stackSize <= count) {
                setInventorySlotContents(index, null);
            } else {
                is = is.splitStack(count);
                markDirty();
            }
        }
        return is;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack is = getStackInSlot(index);
        setInventorySlotContents(index, null);
        return is;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory[index] = stack;

        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return (index == 0 && stack.getItem() == Overlord.overlords_seal) || (index == 1 && stack.getItem() == Items.BONE) || (index == 2 && stack.getItem() == Items.MILK_BUCKET) || (index > 3 && index < 8 && stack.getItem().isValidArmor(stack, getSlotEquipmentType(index), null) || (index == 9 && stack.getItem() == Overlord.skinsuit));
    }

    private EntityEquipmentSlot getSlotEquipmentType(int index){
        if(index == 4)
            return EntityEquipmentSlot.FEET;
        if(index == 5)
            return EntityEquipmentSlot.LEGS;
        if(index == 6)
            return EntityEquipmentSlot.CHEST;
        if(index == 7)
            return EntityEquipmentSlot.HEAD;
        if(index == 8)
            return EntityEquipmentSlot.MAINHAND;
        return EntityEquipmentSlot.OFFHAND;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < inventory.length; ++i) {
            inventory[i] = null;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack is = getStackInSlot(i);
            if (is != null) {
                NBTTagCompound item = new NBTTagCompound();

                item.setByte("SlotSkeletonMaker", (byte) i);
                is.writeToNBT(item);

                list.appendTag(item);
            }
        }
        compound.setTag("ItemsSkeletonMaker", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = (NBTTagList) compound.getTag("ItemsSkeletonMaker");
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) list.get(i);
                int slot = item.getByte("SlotSkeletonMaker");
                if (slot >= 0 && slot < getSizeInventory()) {
                    setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
                }
            }
        } else {
            System.out.println("List was null when reading TileEntityBabySkeletonMaker NBTTagCompound");
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.EAST || side == EnumFacing.WEST || side == EnumFacing.NORTH || side == EnumFacing.SOUTH || side == EnumFacing.UP) {
            return new int[]{1, 2, 4, 5, 6, 7, 9};
        }
        if (side == EnumFacing.DOWN) {
            return new int[]{3};
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null) {
            if (index >= 1 && index < 3 || index >= 4 && index < 8 || index == 9) {
                if(this.isItemValidForSlot(index, stack))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (stack != null)
            if (index == 3)
                return true;
        return false;
    }

    IItemHandler handlerTop = new SidedInvWrapper(this, EnumFacing.UP);
    IItemHandler handlerBottom = new SidedInvWrapper(this, EnumFacing.DOWN);
    IItemHandler handlerSide = new SidedInvWrapper(this, EnumFacing.WEST);

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }
}
