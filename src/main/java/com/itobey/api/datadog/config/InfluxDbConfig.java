package com.itobey.api.datadog.config;

import com.itobey.api.datadog.ConfigProperties;
import io.micronaut.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration class for InfluxDB.
 */
@Configuration
@RequiredArgsConstructor
public class InfluxDbConfig {

    public static final String CREATE_DATABASE = "CREATE DATABASE ";
    public static final String ONE_DAY_ONLY = "one_day_only";
    public static final String CREATE_RETENTION_POLICY = "CREATE RETENTION POLICY ";
    public static final String ON = " ON ";
    public static final String DURATION_1_D_REPLICATION_1_DEFAULT = " DURATION 1d REPLICATION 1 DEFAULT";

    private final ConfigProperties props;

    /**
     * Creates the bean for InfluxDB to be used by the @{@link com.itobey.api.datadog.adapter.InfluxDbAdapter}
     *
     * @return the InfluxDB bean
     */
    @Bean
    public InfluxDB createInfluxDb() {
        ConfigProperties.Influxdb influxdbConfig = props.getInfluxdb();
        final InfluxDB influxDB = InfluxDBFactory.connect(influxdbConfig.getServer(),
                influxdbConfig.getUser(), influxdbConfig.getPassword());

        String databaseName = props.getInfluxdb().getDatabase();
        influxDB.query(new Query(CREATE_DATABASE + databaseName));
        influxDB.setDatabase(databaseName);

        String retentionPolicyName = ONE_DAY_ONLY;
        influxDB.query(new Query(CREATE_RETENTION_POLICY + retentionPolicyName
                + ON + databaseName + DURATION_1_D_REPLICATION_1_DEFAULT));
        influxDB.setRetentionPolicy(retentionPolicyName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        return influxDB;
    }
}