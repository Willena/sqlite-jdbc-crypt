package org.sqlite;

import java.util.Arrays;
import java.util.Properties;
import org.sqlite.mc.SQLiteMCConfig;

public enum SQLiteConfigFactory {
    DEFAULT(SQLiteConfig.class.getName()) {
        @Override
        public SQLiteConfig fromProperties(Properties properties) {
            return new SQLiteConfig(properties);
        }
    },
    SQLITE_MC_CONFIG(SQLiteMCConfig.class.getName()) {
        @Override
        public SQLiteConfig fromProperties(Properties properties) {
            return new SQLiteMCConfig.Builder(properties).build();
        }
    };

    public static final String CONFIG_CLASS_NAME = "config_class_name";
    private final String name;

    SQLiteConfigFactory(String className) {
        this.name = className;
    }

    protected abstract SQLiteConfig fromProperties(Properties properties);

    public static SQLiteConfig getFromProperties(Properties properties) {

        // Quick test to find if it is the default config or MC config
        if (properties.containsKey(SQLiteConfig.Pragma.KEY.getPragmaName())) {
            return SQLITE_MC_CONFIG.fromProperties(properties);
        }

        return Arrays.stream(SQLiteConfigFactory.class.getEnumConstants())
                .filter(cf -> cf.name.equals(properties.getProperty(CONFIG_CLASS_NAME)))
                .findFirst()
                .orElse(DEFAULT)
                .fromProperties(properties);
    }
}
