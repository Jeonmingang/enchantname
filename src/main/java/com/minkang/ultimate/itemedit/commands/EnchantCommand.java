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

    private static final int MAX_LEVEL = 10000;
    private final Main plugin;

    public EnchantCommand(Main plugin) {
        this.plugin = plugin;
    }

    private ConfigurationSection cfg() {
        return plugin.getConfig().getConfigurationSection("enchant");
    }
    private String msg(String path, String def){
        String prefix = color(plugin.getConfig().getString("prefix", ""));
        String val = def;
        ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
        if (m != null && m.isString(path)) val = m.getString(path, def);
        return prefix + color(val);
    }
    private String color(String s){ if (s == null) return ""; return s.replace('&','§'); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(msg("not-player", "&c플레이어만 실행할 수 있습니다."));
            return true;
        }
        Player p = (Player) sender;

        if (!p.hasPermission("ultimate.itemedit.enchant")) {
            p.sendMessage(msg("no-permission", "&c권한이 없습니다."));
            return true;
        }

        List<String> worlds = cfg() != null ? cfg().getStringList("allowed-worlds") : Collections.<String>emptyList();
        if (worlds != null && !worlds.isEmpty()) {
            boolean ok = false;
            for (String s : worlds) if (s != null && s.equalsIgnoreCase(p.getWorld().getName())) { ok = true; break; }
            if (!ok) { p.sendMessage(msg("world-blocked", "&c이 월드에서는 사용할 수 없습니다.")); return true; }
        }

        if (args.length < 2) {
            p.sendMessage(msg("usage", "&c사용법: /인첸트 <인첸트> <수치>"));
            return true;
        }

        String enchName = args[0];
        String levelStr = args[1];
        int level;
        try { level = Integer.parseInt(levelStr); }
        catch (NumberFormatException ex) { p.sendMessage(msg("level-nan", "&c수치는 숫자여야 합니다.")); return true; }
        if (level < 1) level = 1;
        if (level > MAX_LEVEL) level = MAX_LEVEL;

        boolean enableAliases = cfg() == null || cfg().getBoolean("enable-korean-aliases", true);
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

        hand.addUnsafeEnchantment(enchantment, level);
        String templ = "&a인첸트 적용 완료: &f{enchant} &7레벨 &b{level}";
        ConfigurationSection m = cfg() != null ? cfg().getConfigurationSection("messages") : null;
        if (m != null && m.isString("applied-unsafe")) templ = m.getString("applied-unsafe", templ);
        p.sendMessage(color(templ.replace("{enchant}", getKey(enchantment)).replace("{level}", Integer.toString(level))));
        return true;
    }

    private String getKey(Enchantment e){
        try { NamespacedKey k = e.getKey(); if (k != null) return k.toString(); } catch (Throwable ignored) {}
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
                    if (k != null) { keys.add(k.toString()); keys.add(k.getKey()); }
                } catch (Throwable ignored) {}
                keys.add(e.getName());
            }
            if (cfg() == null || cfg().getBoolean("enable-korean-aliases", true)) {
                keys.addAll(Arrays.asList("날카로움","예리함","스마이트","살충","약탈","밀쳐내기","화염","수리",
                        "시저","힘","파워","펀치","불화살","무한","효율","견고함","행운","실크터치",
                        "보호","화염보호","폭발보호","투사체보호","가시","수중호흡","수중채굴","깊은바다추","가벼운착지","차가운걸음","영혼가속",
                        "행운의바다","미끼","충성","급류","집전","다중발사","관통","빠른장전"));
            }
            final String cur = args[0].toLowerCase(Locale.ROOT);
            List<String> out = keys.stream().distinct().filter(s -> s != null && s.toLowerCase(Locale.ROOT).startsWith(cur)).limit(50).collect(Collectors.toList());
            return out;
        } else if (args.length == 2) {
            return Arrays.asList("1","5","10","100","1000","10000");
        }
        return Collections.emptyList();
    }
}
