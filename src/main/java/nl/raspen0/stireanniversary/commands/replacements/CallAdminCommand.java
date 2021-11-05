package nl.raspen0.stireanniversary.commands.replacements;

import nl.raspen0.stireanniversary.LanguageHandler;
import nl.raspen0.stireanniversary.StringList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CallAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(StringList.COMMAND_REPLACEMENT_CALLADMIN.get(LanguageHandler.getLanguage(sender)));
        return true;
    }
}
