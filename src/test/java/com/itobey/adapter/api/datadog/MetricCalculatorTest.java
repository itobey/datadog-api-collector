package com.itobey.adapter.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itobey.adapter.api.datadog.domain.Hostname;
import com.itobey.adapter.api.datadog.domain.Metrics;
import exception.DatadogAdapterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test of @{@link MetricCalculator}
 */
public class MetricCalculatorTest {

    ObjectMapper mapper = new ObjectMapper();
    MetricCalculator metricCalculator;

    {
        ConfigProperties props = new ConfigProperties();
        props.setNucMaxRam(15597);
        metricCalculator = new MetricCalculator(props);
    }

    MetricsQueryResponse validResponse;
    MetricsQueryResponse invalidResponse;

    @BeforeEach
    public void setup() throws IOException {
        validResponse = mapper
                .readValue(new File("src/test/resources/valid-response.json"), MetricsQueryResponse.class);
        invalidResponse = mapper
                .readValue(new File("src/test/resources/empty-pointlist.json"), MetricsQueryResponse.class);
    }

    @Test
    public void calculateMetricsForHost() throws DatadogAdapterException, IOException {
        MetricsQueryResponse responseNucMem = mapper
                .readValue(new File("src/test/resources/valid-response-nuc-mem.json"), MetricsQueryResponse.class);
        MetricsQueryResponse responseNucUptime = mapper
                .readValue(new File("src/test/resources/valid-response-nuc-uptime.json"), MetricsQueryResponse.class);

        Metrics metrics = metricCalculator.calculateMetricsForHost(
                validResponse, responseNucMem, responseNucUptime, Hostname.NUC);

        assertEquals(Hostname.NUC, metrics.getHostname());
        assertEquals(26, metrics.getCpuUsedPercentage());
        assertEquals(64, metrics.getRamUsedPercentage());
        assertEquals(15.825300925925925, metrics.getUptimeInDays());
    }

    @Test
    public void retrieveAverage_whenDataAvailable_shouldRetrieveLastValue() throws DatadogAdapterException {
        double average = metricCalculator.retrieveAverage(validResponse, Hostname.NUC);
        assertEquals(73.01856616445224, average);
    }

    @Test
    public void retrieveAverage_whenNoDatapoints_shouldThrowException() {
        assertThrows(DatadogAdapterException.class, () -> {
            metricCalculator.retrieveAverage(invalidResponse, Hostname.NUC);
        });
    }

    @Test
    public void retrieveLast_whenDataAvailable_shouldRetrieveLastValue() throws DatadogAdapterException {
        int lastValue = metricCalculator.retrieveLast(validResponse, Hostname.NUC);
        assertEquals(69, lastValue);
    }

    @Test
    public void retrieveLast_whenNoDatapoints_shouldThrowException() throws DatadogAdapterException {
        assertThrows(DatadogAdapterException.class, () -> {
            metricCalculator.retrieveLast(invalidResponse, Hostname.NUC);
        });
    }

}
