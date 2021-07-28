package com.itobey.adapter.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.adapter.api.datadog.domain.Hostname;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class handles all the calculations of the metrics gathered from Datadog.
 */
@Service
public class MetricCalculator {

    /**
     * Calculates the average of a specific type of metrics for a specific host.
     *
     * @param metricsQueryResponse the Datadog object containing the metrics
     * @param hostname             the host to which the metrics belong
     * @return the average of the values
     */
    public double calculateAverage(MetricsQueryResponse metricsQueryResponse, Hostname hostname) {
        Double sumOfDatapoints = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals("host:" + hostname.label)).findFirst().get().getPointlist()
                // get the second array entry
                .stream().map((x -> x.get(1)))
                .reduce(Double::sum).get();

        long amountOfDataPoints = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals("host:" + hostname.label)).findFirst().get().getPointlist().stream().count();

        return (sumOfDatapoints / amountOfDataPoints);
    }

    /**
     * Retrieves the last value of th metrics to a specific hostname.
     *
     * @param metricsQueryResponse the Datadog object containing the metrics
     * @param hostname             the host to which the metrics belong
     * @return the most recent value of the metrics
     */
    public int retrieveLast(MetricsQueryResponse metricsQueryResponse, Hostname hostname) {
        Optional<Double> lastElement = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals("host:" + hostname.label)).findFirst().get().getPointlist()
                // get the second array entry
                .stream().map((x -> x.get(1)))
                .reduce((first, second) -> second);

        return (int) lastElement.get().doubleValue();
    }

}
