package me.loveplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

public final class Fmt {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private Fmt() {}

    public static Component mm(String miniMessage) {
        return MM.deserialize(miniMessage);
    }

    public static final String BORDER =
            "<gradient:#ff2e63:#a64dff>━━━━━━━━━━</gradient> <#ff2e63>❤</#ff2e63> <gradient:#a64dff:#ff2e63>━━━━━━━━━━</gradient>";

    public static final String PREFIX =
            "<gradient:#ff2e63:#a64dff><bold>Love</bold></gradient><gradient:#a64dff:#ff2e63><bold>Relations</bold></gradient> <#ff6fae>»</#ff6fae> ";

    public static List<String> filter(List<String> options, String typed) {
        String low = typed.toLowerCase();
        return options.stream().filter(s -> s.toLowerCase().startsWith(low)).toList();
    }

    public static Component sexRequest(String fromName) {
        return mm(BORDER + "\n"
                + "<#ff2e63><bold>ИНТИМ-ЗАПРОС</bold></#ff2e63>\n"
                + "<#ffc7e0>От игрока: <#ff2e63><bold>" + fromName + "</bold></#ff2e63></#ffc7e0>\n"
                + "<#ffc7e0>У тебя есть <#ffd166>30 секунд</#ffd166>, чтобы ответить.</#ffc7e0>\n"
                + "<click:run_command:'/sex accept'><hover:show_text:'<#ff6fae>Нажми, чтобы принять'><#ff6fae><bold>[Принять]</bold></#ff6fae></hover></click> "
                + "<click:run_command:'/sex deny'><hover:show_text:'<#a64dff>Нажми, чтобы отклонить'><#a64dff><bold>[Отклонить]</bold></#a64dff></hover></click>\n"
                + BORDER);
    }

    public static Component marryRequest(String fromName) {
        return mm(BORDER + "\n"
                + "<#ff2e63><bold>БРАК</bold></#ff2e63>\n"
                + "<#ffc7e0>От игрока: <#ff2e63><bold>" + fromName + "</bold></#ff2e63></#ffc7e0>\n"
                + "<#ffc7e0>Игрок делает тебе предложение руки и сердца 💍</#ffc7e0>\n"
                + "<click:run_command:'/marry accept'><hover:show_text:'<#ff6fae>Нажми, чтобы принять'><#ff6fae><bold>[Принять]</bold></#ff6fae></hover></click> "
                + "<click:run_command:'/marry deny'><hover:show_text:'<#a64dff>Нажми, чтобы отклонить'><#a64dff><bold>[Отклонить]</bold></#a64dff></hover></click>\n"
                + BORDER);
    }

    public static Component marriageAnnounce(String a, String b) {
        return mm(BORDER + "\n"
                + "<#ff2e63><bold>💍 СВАДЬБА! 💍</bold></#ff2e63>\n"
                + "<#ffc7e0><#ff2e63><bold>" + a + "</bold></#ff2e63> и <#ff2e63><bold>" + b + "</bold></#ff2e63> теперь в браке! 💖</#ffc7e0>\n"
                + BORDER);
    }
}
