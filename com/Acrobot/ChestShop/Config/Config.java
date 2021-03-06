package com.Acrobot.ChestShop.Config;

import com.nijikokun.register.payment.forChestShop.Methods;

/**
 * @author Acrobot
 */
public class Config {
    public static ConfigObject config;

    public static void setup(ConfigObject cfg) {
        config = cfg;
        Methods.setPreferred(Config.getString(Property.PREFERRED_ECONOMY_PLUGIN));
    }

    public static boolean getBoolean(Property value) {
        return (Boolean) getValue(value.name());
    }

    public static float getFloat(Property value) {
        return getFloat(value.name());
    }

    public static float getFloat(String value) {
        return new Float(getValue(value).toString());
    }

    public static String getString(Property value) {
        return (String) getValue(value.name());
    }

    public static int getInteger(Property value) {
        return Integer.parseInt(getValue(value.name()).toString());
    }

    public static double getDouble(Property value) {
        return getDouble(getValue(value.name()).toString());
    }

    public static double getDouble(String value) {
        return Double.parseDouble(getValue(value).toString());
    }

    private static String getColored(String msg) {
        return msg.replaceAll("&([0-9a-f])", "\u00A7$1");
    }

    public static String getLocal(Language lang) {
        return getColored(config.getLanguageConfig().getString(Language.prefix.name()) + config.getLanguageConfig().getString(lang.name()));
    }

    public static boolean exists(String value) {
        return getValue(value) != null;
    }

    private static Object getValue(String node) {
        return config.getProperty(node);
    }
}
