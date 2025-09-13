package com.minkang.ultimate.itemedit.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemNameCommand implements CommandExecutor {
    private String c(String s){ return ChatColor.translateAlternateColorCodes('&', s); }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { sender.sendMessage("플레이어만 사용 가능합니다."); return true; }
        Player p=(Player)sender;
        if (!p.isOp() && !p.hasPermission("ultimate.itemedit.name")) { p.sendMessage(c("&c권한이 없습니다.")); return true; }
        ItemStack hand=p.getInventory().getItemInMainHand();
        if (hand==null || hand.getType()==Material.AIR){ p.sendMessage(c("&e손에 든 아이템이 없습니다.")); return true; }
        if (args.length==0){ p.sendMessage(c("&b사용법: &f/아이템이름 <이름...> &7또는 &f/아이템이름 삭제 (&7/iname 별칭 지원&f)")); return true; }
        ItemMeta meta=hand.getItemMeta();
        if (meta==null){ p.sendMessage(c("&c이 아이템은 이름을 변경할 수 없습니다.")); return true; }
        if (args[0].equalsIgnoreCase("삭제")){
            meta.setDisplayName(null); hand.setItemMeta(meta); p.sendMessage(c("&a아이템 이름을 제거했습니다.")); return true;
        }
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<args.length;i++){ if(i>0) sb.append(" "); sb.append(args[i]);}
        String newName=c(sb.toString());
        meta.setDisplayName(newName); hand.setItemMeta(meta);
        p.sendMessage(c("&a아이템 이름을 설정했습니다: &r")+newName);
        return true;
    }
}
