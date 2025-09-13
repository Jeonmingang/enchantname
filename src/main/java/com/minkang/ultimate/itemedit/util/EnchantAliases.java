package com.minkang.ultimate.itemedit.util;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class EnchantAliases {
    private static final Map<String, Enchantment> A = new HashMap<String, Enchantment>();

    static {
        // Armor
        map(new String[]{"protection", "보호"}, Enchantment.PROTECTION_ENVIRONMENTAL);
        map(new String[]{"fire_protection", "fire-protection", "화염보호"}, Enchantment.PROTECTION_FIRE);
        map(new String[]{"feather_falling", "feather-falling", "feather", "가벼운착지"}, Enchantment.PROTECTION_FALL);
        map(new String[]{"blast_protection", "blast-protection", "폭발보호"}, Enchantment.PROTECTION_EXPLOSIONS);
        map(new String[]{"projectile_protection", "projectile-protection", "투사체보호"}, Enchantment.PROTECTION_PROJECTILE);
        map(new String[]{"thorns", "가시"}, Enchantment.THORNS);
        map(new String[]{"respiration", "호흡", "수중호흡"}, Enchantment.OXYGEN);
        map(new String[]{"aqua_affinity", "aqua-affinity", "수중채굴"}, Enchantment.WATER_WORKER);
        map(new String[]{"depth_strider", "depth-strider", "깊은바다추"}, Enchantment.DEPTH_STRIDER);
        try { map(new String[]{"frost_walker","frost-walker","차가운걸음"}, Enchantment.FROST_WALKER); } catch (Throwable ignored) {}
        map(new String[]{"binding_curse","저주:결속","결속의저주","결속저주"}, safeByKey("binding_curse"));
        map(new String[]{"vanishing_curse","저주:소실","소실의저주","소실저주"}, safeByKey("vanishing_curse"));

        // Sword
        map(new String[]{"sharpness", "날카로움", "예리함"}, Enchantment.DAMAGE_ALL);
        map(new String[]{"smite", "스마이트"}, Enchantment.DAMAGE_UNDEAD);
        map(new String[]{"bane_of_arthropods", "bane-of-arthropods", "살충", "살충특"}, Enchantment.DAMAGE_ARTHROPODS);
        map(new String[]{"knockback", "밀쳐내기"}, Enchantment.KNOCKBACK);
        map(new String[]{"fire_aspect", "fire-aspect", "화염"}, Enchantment.FIRE_ASPECT);
        map(new String[]{"looting", "약탈"}, Enchantment.LOOT_BONUS_MOBS);
        try { map(new String[]{"sweeping_edge","sweeping-edge","휩쓸기","시저"}, Enchantment.SWEEPING_EDGE); } catch (Throwable ignored) {}

        // Tools
        map(new String[]{"efficiency", "효율"}, Enchantment.DIG_SPEED);
        map(new String[]{"silk_touch", "silk-touch", "실크터치"}, Enchantment.SILK_TOUCH);
        map(new String[]{"unbreaking", "내구성", "견고함"}, Enchantment.DURABILITY);
        map(new String[]{"fortune", "행운"}, Enchantment.LOOT_BONUS_BLOCKS);

        // Bow
        map(new String[]{"power", "힘", "파워"}, Enchantment.ARROW_DAMAGE);
        map(new String[]{"punch", "펀치"}, Enchantment.ARROW_KNOCKBACK);
        map(new String[]{"flame", "불화살"}, Enchantment.ARROW_FIRE);
        map(new String[]{"infinity", "무한"}, Enchantment.ARROW_INFINITE);

        // Fishing
        map(new String[]{"luck_of_the_sea","luck-of-the-sea","행운의바다","행운바다"}, Enchantment.LUCK);
        map(new String[]{"lure","미끼","유인"}, Enchantment.LURE);

        // Trident
        map(new String[]{"loyalty","충성"}, safeByKey("loyalty"));
        map(new String[]{"impaling","찌르기"}, safeByKey("impaling"));
        map(new String[]{"riptide","급류"}, safeByKey("riptide"));
        map(new String[]{"channeling","집전"}, safeByKey("channeling"));

        // Crossbow
        map(new String[]{"multishot","다중발사"}, safeByKey("multishot"));
        map(new String[]{"piercing","관통"}, safeByKey("piercing"));
        map(new String[]{"quick_charge","quick-charge","빠른장전"}, safeByKey("quick_charge"));

        // Boots (Nether 1.16)
        map(new String[]{"soul_speed","영혼가속"}, safeByKey("soul_speed"));

        // Misc
        map(new String[]{"mending", "수리"}, Enchantment.MENDING);
    }

    private static Enchantment safeByKey(String key) {
        try {
            return Enchantment.getByKey(NamespacedKey.minecraft(key));
        } catch (Throwable t) {
            return null;
        }
    }

    private static void map(String[] names, Enchantment ench) {
        if (ench == null) return;
        for (String n : names) {
            if (n == null) continue;
            A.put(n.toLowerCase(Locale.ROOT), ench);
        }
    }

    public static Enchantment find(String input, boolean enableAliases) {
        if (input == null) return null;
        String in = input.trim().toLowerCase(Locale.ROOT);

        Enchantment e = A.get(in);
        if (e != null) return e;

        try {
            NamespacedKey key = NamespacedKey.fromString(in);
            if (key != null) {
                Enchantment byKey = Enchantment.getByKey(key);
                if (byKey != null) return byKey;
            }
        } catch (Throwable ignored) {}

        String normalized = in.replace(' ', '_').replace('-', '_').toUpperCase(Locale.ROOT);
        Enchantment byName = Enchantment.getByName(normalized);
        if (byName != null) return byName;

        return null;
    }

    private EnchantAliases(){}
}
