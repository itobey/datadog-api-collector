package dev.itobey.adapter.api.datadog.collector.adapter;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import dev.itobey.adapter.api.datadog.collector.domain.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
    public static final String UPTIME_IN_DAYS = "uptime_in_days";
    public static final String METRICS = "metrics";

    private final InfluxDBClient influxDBClient;

    @Value("${collector.influxdb.bucket}")
    private String bucket;

    @Value("${collector.influxdb.organization}")
    private String organization;

    /**
     * Adds metrics to InfluxDB using the given fields.
     *
     * @param metrics the metrics to add to the database
     */
    public void addToInfluxDb(Metrics metrics) {
        // Create a point for the data
        Point point = Point
                .measurement(METRICS)
                .time(Instant.now(), WritePrecision.NS)
                .addTag(HOST, metrics.getHostname().toString())
                .addField(CPU_USED_PERCENTAGE, metrics.getCpuUsedPercentage())
                .addField(RAM_USED_PERCENTAGE, metrics.getRamUsedPercentage())
                .addField(UPTIME_IN_DAYS, metrics.getUptimeInDays());

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            log.info("Writing sensor data: {}", point);
            writeApi.writePoint(bucket, organization, point);
        } catch (Exception e) {
            log.error("Error writing sensor data to InfluxDB", e);
        }
    }

    // Method to clean up resources when needed
    public void close() {
        influxDBClient.close();
    }

}
