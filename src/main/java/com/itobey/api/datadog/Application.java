package com.itobey.api.datadog;

import com.itobey.api.datadog.adapter.DatadogAdapter;
import com.itobey.api.datadog.adapter.InfluxDbAdapter;
import com.itobey.api.datadog.config.InfluxDbConfig;
import com.itobey.api.datadog.domain.Metrics;
import io.micronaut.runtime.Micronaut;

import java.util.List;

/**
 * The entrypoint.
 */
public class Application {

    /**
     * The entrypoint for Micronaut.
     *
     * @param args
     */
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);

        MetricsGatherer metricsGatherer = new MetricsGatherer(new DatadogAdapter(), new MetricCalculator());
        List<Metrics> metrics = metricsGatherer.gatherMetrics();
        System.out.println(metrics);

        InfluxDbAdapter influxDbAdapter = new InfluxDbAdapter(new InfluxDbConfig().createInfluxDb());

        metrics.stream().forEach(influxDbAdapter::addToInfluxDb);
    }


}
