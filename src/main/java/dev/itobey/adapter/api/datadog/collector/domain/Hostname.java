package dev.itobey.adapter.api.datadog.collector.domain;

/**
 * Contains the hostnames which are available by the Datadog Api.
 */
public enum Hostname {
    NUC("tobey-nuc"),
    ITOBEY("itobey"),
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
