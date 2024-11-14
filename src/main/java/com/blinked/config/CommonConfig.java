package com.blinked.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CommonConfig {
    public static int createConfigInt(Configuration config, String category, String name, String comment, int def) {

        Property prop = config.get(category, name, def);
        prop.setComment(comment);
        return prop.getInt();
    }

    public static int minBlinkTimer = 100;
    public static int maxBlinkTimer = 300;
    public static int closeEyeDuration = 5;
    public static int blackScreenDuration = 5;
    public static int openEyeDuration = 5;
    public static void loadFromConfig(Configuration config) {
        minBlinkTimer = createConfigInt(config, "General", "minBlinkTimer", "Player needs to blink after this many ticks (it's random - so it's the min value of time) (default - 5 seconds)", 100);
        maxBlinkTimer = createConfigInt(config, "General", "maxBlinkTimer", "Player needs to blink after this many ticks (it's random - so it's the max value of time) (default - 15 seconds)", 300);
        closeEyeDuration = createConfigInt(config, "General", "closeEyeDuration", "How many ticks you need to close the 'eyes' (default - 5 ticks - 0.25 sec)", 5);
        blackScreenDuration = createConfigInt(config, "General", "blackScreenDuration", "How many ticks your 'eyes' are actually closed (default - 5 ticks - 0.25 sec)", 5);
        openEyeDuration = createConfigInt(config, "General", "openEyeDuration", "How many ticks you need to open the 'eyes' (default - 5 ticks - 0.25 sec)", 5);
    }


}
