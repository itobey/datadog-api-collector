package com.itobey.api.datadog.adapter;

import com.itobey.api.datadog.domain.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Adapter to connect to InfluxDB.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InfluxDbAdapter {

    public static final String HOST = "host";
    public static final String CPU_USED_PERCENTAGE = "cpu_used_percentage";
    public static final String RAM_USED_PERCENTAGE = "ram_used_percentage";
    public static final String UPTIME_IN_SECONDS = "uptime_in_seconds";
    public static final String METRICS = "metrics";

    private final InfluxDB influxDB;

    /**
     * Adds metrics to InfluxDB using the given fields.
     *
     * @param metrics the metrics to add to the database
     */
    public void addToInfluxDb(Metrics metrics) {
        influxDB.write(Point.measurement(METRICS)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag(HOST, metrics.getHostname().toString())
                .addField(CPU_USED_PERCENTAGE, metrics.getCpuUsedPercentage())
                .addField(RAM_USED_PERCENTAGE, metrics.getRamUsedPercentage())
                .addField(UPTIME_IN_SECONDS, metrics.getUptimeInSeconds())
                .build());
        log.debug("written to influxdb");
    }


}
