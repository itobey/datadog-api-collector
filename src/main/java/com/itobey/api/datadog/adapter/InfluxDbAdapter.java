package com.itobey.api.datadog.adapter;

import com.itobey.api.datadog.domain.Metrics;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class InfluxDbAdapter {

    private final InfluxDB influxDB;

    public void addToInfluxDb(Metrics metrics) {

        // Write points to InfluxDB.
        influxDB.write(Point.measurement("metrics")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("host", metrics.getHostname().toString())
                .addField("cpu_used_percentage", metrics.getCpuUsedPercentage())
                .addField("ram_used_percentage",metrics.getRamUsedPercentage())
                .addField("uptime_in_seconds", metrics.getUptimeInSeconds())
                .build());

    }


}
