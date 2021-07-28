package com.itobey.api.datadog;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * properties for configuration
 */
@ConfigurationProperties("datadog-api-gatherer")
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
    @ConfigurationProperties("influxdb")
    public static class Influxdb {
        private String server;
        private String user;
        private String password;
        private String database;
    }

}
