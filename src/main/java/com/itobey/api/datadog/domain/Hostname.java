package com.itobey.api.datadog.domain;

public enum Hostname {
    NUC("tobey-nuc"),
    ITOBEY("1a7f7b1c90b4"),
    ODROID("odroid");

    public final String label;

    Hostname(String label) {
        this.label = label;
    }
}
