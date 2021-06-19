package com.itobey.api.datadog;

import com.itobey.api.datadog.adapter.InfluxDbAdapter;
import com.itobey.api.datadog.domain.Metrics;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.List;

@Singleton
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final MetricsGatherer metricsGatherer;
    private final InfluxDbAdapter influxDbAdapter;

    @Scheduled(fixedDelay = "10m")
    public void runMetricsGathering() {
        List<Metrics> metrics = metricsGatherer.gatherMetrics();
        log.info(metrics.toString());
        metrics.stream().forEach(influxDbAdapter::addToInfluxDb);
    }
}
