package com.itobey.adapter.api.datadog;

import com.datadog.api.v1.client.ApiException;
import com.datadog.api.v1.client.model.MetricsQueryResponse;
import com.itobey.adapter.api.datadog.adapter.DatadogAdapter;
import com.itobey.adapter.api.datadog.domain.Hostname;
import com.itobey.adapter.api.datadog.domain.Metrics;
import exception.DatadogAdapterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Gather the metrics from the Datadog API and format them.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Singleton
public class MetricsGatherer {

    private final DatadogAdapter datadogAdapter;
    private final MetricCalculator metricCalculator;
    private final ConfigProperties props;

    public static final String SYSTEM_CPU_IDLE_BY_HOST = "system.cpu.idle{*}by{host}";
    public static final String SYSTEM_MEM_USABLE_BY_HOST = "system.mem.usable{*}by{host}";
    public static final String SYSTEM_UPTIME_BY_HOST = "system.uptime{*}by{host}";

    /**
     * Gather all necessary metrics.
     *
     * @return a list of the metrics sorted by hosts
     */
    public List<Metrics> retrieveMetricsForHosts() throws ApiException {

        long unixTimeFrom = createUnixTimeStart();
        long unixTimeTo = createUnixTimeEnd();

        MetricsQueryResponse responseQueryCPU = datadogAdapter.queryMetrics(
                SYSTEM_CPU_IDLE_BY_HOST, unixTimeFrom, unixTimeTo);
        MetricsQueryResponse responseQueryRAM = datadogAdapter.queryMetrics(
                SYSTEM_MEM_USABLE_BY_HOST, unixTimeFrom, unixTimeTo);
        MetricsQueryResponse responseQueryUptime = datadogAdapter.queryMetrics(
                SYSTEM_UPTIME_BY_HOST, unixTimeFrom, unixTimeTo);

        List<Metrics> metricsList = new ArrayList<>();
        for (Hostname hostname : Hostname.values()) {
            try {
                metricsList.add(metricCalculator.calculateMetricsForHost(
                        responseQueryCPU, responseQueryRAM, responseQueryUptime, hostname));
            } catch (DatadogAdapterException e) {
                log.warn("metrics for host {} could not be retrieved, skipping this host entry combination", hostname);
            }
        }
        return metricsList;
    }

    private long createUnixTimeEnd() {
        return LocalTime.now().toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));
    }

    private long createUnixTimeStart() {
        return LocalTime.now().minusMinutes(props.getSearchWindow()).toEpochSecond(LocalDate.now(), ZoneOffset.ofHours(2));
    }

}
