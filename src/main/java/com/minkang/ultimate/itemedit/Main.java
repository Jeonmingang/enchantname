package com.minkang.ultimate.itemedit;

import com.minkang.ultimate.itemedit.commands.ItemLoreCommand;
import com.minkang.ultimate.itemedit.commands.ItemNameCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        if (getCommand("아이템이름") != null) getCommand("아이템이름").setExecutor(new ItemNameCommand());
        if (getCommand("아이템설명") != null) getCommand("아이템설명").setExecutor(new ItemLoreCommand());
        // ASCII aliases for CatServer edge cases
        if (getCommand("iname") != null) getCommand("iname").setExecutor(new ItemNameCommand());
        if (getCommand("ilore") != null) getCommand("ilore").setExecutor(new ItemLoreCommand());
        getLogger().info("[UltimateItemEdit] Enabled v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("[UltimateItemEdit] Disabled");
    }
}
