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

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricsGatherer {

    private final DatadogAdapter datadogAdapter;
    private final MetricCalculator metricCalculator;

    public final static int MAX_CPU = 100;

    public List<Metrics> gatherMetrics() {

        //TODO property of timeframe
        long unix_time_from = LocalTime.now().minusMinutes(30L).toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));
        long unix_time_to = LocalTime.now().toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));

        MetricsQueryResponse metricsQueryResponse = datadogAdapter.queryMetrics(
                "system.cpu.idle{*}by{host}", unix_time_from, unix_time_to);

        double cpuNuc = MAX_CPU - metricCalculator.calculateAverage(metricsQueryResponse, Hostname.NUC);
        double cpuItobey = MAX_CPU - metricCalculator.calculateAverage(metricsQueryResponse, Hostname.ITOBEY);
        double cpuOdroid = MAX_CPU - metricCalculator.calculateAverage(metricsQueryResponse, Hostname.ODROID);


        Metrics metricsNUC = Metrics.builder().hostname(Hostname.NUC).cpu(cpuNuc).ram(1).uptime(1).build();
        Metrics metricsItobey = Metrics.builder().hostname(Hostname.ITOBEY).cpu(cpuItobey).ram(1).uptime(1).build();
        Metrics metricsOdroid = Metrics.builder().hostname(Hostname.ODROID).cpu(cpuOdroid).ram(1).uptime(1).build();
        return List.of(metricsNUC, metricsItobey, metricsOdroid);
    }

}
