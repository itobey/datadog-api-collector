package dev.itobey.adapter.api.datadog.collector;

import com.datadog.api.client.ApiException;
import dev.itobey.adapter.api.datadog.collector.adapter.InfluxDbAdapter;
import dev.itobey.adapter.api.datadog.collector.domain.Metrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Micronaut scheduler to periodically execute a job.
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class Scheduler {

    private final MetricsGatherer metricsGatherer;
    private final InfluxDbAdapter influxDbAdapter;

    /**
     * Runs the metrics gathering and reporting job every 10 minutes.
     */
    @Scheduled(fixedDelay = 600000) //every 10 minutes
    public void runMetricsGathering() {
        try {
            List<Metrics> metrics = metricsGatherer.retrieveMetricsForHosts();
            log.info(metrics.toString());
            metrics.stream().forEach(influxDbAdapter::addToInfluxDb);
        } catch (ApiException e) {
            log.error("exception when gathering metrics, retrying on the next run cycle");
        }
    }
}
