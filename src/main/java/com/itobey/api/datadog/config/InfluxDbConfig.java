package com.itobey.api.datadog.config;

import com.itobey.api.datadog.ConfigProperties;
import io.micronaut.context.annotation.Bean;
import lombok.RequiredArgsConstructor;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InfluxDbConfig {

    private final ConfigProperties props;

    @Bean
    public InfluxDB createInfluxDb() {
        ConfigProperties.Influxdb influxdbConfig = props.getInfluxdb();
        final InfluxDB influxDB = InfluxDBFactory.connect(influxdbConfig.getServer(), influxdbConfig.getUser(), influxdbConfig.getPassword());

        String databaseName = props.getInfluxdb().getDatabase();
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        String retentionPolicyName = "one_day_only";
        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName
                + " ON " + databaseName + " DURATION 1d REPLICATION 1 DEFAULT"));
        influxDB.setRetentionPolicy(retentionPolicyName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        return influxDB;
    }
}