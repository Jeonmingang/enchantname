package com.minkang.ultimate.itemedit.util;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class EnchantAliases {
    private static final Map<String, Enchantment> ALIASES = new HashMap<String, Enchantment>();

    static {
        putAlias("날카로움", Enchantment.DAMAGE_ALL);
        putAlias("예리함", Enchantment.DAMAGE_ALL);
        putAlias("스마이트", Enchantment.DAMAGE_UNDEAD);
        putAlias("언데드", Enchantment.DAMAGE_UNDEAD);
        putAlias("살충", Enchantment.DAMAGE_ARTHROPODS);
        putAlias("살충특", Enchantment.DAMAGE_ARTHROPODS);
        putAlias("약탈", Enchantment.LOOT_BONUS_MOBS);
        putAlias("밀쳐내기", Enchantment.KNOCKBACK);
        putAlias("화염", Enchantment.FIRE_ASPECT);
        putAlias("수리", Enchantment.MENDING);
        try { // sweeping_edge may not exist on very old
            putAlias("시저", Enchantment.SWEEPING_EDGE);
        } catch (Throwable ignored) {}

        putAlias("힘", Enchantment.ARROW_DAMAGE);
        putAlias("파워", Enchantment.ARROW_DAMAGE);
        putAlias("펀치", Enchantment.ARROW_KNOCKBACK);
        putAlias("불화살", Enchantment.ARROW_FIRE);
        putAlias("무한", Enchantment.ARROW_INFINITE);

        putAlias("효율", Enchantment.DIG_SPEED);
        putAlias("견고함", Enchantment.DURABILITY);
        putAlias("행운", Enchantment.LOOT_BONUS_BLOCKS);
        putAlias("실크터치", Enchantment.SILK_TOUCH);

        putAlias("보호", Enchantment.PROTECTION_ENVIRONMENTAL);
        putAlias("화염보호", Enchantment.PROTECTION_FIRE);
        putAlias("폭발보호", Enchantment.PROTECTION_EXPLOSIONS);
        putAlias("투사체보호", Enchantment.PROTECTION_PROJECTILE);
        putAlias("가시", Enchantment.THORNS);
        putAlias("수중호흡", Enchantment.OXYGEN);
        putAlias("수중채굴", Enchantment.WATER_WORKER);
        putAlias("깊은바다추", Enchantment.DEPTH_STRIDER);
        putAlias("가벼운착지", Enchantment.PROTECTION_FALL);
        try {
            putAlias("차가운걸음", Enchantment.FROST_WALKER);
        } catch (Throwable ignored) {}
        try {
            Enchantment soul = Enchantment.getByKey(NamespacedKey.minecraft("soul_speed"));
            if (soul != null) putAlias("영혼가속", soul);
        } catch (Throwable ignored) {}
    }

    private static void putAlias(String a, Enchantment e) {
        if (a != null && e != null) {
            ALIASES.put(a.toLowerCase(Locale.KOREAN), e);
        }
    }

    public static Enchantment find(String input, boolean enableAliases) {
        if (input == null) return null;
        String in = input.trim();

        if (enableAliases) {
            Enchantment aliased = ALIASES.get(in.toLowerCase(Locale.KOREAN));
            if (aliased != null) return aliased;
        }
        try {
            NamespacedKey key = NamespacedKey.fromString(in.toLowerCase(Locale.ROOT));
            if (key != null) {
                Enchantment byKey = Enchantment.getByKey(key);
                if (byKey != null) return byKey;
            }
        } catch (Throwable ignored) {}
        Enchantment byName = Enchantment.getByName(in.replace(' ', '_').replace('-', '_').toUpperCase(Locale.ROOT));
        return byName;
    }

    private EnchantAliases(){}
}
