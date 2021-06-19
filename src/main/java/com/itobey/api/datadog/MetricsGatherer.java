package com.itobey.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.api.datadog.adapter.DatadogAdapter;
import com.itobey.api.datadog.domain.Hostname;
import com.itobey.api.datadog.domain.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Gather the metrics from the Datadog API and format them.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Singleton
public class MetricsGatherer {

    private final DatadogAdapter datadogAdapter;
    private final MetricCalculator metricCalculator;
    private final ConfigProperties props;

    public static final String SYSTEM_CPU_IDLE_BY_HOST = "system.cpu.idle{*}by{host}";
    public static final String SYSTEM_MEM_USABLE_BY_HOST = "system.mem.usable{*}by{host}";
    public static final String SYSTEM_UPTIME_BY_HOST = "system.uptime{*}by{host}";

    public static final int MAX_CPU = 100;

    /**
     * Gather all necessary metrics.
     *
     * @return a list of the metrics sorted by hosts
     */
    public List<Metrics> gatherMetrics() {

        long unix_time_from = LocalTime.now().minusMinutes(props.getSearchWindow()).toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));
        long unix_time_to = LocalTime.now().toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));

        MetricsQueryResponse responseQueryCPU = datadogAdapter.queryMetrics(
                SYSTEM_CPU_IDLE_BY_HOST, unix_time_from, unix_time_to);
        int cpuNuc = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.NUC));
        int cpuItobey = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.ITOBEY));
        int cpuOdroid = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.ODROID));

        MetricsQueryResponse responseQueryRAM = datadogAdapter.queryMetrics(
                SYSTEM_MEM_USABLE_BY_HOST, unix_time_from, unix_time_to);

        int ramNuc = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.NUC), props.getNucMaxRam());
        int ramItobey = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.ITOBEY), props.getItobeyMaxRam());
        int ramOdroid = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.ODROID), props.getOdroidMaxRam());

        MetricsQueryResponse responseQueryUptime = datadogAdapter.queryMetrics(
                SYSTEM_UPTIME_BY_HOST, unix_time_from, unix_time_to);
        double uptimeNuc = calculateSecondsToDays(metricCalculator.retrieveLast(responseQueryUptime, Hostname.NUC));
        double uptimeItobey = calculateSecondsToDays(metricCalculator.retrieveLast(responseQueryUptime, Hostname.ITOBEY));
        double uptimeOdroid = calculateSecondsToDays(metricCalculator.retrieveLast(responseQueryUptime, Hostname.ODROID));

        Metrics metricsNUC = Metrics.builder().hostname(Hostname.NUC).cpuUsedPercentage(cpuNuc).ramUsedPercentage(ramNuc).uptimeInSeconds(uptimeNuc).build();
        Metrics metricsItobey = Metrics.builder().hostname(Hostname.ITOBEY).cpuUsedPercentage(cpuItobey).ramUsedPercentage(ramItobey).uptimeInSeconds(uptimeItobey).build();
        Metrics metricsOdroid = Metrics.builder().hostname(Hostname.ODROID).cpuUsedPercentage(cpuOdroid).ramUsedPercentage(ramOdroid).uptimeInSeconds(uptimeOdroid).build();

        return List.of(metricsNUC, metricsItobey, metricsOdroid);
    }

    private int calculateRamUsage(double ramFreeInBytes, int maxRam) {
        double ramFreeInMb = calcByteToMb(ramFreeInBytes);
        return (int) (100 - (ramFreeInMb / maxRam * 100));
    }

    private double calculateSecondsToDays(double seconds) {
        double days = seconds / 3600 / 24;
        return Math.pow(days, 1);
    }

    /**
     * Format Bytes to Megabytes.
     *
     * @param usage the current byte value
     * @return the value in MB
     */
    private double calcByteToMb(double usage) {
        return (usage / 1024 / 1024);
    }


}
