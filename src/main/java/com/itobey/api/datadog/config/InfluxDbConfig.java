package com.itobey.api.datadog.config;

import com.itobey.api.datadog.domain.Metrics;
import io.micronaut.context.annotation.Bean;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;

public class InfluxDbConfig {

    @Bean
    public InfluxDB createInfluxDb() {
        // Create an object to handle the communication with InfluxDB.
// (best practice tip: reuse the 'influxDB' instance when possible)
        final String serverURL = "http://192.168.0.34:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        // Create a database...
// https://docs.influxdata.com/influxdb/v1.7/query_language/database_management/
        String databaseName = "datadog_metrics";
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

// ... and a retention policy, if necessary.
// https://docs.influxdata.com/influxdb/v1.7/query_language/database_management/
        String retentionPolicyName = "one_day_only";
        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName
                + " ON " + databaseName + " DURATION 1d REPLICATION 1 DEFAULT"));
        influxDB.setRetentionPolicy(retentionPolicyName);

// Enable batch writes to get better performance.
        influxDB.enableBatch(BatchOptions.DEFAULTS);

        return influxDB;
    }
}