package the_fireplace.overlord;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import the_fireplace.overlord.entity.EntityArmyMember;
import the_fireplace.overlord.entity.EntityBabySkeleton;
import the_fireplace.overlord.entity.EntitySkeletonWarrior;

import java.util.Random;

/**
 * @author The_Fireplace
 */
public class CommonEvents {
    @SubscribeEvent
    public void rightClickEntity(PlayerInteractEvent.EntityInteract event){
        if(event.getTarget() instanceof EntitySkeleton || ((event.getTarget() instanceof EntitySkeletonWarrior || event.getTarget() instanceof EntityBabySkeleton) && event.getEntityPlayer().isSneaking()))
            if(((EntityLivingBase) event.getTarget()).getHealth() < ((EntityLivingBase) event.getTarget()).getMaxHealth())
                if(event.getItemStack() != null)
                    if(event.getItemStack().getItem() == Items.MILK_BUCKET){
                        ((EntityLivingBase) event.getTarget()).heal(1);
                        if(!event.getEntityPlayer().isCreative()) {
                            if(event.getItemStack().stackSize > 1)
                                event.getItemStack().stackSize--;
                            else
                                event.getEntityPlayer().setItemStackToSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND, null);
                            event.getEntityPlayer().inventory.addItemStackToInventory(new ItemStack(Items.BUCKET));
                        }
                        if(event.getTarget() instanceof EntitySkeletonWarrior)
                            ((EntitySkeletonWarrior) event.getTarget()).increaseMilkLevel(false);
                    }
    }
    @SubscribeEvent
    public void entityTick(LivingEvent.LivingUpdateEvent event){
        if(!event.getEntityLiving().worldObj.isRemote){
            if(event.getEntityLiving() instanceof EntitySkeleton || event.getEntityLiving() instanceof EntitySkeletonWarrior || event.getEntityLiving() instanceof EntityBabySkeleton){
                if(event.getEntityLiving().ticksExisted < 5){
                    if(event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD) == null){
                        Random random = new Random();
                        if(random.nextInt(1200) == 0)
                            event.getEntityLiving().setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Overlord.sans_mask));
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void configCahnged(ConfigChangedEvent.OnConfigChangedEvent event){
        if(event.getModID().equals(Overlord.MODID)){
            Overlord.syncConfig();
        }
    }
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event){
        if(!event.getEntity().worldObj.isRemote)
        if(event.getSource().isProjectile()){
            if(event.getEntityLiving() instanceof EntityPlayerMP){
                if(event.getSource().getEntity() instanceof EntitySkeletonWarrior){
                    if(((EntitySkeletonWarrior) event.getSource().getEntity()).getOwnerId().equals(event.getEntityLiving().getUniqueID())){
                        if(((EntityPlayerMP) event.getEntityLiving()).getStatFile().canUnlockAchievement(Overlord.nmyi))
                            ((EntityPlayerMP) event.getEntityLiving()).addStat(Overlord.nmyi);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void livingDeath(LivingDeathEvent event){
        if(!event.getEntityLiving().worldObj.isRemote){
            if(event.getSource().getEntity() instanceof EntityWolf && event.getEntityLiving() instanceof EntityArmyMember){
                if(((EntityWolf) event.getSource().getEntity()).getOwnerId() != null){
                    EntityPlayer wolfOwner = ((EntityArmyMember) event.getEntityLiving()).worldObj.getPlayerEntityByUUID(((EntityWolf) event.getSource().getEntity()).getOwnerId());
                    if(wolfOwner != null){
                        if(wolfOwner instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) wolfOwner).getStatFile().canUnlockAchievement(Overlord.wardog)) {
                                wolfOwner.addStat(Overlord.wardog);
                            }
                    }
                }
            }
        }
    }
}
