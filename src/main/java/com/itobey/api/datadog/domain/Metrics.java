package com.itobey.api.datadog.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
public class Metrics {

    private Hostname hostname;
    private Double cpu;
    private int ram;
    private int uptime;

}
