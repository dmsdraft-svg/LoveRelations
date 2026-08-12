package me.loveplugin.commands;

import me.loveplugin.Fmt;
import me.loveplugin.LovePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SexHelpCommand implements CommandExecutor, TabCompleter {

    private final LovePlugin plugin;

    public SexHelpCommand(LovePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        boolean adminArg = args.length >= 1 && args[0].equalsIgnoreCase("admin");
        if (adminArg && !sender.hasPermission("plugin.admin")) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нет прав (plugin.admin).</#ff2e63>"));
            return true;
        }

        StringBuilder sb = new StringBuilder(playerHelp());
        if (sender.hasPermission("plugin.admin")) sb.append("\n").append(adminHelp());
        sender.sendMessage(Fmt.mm(sb.toString()));
        return true;
    }

    private String playerHelp() {
        return Fmt.BORDER + "\n"
                + "<#ff2e63><bold>СПРАВКА ПО КОМАНДАМ</bold></#ff2e63>\n"
                + "<click:suggest_command:'/gender set '><hover:show_text:'<#ff6fae>Выбрать гендер'><#ff6fae>/gender set <male|female></#ff6fae></hover></click> <#ffc7e0>— выбрать гендер</#ffc7e0>\n"
                + "<click:suggest_command:'/sex '><hover:show_text:'<#ff6fae>Интим-запрос'><#ff6fae>/sex <ник></#ff6fae></hover></click> <#ffc7e0>— предложить интим</#ffc7e0>\n"
                + "<click:suggest_command:'/marry '><hover:show_text:'<#ff6fae>Предложение брака'><#ff6fae>/marry <ник></#ff6fae></hover></click> <#ffc7e0>— предложить брак</#ffc7e0>\n"
                + Fmt.BORDER;
    }

    private String adminHelp() {
        return "<#a64dff><bold>АДМИН-КОМАНДЫ</bold></#a64dff>\n"
                + "<#ff6fae>/sex reload</#ff6fae> <#ffc7e0>— перезагрузить конфиг и data.yml</#ffc7e0>\n"
                + "<#ff6fae>/gender set <male|female> <ник></#ff6fae> <#ffc7e0>— принудительная смена гендера</#ffc7e0>\n"
                + "<#ff6fae>/marry divorce</#ff6fae> <#ffc7e0>— развод</#ffc7e0>\n"
                + "<#ff6fae>/marry resetall</#ff6fae> <#ffc7e0>— сброс всех браков</#ffc7e0>\n"
                + Fmt.BORDER;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, String[] args) {
        if (args.length == 1) return Fmt.filter(List.of("admin"), args[0]);
        return List.of();
    }
}
