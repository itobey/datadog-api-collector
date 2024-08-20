package dev.itobey.adapter.api.datadog.collector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * properties for configuration
 */
@ConfigurationProperties("collector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigProperties {

    private int nucMaxRam;
    private int itobeyMaxRam;
    private int odroidMaxRam;
    private long searchWindow;
    private Influxdb influxdb;

    /**
     * InfluxDB properties
     */
    @Getter
    @Setter
    public static class Influxdb {
        private String server;
        private String user;
        private String password;
        private String database;
    }

}
