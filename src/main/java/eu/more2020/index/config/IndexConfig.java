package eu.more2020.index.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

public final class IndexConfig {

    private static final PropertiesConfiguration PROPERTIES_CONFIG = loadPropertiesConfig();


    public static char DELIMITER = PROPERTIES_CONFIG.getString("DELIMITER", ",").charAt(0);
    public static String FORMAT = PROPERTIES_CONFIG.getString("FORMAT", "dd-MM-YYYY");
    public static double FILTER_SCORE = PROPERTIES_CONFIG.getDouble("FILTER_SCORE", 0.5d);
    public static double GROUP_BY_SCORE = PROPERTIES_CONFIG.getDouble("GROUP_BY_SCORE", 0.4d);
    public static double DEFAULT_SCORE = PROPERTIES_CONFIG.getDouble("DEFAULT_SCORE", 0.1d);


    private static PropertiesConfiguration loadPropertiesConfig() {
        try {
            return new PropertiesConfiguration("config.properties");
        } catch (ConfigurationException e) {
            throw new ConfigurationRuntimeException(e);
        }
    }

}