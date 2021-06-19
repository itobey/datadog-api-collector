package com.itobey.adapter.api.datadog.domain;

/**
 * Contains the hostnames which are available by the Datadog Api.
 */
public enum Hostname {
    NUC("tobey-nuc"),
    ITOBEY("1a7f7b1c90b4"),
    ODROID("odroid");

    public final String label;

    /**
     * Retrieves the label to a hostname.
     *
     * @param label the label
     */
    Hostname(String label) {
        this.label = label;
    }
}
