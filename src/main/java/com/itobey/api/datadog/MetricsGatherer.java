package com.itobey.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.api.datadog.adapter.DatadogAdapter;
import com.itobey.api.datadog.domain.Hostname;
import com.itobey.api.datadog.domain.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class MetricsGatherer {

    private final DatadogAdapter datadogAdapter;
    private final MetricCalculator metricCalculator;

    public static final String SYSTEM_CPU_IDLE_BY_HOST = "system.cpu.idle{*}by{host}";
    public static final String SYSTEM_MEM_USABLE_BY_HOST = "system.mem.usable{*}by{host}";
    public static final String SYSTEM_UPTIME_BY_HOST = "system.uptime{*}by{host}";

    public static final int MAX_CPU = 100;
    public static final int MAX_RAM_NUC_MB = 15597;
    public static final int MAX_RAM_ITOBEY_MB = 7772;
    public static final int MAX_RAM_ODROID_MB = 3712;

    /**
     * Gather all necessary metrics.
     *
     * @return a list of the metrics sorted by hosts
     */
    public List<Metrics> gatherMetrics() {

        //TODO property of timeframe
        long unix_time_from = LocalTime.now().minusMinutes(30L).toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));
        long unix_time_to = LocalTime.now().toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));

        MetricsQueryResponse responseQueryCPU = datadogAdapter.queryMetrics(
                SYSTEM_CPU_IDLE_BY_HOST, unix_time_from, unix_time_to);
        int cpuNuc = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.NUC));
        int cpuItobey = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.ITOBEY));
        int cpuOdroid = (int) (MAX_CPU - metricCalculator.calculateAverage(responseQueryCPU, Hostname.ODROID));

        MetricsQueryResponse responseQueryRAM = datadogAdapter.queryMetrics(
                SYSTEM_MEM_USABLE_BY_HOST, unix_time_from, unix_time_to);

//        int mb = calcByteToMb(metricCalculator.calculateAverage(responseQueryRAM, Hostname.NUC));
//        int asd = mb / MAX_RAM_NUC_MB;
//        int ramNuc = (1 - (asd) / MAX_RAM_NUC_MB) * 100;

        int ramNuc = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.NUC), MAX_RAM_NUC_MB);
        int ramItobey = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.ITOBEY), MAX_RAM_ITOBEY_MB);
        int ramOdroid = calculateRamUsage(metricCalculator.calculateAverage(responseQueryRAM, Hostname.ODROID), MAX_RAM_ODROID_MB);

        MetricsQueryResponse responseQueryUptime = datadogAdapter.queryMetrics(
                SYSTEM_UPTIME_BY_HOST, unix_time_from, unix_time_to);
        long uptimeNuc = (long) metricCalculator.retrieveLast(responseQueryUptime, Hostname.NUC);
        long uptimeItobey = (long) metricCalculator.retrieveLast(responseQueryUptime, Hostname.ITOBEY);
        long uptimeOdroid = (long) metricCalculator.retrieveLast(responseQueryUptime, Hostname.ODROID);

        Metrics metricsNUC = Metrics.builder().hostname(Hostname.NUC).cpuUsedPercentage(cpuNuc).ramUsedPercentage(ramNuc).uptimeInSeconds(uptimeNuc).build();
        Metrics metricsItobey = Metrics.builder().hostname(Hostname.ITOBEY).cpuUsedPercentage(cpuItobey).ramUsedPercentage(ramItobey).uptimeInSeconds(uptimeItobey).build();
        Metrics metricsOdroid = Metrics.builder().hostname(Hostname.ODROID).cpuUsedPercentage(cpuOdroid).ramUsedPercentage(ramOdroid).uptimeInSeconds(uptimeOdroid).build();

        return List.of(metricsNUC, metricsItobey, metricsOdroid);
    }

    private int calculateRamUsage(double ramFreeInBytes, int maxRam) {
        double ramFreeInMb = calcByteToMb(ramFreeInBytes);
        double v = 100 - (ramFreeInMb / maxRam * 100);
        return (int) v;
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
