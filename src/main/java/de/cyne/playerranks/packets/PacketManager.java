package de.cyne.playerranks.packets;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class PacketManager {

    public static Field ENTRIES;
    public static Field PREFIX;
    public static Field SUFFIX;
    public static Field TEAM_NAME;
    public static Field PARAM_INT;
    public static Field PACK_OPTION;
    public static Field DISPLAY_NAME;
    public static Field TEAM_COLOR;
    public static Field PUSH;
    public static Field VISIBILITY;

    public PacketManager() {

        try {
            PacketData currentVersion = null;
            for (PacketData packetData : PacketData.values()) {
                if (this.getVersion().contains(packetData.name())) {
                    currentVersion = packetData;
                }
            }

            if (currentVersion != null) {
                PREFIX = getField(currentVersion.getPrefix());
                SUFFIX = getField(currentVersion.getSuffix());
                ENTRIES = getField(currentVersion.getEntries());
                TEAM_NAME = getField(currentVersion.getTeamName());
                PARAM_INT = getField(currentVersion.getParamInt());
                PACK_OPTION = getField(currentVersion.getPackOption());
                DISPLAY_NAME = getField(currentVersion.getDisplayName());

                if (!isLegacyVersion())
                    TEAM_COLOR = getField(currentVersion.getColor());

                if (isPushVersion())
                    PUSH = getField(currentVersion.getPush());

                if (isVisibilityVersion())
                    VISIBILITY = getField(currentVersion.getVisibility());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static boolean isLegacyVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) <= 12;
    }

    private static boolean isPushVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) >= 9;
    }

    private static boolean isVisibilityVersion() {
        return Integer.parseInt(getVersion().split("_")[1]) >= 8;
    }

    private Class<?> getNMSClass(String name) {
        String version = getVersion();
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    private Field getField(String path) throws Exception {
        Field field = this.getNMSClass("PacketPlayOutScoreboardTeam").getDeclaredField(path);
        field.setAccessible(true);
        return field;
    }

}
