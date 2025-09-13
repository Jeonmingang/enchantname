package com.minkang.ultimate.itemedit.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemLoreCommand implements CommandExecutor {

    private String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용 가능합니다.");
            return true;
        }
        Player p = (Player) sender;

        if (!p.isOp() && !p.hasPermission("ultimate.itemedit.lore")) {
            p.sendMessage(colorize("&c권한이 없습니다."));
            return true;
        }

        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) {
            p.sendMessage(colorize("&e손에 든 아이템이 없습니다."));
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(colorize("&b사용법: &f/아이템설명 추가 <문장...> &7또는 &f/아이템설명 삭제"));
            p.sendMessage(colorize("&7힌트: &f/아이템설명 <문장...> &7만 입력해도 한 줄이 추가됩니다."));
            return true;
        }

        ItemMeta meta = hand.getItemMeta();
        if (meta == null) {
            p.sendMessage(colorize("&c이 아이템은 설명을 변경할 수 없습니다."));
            return true;
        }

        // Initialize lore list
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<String>();

        if (args[0].equalsIgnoreCase("삭제")) {
            if (meta.hasLore()) {
                meta.setLore(null);
                hand.setItemMeta(meta);
                p.sendMessage(colorize("&a아이템 설명(lore)을 모두 제거했습니다."));
            } else {
                p.sendMessage(colorize("&e제거할 설명이 없습니다."));
            }
            return true;
        }

        // support '/아이템설명 추가 ...' or just '/아이템설명 ...'
        int startIdx = 0;
        if (args[0].equalsIgnoreCase("추가")) {
            if (args.length == 1) {
                p.sendMessage(colorize("&c추가할 문장을 입력하세요. 예) &f/아이템설명 추가 전설의 검"));
                return true;
            }
            startIdx = 1;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i < args.length; i++) {
            if (i > startIdx) sb.append(" ");
            sb.append(args[i]);
        }
        String line = colorize(sb.toString());
        lore.add(line);
        meta.setLore(lore);
        hand.setItemMeta(meta);
        p.sendMessage(colorize("&a설명 한 줄을 추가했습니다: &r") + line);
        return true;
    }
}
