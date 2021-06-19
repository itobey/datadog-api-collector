package com.itobey.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.api.datadog.domain.Hostname;
import org.springframework.stereotype.Service;

@Service
public class MetricCalculator {

    public double calculateAverage(MetricsQueryResponse metricsQueryResponse, Hostname hostname) {
        Double sumOfDatapoints = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals("host:" + hostname.label)).findFirst().get().getPointlist()
                .stream().map((x -> x.get(1)))
                .reduce(Double::sum).get();

        long amountOfDataPoints = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals("host:" + hostname.label)).findFirst().get().getPointlist().stream().count();

        return sumOfDatapoints / amountOfDataPoints;
    }

}
