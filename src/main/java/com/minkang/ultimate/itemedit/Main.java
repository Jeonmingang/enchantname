package com.minkang.ultimate.itemedit;

import com.minkang.ultimate.itemedit.commands.ItemLoreCommand;
import com.minkang.ultimate.itemedit.commands.ItemNameCommand;
import com.minkang.ultimate.itemedit.commands.EnchantCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register commands
        if (getCommand("아이템이름") != null) {
            getCommand("아이템이름").setExecutor(new ItemNameCommand());
        }
        if (getCommand("아이템설명") != null) {
            getCommand("아이템설명").setExecutor(new ItemLoreCommand());
        }

if (getCommand("인첸트") != null) {
    EnchantCommand ec = new EnchantCommand(this);
    getCommand("인첸트").setExecutor(ec);
    getCommand("인첸트").setTabCompleter(ec);
}
if (getCommand("인첸트리로드") != null) {
    getCommand("인첸트리로드").setExecutor((sender, cmd, lbl, args) -> {
        if (sender instanceof org.bukkit.entity.Player) {
            org.bukkit.entity.Player p = (org.bukkit.entity.Player) sender;
            if (!p.hasPermission("ultimate.itemedit.admin")) {
                p.sendMessage(getConfig().getString("no-perm", "§c권한이 없습니다."));
                return true;
            }
        }
        reloadConfig();
        sender.sendMessage(getConfig().getString("prefix", "") + "§a설정을 리로드했습니다.");
        return true;
    });
}

        getLogger().info("[UltimateItemEdit] Enabled v" + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        getLogger().info("[UltimateItemEdit] Disabled");
    }
}
