package nl.raspen0.stireanniversary;

import nl.raspen0.stiretweaks.language.Language;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportHandler {

    private final StireAnniversary plugin;

    public TeleportHandler(StireAnniversary plugin){
        this.plugin = plugin;
    }

    public void teleportPlayer(Player target, CommandSender sender, String baseType){
        teleportPlayer(target, sender, baseType, target.getWorld());
    }

    public void teleportPlayer(Player target, CommandSender sender, String baseType, World world){
        if(!EnumUtils.isValidEnum(BaseType.class, baseType)){
            sender.sendMessage(ChatColor.RED + "Invalid base type.");
            return;
        }
        Base base = plugin.getBase(target.getUniqueId(), world, BaseType.valueOf(baseType));
        teleportPlayer(target, sender, base);
    }

    public void teleportPlayer(Player target, CommandSender sender, Base base){
        if(base == null){
            sender.sendMessage(ChatColor.RED + "Base not found.");
            return;
        }

        Language language;

        if(!(sender instanceof Player player)){
            language = Language.ENG;
        } else{
            language = LanguageHandler.getLanguage(player.getUniqueId());
        }

        target.teleportAsync(base.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        sendTeleportMessage(target, language, base);
        if(!sender.getName().equals(target.getName())) {
            sender.sendMessage(ChatColor.AQUA + target.getName() + " has been teleported.");
        }
    }


    private void sendTeleportMessage(Player target, Language lang, Base base){
        if(base.getBaseType() != BaseType.FACTION && base.getBaseType() != BaseType.TOWN){
            return;
        }
        if(base.getName() == null){
            target.sendMessage(StringList.BASE_TELEPORT_MESSAGE_NO_NAME.get(lang).replace("{0}", StringList.valueOf(base.getBaseType().toString()).get(lang)));
        } else {
            target.sendMessage(StringList.BASE_TELEPORT_MESSAGE_NAME.get(lang).replace("{0}", base.getName()));
        }
    }
}
