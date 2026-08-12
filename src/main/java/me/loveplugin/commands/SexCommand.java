package me.loveplugin.commands;

import me.loveplugin.Fmt;
import me.loveplugin.LovePlugin;
import me.loveplugin.effects.SprayEffect;
import me.loveplugin.managers.RequestManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SexCommand implements CommandExecutor, TabCompleter {

    private final LovePlugin plugin;

    public SexCommand(LovePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) doReload(sender);
            else sender.sendMessage("Консоль: /sex reload");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ffc7e0>Использование: <#ff6fae>/sex <ник></#ff6fae>, "
                    + "<#ff6fae>/sex accept</#ff6fae>, <#ff6fae>/sex deny</#ff6fae></#ffc7e0>"));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "accept" -> handleAccept(player);
            case "deny" -> handleDeny(player);
            case "reload" -> doReload(player);
            default -> sendRequest(player, args[0]);
        }
        return true;
    }

    private void sendRequest(Player sender, String name) {
        if (!plugin.genders().has(sender.getUniqueId())) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Сначала выбери гендер!</#ff2e63>\n"
                    + "<click:suggest_command:'/gender set male'><hover:show_text:'<#ff6fae>Выбрать мужской'><#ff6fae><bold>[Мужской]</bold></#ff6fae></hover></click> "
                    + "<click:suggest_command:'/gender set female'><hover:show_text:'<#a64dff>Выбрать женский'><#a64dff><bold>[Женский]</bold></#a64dff></hover></click>"));
            return;
        }
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Игрок " + name + " не найден или не в сети.</#ff2e63>"));
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нельзя отправить запрос самому себе!</#ff2e63>"));
            return;
        }
        if (!plugin.genders().has(target.getUniqueId())) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>У игрока <bold>" + target.getName()
                    + "</bold> ещё не выбран гендер.</#ff2e63>"));
            return;
        }

        plugin.requests().putSex(target.getUniqueId(), sender.getUniqueId(), () -> {
            Player f = Bukkit.getPlayer(sender.getUniqueId());
            if (f != null) f.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Запрос к " + target.getName() + " истёк (30 сек).</#a64dff>"));
            Player t = Bukkit.getPlayer(target.getUniqueId());
            if (t != null) t.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Запрос от " + sender.getName() + " истёк.</#a64dff>"));
        });
        target.sendMessage(Fmt.sexRequest(sender.getName()));
        sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Запрос отправлен игроку <bold>" + target.getName() + "</bold> ❤</#ff6fae>"));
    }

    private void handleAccept(Player player) {
        RequestManager.Pending pending = plugin.requests().takeSex(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>У тебя нет активных запросов.</#ff2e63>"));
            return;
        }
        Player from = Bukkit.getPlayer(pending.from());
        if (from == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Игрок уже вышел с сервера.</#ff2e63>"));
            return;
        }

        for (Player p : new Player[]{from, player}) {
            p.playSound(p.getLocation(), Sound.ENTITY_HORSE_DEATH, 1.0f, 0.1f);
            p.swingMainHand();
        }
        new SprayEffect(plugin, from).runTaskTimer(plugin, 0L, 1L);

        from.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae><bold>" + player.getName() + "</bold> принимает твой запрос ❤</#ff6fae>"));
        player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Ты принимаешь запрос от <bold>" + from.getName() + "</bold> ❤</#ff6fae>"));
    }

    private void handleDeny(Player player) {
        RequestManager.Pending pending = plugin.requests().takeSex(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>У тебя нет активных запросов.</#ff2e63>"));
            return;
        }
        Player from = Bukkit.getPlayer(pending.from());
        if (from != null)
            from.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff><bold>" + player.getName() + "</bold> отклоняет твой запрос.</#a64dff>"));
        player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Ты отклоняешь запрос.</#a64dff>"));
    }

    private void doReload(CommandSender sender) {
        if (!sender.hasPermission("plugin.admin")) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нет прав (plugin.admin).</#ff2e63>"));
            return;
        }
        plugin.reloadConfig();
        plugin.genders().reload();
        plugin.marriages().reload();
        sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Конфигурация и данные перезагружены.</#ff6fae>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, String[] args) {
        if (args.length == 1) {
            List<String> opts = new ArrayList<>(Arrays.asList("accept", "deny"));
            if (sender.hasPermission("plugin.admin")) opts.add("reload");
            Bukkit.getOnlinePlayers().forEach(p -> opts.add(p.getName()));
            return Fmt.filter(opts, args[0]);
        }
        return List.of();
    }
}
