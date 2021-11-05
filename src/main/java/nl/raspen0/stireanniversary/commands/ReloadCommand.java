package nl.raspen0.stireanniversary.commands;

import nl.raspen0.stireanniversary.LanguageHandler;
import nl.raspen0.stireanniversary.StireAnniversary;
import nl.raspen0.stireanniversary.StringList;
import nl.raspen0.stiretweaks.language.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final StireAnniversary plugin;

    public ReloadCommand(StireAnniversary plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Language lang = sender instanceof Player ? LanguageHandler.getLanguage(((Player) sender).getUniqueId()) : Language.ENG;
        if(!sender.hasPermission("stireanniversary.command.reload")){
            sender.sendMessage(StringList.NO_PERMISSION.get(lang));
            return true;
        }
        plugin.reload();
        return true;
    }
}
