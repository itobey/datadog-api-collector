package com.itobey.adapter.api.datadog;

import com.datadog.api.v1.client.model.MetricsQueryMetadata;
import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.adapter.api.datadog.domain.Hostname;
import com.itobey.adapter.api.datadog.domain.Metrics;
import exception.DatadogAdapterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This class handles all the calculations of the metrics gathered from Datadog.
 * These calculations are to retrieve the average of the datapoints and the last (most recent) value of the list of datapoints.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MetricCalculator {

    private final ConfigProperties props;

    public static final String HOST_PREFIX = "host:";
    public static final int MAX_CPU = 100;

    /**
     * Calculates the metrics for a specific host.
     *
     * @param responseQueryCPU    the metrics containing all CPU data
     * @param responseQueryRAM    the metrics containing all RAM data
     * @param responseQueryUptime the metrics containing all uptime data
     * @param hostname            the hostname the metrics should be calculated for
     * @return the calculated metrics
     */
    public Metrics calculateMetricsForHost(MetricsQueryResponse responseQueryCPU,
                                           MetricsQueryResponse responseQueryRAM,
                                           MetricsQueryResponse responseQueryUptime,
                                           Hostname hostname) throws DatadogAdapterException {
        int maxRam = 0;
        switch (hostname) {
            case NUC:
                maxRam = props.getNucMaxRam();
                break;
            case ITOBEY:
                maxRam = props.getItobeyMaxRam();
                break;
            case ODROID:
                maxRam = props.getOdroidMaxRam();
                break;
            default:
                throw new IllegalArgumentException("hostname not recognized");
        }

        int cpu = (int) (MAX_CPU - retrieveAverage(responseQueryCPU, hostname));
        int ram = calculateRamUsage(retrieveAverage(responseQueryRAM, hostname), maxRam);
        double uptime = calculateSecondsToDays(retrieveLast(responseQueryUptime, hostname));

        return Metrics.builder()
                .hostname(hostname)
                .cpuUsedPercentage(cpu)
                .ramUsedPercentage(ram)
                .uptimeInDays(uptime)
                .build();
    }

    /**
     * Calculates the average of a specific type of metrics for a specific host.
     *
     * @param metricsQueryResponse the Datadog object containing the metrics
     * @param hostname             the host to which the metrics belong
     * @return the average of the values
     */
    protected double retrieveAverage(MetricsQueryResponse metricsQueryResponse, Hostname hostname) throws DatadogAdapterException {
        Optional<MetricsQueryMetadata> metricsQueryMetadata = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals(HOST_PREFIX + hostname.label)).findFirst();

        try {
            Double sumOfDatapoints = calculateSum(metricsQueryMetadata);
            long amountOfDataPoints = getAmountOfDataPoints(metricsQueryMetadata);
            return (sumOfDatapoints / amountOfDataPoints);
        } catch (DatadogAdapterException datadogAdapterException) {
            log.error("error calculating sum for host '{}'", hostname);
            throw datadogAdapterException;
        }
    }

    /**
     * Retrieves the last value of the metrics to a specific hostname.
     *
     * @param metricsQueryResponse the Datadog object containing the metrics
     * @param hostname             the host to which the metrics belong
     * @return the most recent value of the metrics
     */
    protected int retrieveLast(MetricsQueryResponse metricsQueryResponse, Hostname hostname) throws DatadogAdapterException {
        Optional<MetricsQueryMetadata> metricsQueryMetadata = metricsQueryResponse.getSeries().stream()
                .filter(x -> x.getScope().equals(HOST_PREFIX + hostname.label)).findFirst();
        try {
            return calculateLastValue(metricsQueryMetadata);
        } catch (DatadogAdapterException datadogAdapterException) {
            log.error("error retrieving last value for host '{}'", hostname);
            throw datadogAdapterException;
        }
    }

    private int calculateRamUsage(double ramFreeInBytes, int maxRam) {
        double ramFreeInMb = calcByteToMb(ramFreeInBytes);
        return (int) (100 - (ramFreeInMb / maxRam * 100));
    }

    private double calculateSecondsToDays(double seconds) {
        double days = seconds / 3600 / 24;
        return Math.pow(days, 1);
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

    private Double calculateSum(Optional<MetricsQueryMetadata> metricsQueryMetadata) throws DatadogAdapterException {
        if (metricsQueryMetadata.isPresent()) {
            Optional<Double> sumOptional = metricsQueryMetadata.get().getPointlist()
                    // get the second array entry
                    .stream().map((x -> x.get(1)))
                    .reduce(Double::sum);

            if (sumOptional.isPresent()) {
                return sumOptional.get();
            }
        }
        log.error("error calculating sum of metrics");
        throw new DatadogAdapterException("error calculating sum of metrics");
    }

    private long getAmountOfDataPoints(Optional<MetricsQueryMetadata> metricsQueryMetadata) throws DatadogAdapterException {
        if (metricsQueryMetadata.isPresent()) {
            return metricsQueryMetadata.get().getPointlist().stream().count();
        }
        log.error("error getting amount of metric datapoints");
        throw new DatadogAdapterException("error getting amount of metric datapoints");
    }

    private int calculateLastValue(Optional<MetricsQueryMetadata> metricsQueryMetadata) throws DatadogAdapterException {
        if (metricsQueryMetadata.isPresent()) {
            Optional<Double> lastValueOptional = metricsQueryMetadata.get().getPointlist()
                    .stream().map((x -> x.get(1)))
                    .reduce((first, second) -> second);

            if (lastValueOptional.isPresent()) {
                return (int) lastValueOptional.get().doubleValue();
            }
        }
        log.error("error calculating last value");
        throw new DatadogAdapterException("error calculating last value");
    }

}
