package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;

/**
 * @author The_Fireplace
 */
public class CommandAllyRemove extends CommandBase {
    @Override
    public String getCommandName() {
        return "allyremove";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(args.length == 1){
                EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(args[0]);
                if(player != null) {
                    if (Alliances.getInstance().isAlliedTo(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                        for (Alliance alliance : Alliances.getInstance().getAlliances()) {
                            if (alliance.getUser1().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser2().getUUID().equals(player.getUniqueID().toString())) {
                                Alliances.getInstance().removeAlliance(alliance);
                                break;
                            } else if (alliance.getUser2().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser1().getUUID().equals(player.getUniqueID().toString())) {
                                Alliances.getInstance().removeAlliance(alliance);
                                break;
                            }
                        }
                        if(sender instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.breakalliance))
                                ((EntityPlayer)sender).addStat(Overlord.breakalliance);
                        player.addChatMessage(new TextComponentTranslation("overlord.allytermination", ((EntityPlayer) sender).getDisplayNameString()));
                        sender.addChatMessage(new TextComponentTranslation("overlord.allyterminated", player.getDisplayNameString()));
                    } else {
                        sender.addChatMessage(new TextComponentTranslation("overlord.notallied", player.getDisplayNameString()));
                    }
                }else{
                    sender.addChatMessage(new TextComponentTranslation("commands.generic.player.notFound"));
                }
            }else{
                throw new WrongUsageException(getCommandUsage(sender));
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/allyremove <PlayerName>";
    }
}
