package me.loveplugin.commands;

import me.loveplugin.Fmt;
import me.loveplugin.LovePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GenderCommand implements CommandExecutor, TabCompleter {

    private final LovePlugin plugin;

    public GenderCommand(LovePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Использование: /gender set <male|female> [игрок]</#ff2e63>"));
                return true;
            }
            String gender = args[1].toLowerCase(Locale.ROOT);
            if (!gender.equals("male") && !gender.equals("female")) {
                sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Неизвестный гендер. Доступно: male, female.</#ff2e63>"));
                return true;
            }

            Player target;
            if (args.length >= 3) {
                if (!sender.hasPermission("plugin.admin")) {
                    sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нет прав (plugin.admin) на принудительную смену гендера.</#ff2e63>"));
                    return true;
                }
                target = Bukkit.getPlayerExact(args[2]);
                if (target == null) {
                    sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Игрок " + args[2] + " не найден или не в сети.</#ff2e63>"));
                    return true;
                }
            } else if (sender instanceof Player p) {
                target = p;
            } else {
                sender.sendMessage("Консоль: /gender set <male|female> <ник>");
                return true;
            }

            plugin.genders().set(target.getUniqueId(), gender);
            String ru = plugin.genders().ruName(gender);
            target.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Твой гендер установлен: <bold>" + ru + "</bold> ❤</#ff6fae>"));
            if (!target.equals(sender)) {
                sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Гендер игрока <bold>" + target.getName()
                        + "</bold> установлен: <bold>" + ru + "</bold>.</#ff6fae>"));
            }
            return true;
        }

        if (sender instanceof Player p) {
            String g = plugin.genders().get(p.getUniqueId());
            p.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ffc7e0>Твой гендер: <#ff2e63><bold>" + plugin.genders().ruName(g)
                    + "</bold></#ff2e63>" + (g == null ? " <#a64dff>(/gender set male|female)</#a64dff>" : "")));
        } else {
            sender.sendMessage("Консоль: /gender set <male|female> <ник>");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, String[] args) {
        if (args.length == 1) return Fmt.filter(Arrays.asList("set"), args[0]);
        if (args.length == 2 && args[0].equalsIgnoreCase("set"))
            return Fmt.filter(Arrays.asList("male", "female"), args[1]);
        if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("plugin.admin"))
            return null;
        return List.of();
    }
}
