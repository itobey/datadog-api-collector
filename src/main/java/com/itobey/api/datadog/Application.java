package com.itobey.api.datadog;

import com.itobey.api.datadog.adapter.DatadogAdapter;
import com.itobey.api.datadog.adapter.MqttAdapter;
import com.itobey.api.datadog.domain.Metrics;
import io.micronaut.runtime.Micronaut;
import org.eclipse.paho.client.mqttv3.MqttException;

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

        MqttAdapter mqttAdapter = new MqttAdapter();
        try {
            mqttAdapter.sendMessage(metrics.get(0));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


}
