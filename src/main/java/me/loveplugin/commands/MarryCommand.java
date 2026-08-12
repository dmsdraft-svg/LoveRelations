package me.loveplugin.commands;

import me.loveplugin.Fmt;
import me.loveplugin.LovePlugin;
import me.loveplugin.managers.RequestManager;
import net.kyori.adventure.text.Component;
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
import java.util.UUID;

public class MarryCommand implements CommandExecutor, TabCompleter {

    private final LovePlugin plugin;

    public MarryCommand(LovePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("resetall")) handleResetAll(sender);
            else sender.sendMessage("Консоль: /marry resetall");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ffc7e0>Использование: <#ff6fae>/marry <ник></#ff6fae>, "
                    + "<#ff6fae>/marry accept</#ff6fae>, <#ff6fae>/marry deny</#ff6fae>, "
                    + "<#ff6fae>/marry divorce</#ff6fae></#ffc7e0>"));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "accept" -> handleAccept(player);
            case "deny" -> handleDeny(player);
            case "divorce" -> handleDivorce(player);
            case "resetall" -> handleResetAll(player);
            default -> sendProposal(player, args[0]);
        }
        return true;
    }

    private void sendProposal(Player sender, String name) {
        if (plugin.marriages().isMarried(sender.getUniqueId())) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Ты уже в браке!</#ff2e63>"));
            return;
        }
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Игрок " + name + " не найден или не в сети.</#ff2e63>"));
            return;
        }
        if (target.equals(sender)) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нельзя жениться на самом себе!</#ff2e63>"));
            return;
        }
        if (plugin.marriages().isMarried(target.getUniqueId())) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63><bold>" + target.getName() + "</bold> уже в браке.</#ff2e63>"));
            return;
        }

        plugin.requests().putMarry(target.getUniqueId(), sender.getUniqueId(), () -> {
            Player f = Bukkit.getPlayer(sender.getUniqueId());
            if (f != null) f.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Предложение для " + target.getName() + " истекло.</#a64dff>"));
            Player t = Bukkit.getPlayer(target.getUniqueId());
            if (t != null) t.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Предложение от " + sender.getName() + " истекло.</#a64dff>"));
        });
        target.sendMessage(Fmt.marryRequest(sender.getName()));
        sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Предложение руки и сердца отправлено <bold>"
                + target.getName() + "</bold> 💍</#ff6fae>"));
    }

    private void handleAccept(Player player) {
        RequestManager.Pending pending = plugin.requests().takeMarry(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>У тебя нет активных предложений.</#ff2e63>"));
            return;
        }
        Player from = Bukkit.getPlayer(pending.from());
        if (from == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Игрок уже вышел с сервера.</#ff2e63>"));
            return;
        }
        if (plugin.marriages().isMarried(from.getUniqueId()) || plugin.marriages().isMarried(player.getUniqueId())) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Один из вас уже в браке.</#ff2e63>"));
            from.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Один из вас уже в браке.</#ff2e63>"));
            return;
        }

        plugin.marriages().marry(from.getUniqueId(), player.getUniqueId());

        for (Player p : new Player[]{from, player}) {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        Component announce = Fmt.marriageAnnounce(from.getName(), player.getName());
        double radius = plugin.getConfig().getDouble("settings.marry-announce-radius", 40);
        double r2 = radius * radius;
        for (Player online : Bukkit.getOnlinePlayers()) {
            boolean near = false;
            for (Player part : new Player[]{from, player}) {
                if (online.getWorld().equals(part.getWorld())
                        && online.getLocation().distanceSquared(part.getLocation()) <= r2) {
                    near = true;
                    break;
                }
            }
            if (near) online.sendMessage(announce);
        }
    }

    private void handleDeny(Player player) {
        RequestManager.Pending pending = plugin.requests().takeMarry(player.getUniqueId());
        if (pending == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>У тебя нет активных предложений.</#ff2e63>"));
            return;
        }
        Player from = Bukkit.getPlayer(pending.from());
        if (from != null)
            from.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff><bold>" + player.getName() + "</bold> отклоняет предложение 💔</#a64dff>"));
        player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Ты отклоняешь предложение 💔</#a64dff>"));
    }

    private void handleDivorce(Player player) {
        UUID partner = plugin.marriages().getPartner(player.getUniqueId());
        if (partner == null) {
            player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Ты не в браке.</#ff2e63>"));
            return;
        }
        plugin.marriages().divorce(player.getUniqueId());
        Player other = Bukkit.getPlayer(partner);
        if (other != null) other.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Твой брак с <bold>"
                + player.getName() + "</bold> расторгнут 💔</#a64dff>"));
        player.sendMessage(Fmt.mm(Fmt.PREFIX + "<#a64dff>Брак расторгнут 💔</#a64dff>"));
    }

    private void handleResetAll(CommandSender sender) {
        if (!sender.hasPermission("plugin.admin")) {
            sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff2e63>Нет прав (plugin.admin).</#ff2e63>"));
            return;
        }
        int pairs = plugin.marriages().resetAll();
        sender.sendMessage(Fmt.mm(Fmt.PREFIX + "<#ff6fae>Все браки сброшены (" + pairs + " пар).</#ff6fae>"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String label, String[] args) {
        if (args.length == 1) {
            List<String> opts = new ArrayList<>(Arrays.asList("accept", "deny", "divorce"));
            if (sender.hasPermission("plugin.admin")) opts.add("resetall");
            Bukkit.getOnlinePlayers().forEach(p -> opts.add(p.getName()));
            return Fmt.filter(opts, args[0]);
        }
        return List.of();
    }
}
