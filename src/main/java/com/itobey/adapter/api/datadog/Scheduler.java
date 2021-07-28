package com.itobey.adapter.api.datadog;

import com.datadog.api.v1.client.ApiException;
import com.itobey.adapter.api.datadog.adapter.InfluxDbAdapter;
import com.itobey.adapter.api.datadog.domain.Metrics;
import io.micronaut.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.List;

/**
 * Micronaut scheduler to periodically execute a job.
 */
@Singleton
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    private final MetricsGatherer metricsGatherer;
    private final InfluxDbAdapter influxDbAdapter;

    /**
     * Runs the metrics gathering and reporting job every 10 minutes.
     */
    @Scheduled(fixedDelay = "10m")
    public void runMetricsGathering() {
        try {
            List<Metrics> metrics = metricsGatherer.gatherMetrics();
            log.info(metrics.toString());
            metrics.stream().forEach(influxDbAdapter::addToInfluxDb);
        } catch (ApiException e) {
            log.error("exception when gathering metrics, retrying on the next run cycle");
        }
    }
}
