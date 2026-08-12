package me.loveplugin;

import me.loveplugin.commands.GenderCommand;
import me.loveplugin.commands.MarryCommand;
import me.loveplugin.commands.SexCommand;
import me.loveplugin.commands.SexHelpCommand;
import me.loveplugin.managers.GenderManager;
import me.loveplugin.managers.MarriageManager;
import me.loveplugin.managers.RequestManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class LovePlugin extends JavaPlugin {

    private static LovePlugin instance;
    private GenderManager genderManager;
    private MarriageManager marriageManager;
    private RequestManager requestManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (!new File(getDataFolder(), "data.yml").exists()) saveResource("data.yml", false);

        genderManager = new GenderManager(this);
        marriageManager = new MarriageManager(this);
        requestManager = new RequestManager(this);

        GenderCommand genderCmd = new GenderCommand(this);
        SexCommand sexCmd = new SexCommand(this);
        MarryCommand marryCmd = new MarryCommand(this);
        SexHelpCommand helpCmd = new SexHelpCommand(this);

        getCommand("gender").setExecutor(genderCmd);
        getCommand("gender").setTabCompleter(genderCmd);
        getCommand("sex").setExecutor(sexCmd);
        getCommand("sex").setTabCompleter(sexCmd);
        getCommand("marry").setExecutor(marryCmd);
        getCommand("marry").setTabCompleter(marryCmd);
        getCommand("sexhelp").setExecutor(helpCmd);
        getCommand("sexhelp").setTabCompleter(helpCmd);

        getLogger().info("LoveRelations включён!");
    }

    @Override
    public void onDisable() {
        genderManager.save();
        marriageManager.save();
    }

    public static LovePlugin get() { return instance; }
    public GenderManager genders() { return genderManager; }
    public MarriageManager marriages() { return marriageManager; }
    public RequestManager requests() { return requestManager; }
}
