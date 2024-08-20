package dev.itobey.adapter.api.datadog.collector.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The domain object for metrics gathered by the Datadog API to be written to InfluxDB.
 */
@Setter
@Getter
@Builder
@ToString
public class Metrics {

    private Hostname hostname;
    private int cpuUsedPercentage;
    private int ramUsedPercentage;
    private double uptimeInDays;

}
