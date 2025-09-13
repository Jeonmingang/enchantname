package com.minkang.ultimate.itemedit.commands;

import com.minkang.ultimate.itemedit.Main;
import com.minkang.ultimate.itemedit.util.EnchantAliases;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public EnchantCommand(Main plugin) {
        this.plugin = plugin;
    }

    private ConfigurationSection cfg() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("enchant");
        return sec;
    }
    private String msg(String path, String def){
        String prefix = color(plugin.getConfig().getString("prefix", "")); // fall back to global prefix if set
        String val = def;
        ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
        if (m != null && m.isString(path)) val = m.getString(path, def);
        return prefix + color(val);
    }
    private String color(String s){
        if (s == null) return "";
        return s.replace('&','§');
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("not-player", "&c플레이어만 실행할 수 있습니다."));
            return true;
        }
        Player p = (Player) sender;

        // Permission (OP only by default via plugin.yml)
        if (!p.hasPermission("ultimate.itemedit.enchant")) {
            p.sendMessage(msg("no-permission", "&c권한이 없습니다."));
            return true;
        }

        ConfigurationSection c = cfg();
        boolean enableAliases = c == null || c.getBoolean("enable-korean-aliases", true);
        boolean allowUnsafe = c == null || c.getBoolean("allow-unsafe-levels", true);
        boolean respectVanillaMax = c == null || c.getBoolean("respect-vanilla-max", true);
        List<String> worlds = c != null ? c.getStringList("allowed-worlds") : Collections.<String>emptyList();

        if (worlds != null && !worlds.isEmpty()) {
            World w = p.getWorld();
            boolean ok = false;
            for (String s : worlds) {
                if (s != null && s.equalsIgnoreCase(w.getName())) {
                    ok = true;
                    break;
                }
            }
            if (!ok) {
                p.sendMessage(msg("world-blocked", "&c이 월드에서는 사용할 수 없습니다."));
                return true;
            }
        }

        if (args.length < 2) {
            p.sendMessage(msg("usage", "&c사용법: /인첸트 <인첸트> <수치>"));
            return true;
        }

        String enchName = args[0];
        String levelStr = args[1];
        int level;
        try { level = Integer.parseInt(levelStr); }
        catch (NumberFormatException ex) {
            p.sendMessage(msg("level-nan", "&c수치는 숫자여야 합니다."));
            return true;
        }

        Enchantment enchantment = EnchantAliases.find(enchName, enableAliases);
        if (enchantment == null) {
            String templ = "&c알 수 없는 인첸트입니다: &f{name}";
            ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
            if (m != null && m.isString("unknown-enchant")) templ = m.getString("unknown-enchant", templ);
            p.sendMessage(color(templ.replace("{name}", enchName)));
            return true;
        }

        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == org.bukkit.Material.AIR) {
            p.sendMessage(msg("no-item", "&c손에 아이템을 들어주세요."));
            return true;
        }

        if (allowUnsafe) {
            hand.addUnsafeEnchantment(enchantment, level);
            String templ = "&a인첸트 적용 완료: &f{enchant} &7레벨 &b{level} &7(무제한 적용)";
            ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
            if (m != null && m.isString("applied-unsafe")) templ = m.getString("applied-unsafe", templ);
            p.sendMessage(color(templ.replace("{enchant}", getKey(enchantment)).replace("{level}", Integer.toString(level))));
        } else {
            int finalLevel = level;
            if (respectVanillaMax) {
                int max = enchantment.getMaxLevel();
                if (finalLevel > max) finalLevel = max;
                if (finalLevel < 1) finalLevel = 1;
            }
            hand.addEnchantment(enchantment, finalLevel);
            String templ = "&a인첸트 적용 완료: &f{enchant} &7레벨 &b{level} &7(안전 적용)";
            ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
            if (m != null && m.isString("applied-safe")) templ = m.getString("applied-safe", templ);
            p.sendMessage(color(templ.replace("{enchant}", getKey(enchantment)).replace("{level}", Integer.toString(finalLevel))));
        }
        return true;
    }

    private String getKey(Enchantment e){
        try {
            NamespacedKey k = e.getKey();
            if (k != null) return k.toString();
        } catch (Throwable ignored) {}
        return e.getName();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();
        if (args.length == 1) {
            List<String> keys = new ArrayList<String>();
            for (Enchantment e : Enchantment.values()) {
                if (e == null) continue;
                try {
                    NamespacedKey k = e.getKey();
                    if (k != null) {
                        keys.add(k.toString());
                        keys.add(k.getKey());
                    }
                } catch (Throwable ignored) {}
                keys.add(e.getName());
            }
            if (cfg() == null || cfg().getBoolean("enable-korean-aliases", true)) {
                keys.addAll(Arrays.asList("날카로움","예리함","스마이트","살충","약탈","밀쳐내기","화염","수리",
                        "시저","힘","파워","펀치","불화살","무한","효율","견고함","행운","실크터치",
                        "보호","화염보호","폭발보호","투사체보호","가시","수중호흡","수중채굴","깊은바다추","가벼운착지","차가운걸음","영혼가속"));
            }
            final String cur = args[0].toLowerCase(Locale.ROOT);
            return keys.stream().distinct()
                    .filter(s -> s != null && s.toLowerCase(Locale.ROOT).startsWith(cur))
                    .limit(50).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("1","2","3","4","5","6","7","8","9","10");
        }
        return Collections.emptyList();
    }
}
