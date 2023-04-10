# Datadog Api Collector

More information on this tool can be found in my [blog post](https://itobey.dev/datadog-as-an-intermediary-for-gathering-metrics/).

# About

The Datadog agent reports your systems metrics to Datadog and the official API permits retrieval of point-data which is
a list of a whole lot of entries for a specific time frame. This is a little tool which allows to retrieve an average of
some metrics for specific hosts from Datadog. Because this is tightly scoped on my use case, it may not be useful for
you. However you can always check and modify my code to suit your use case. This little project doubled as a means of
having a look into [Micronaut](https://micronaut.io/).

# How does it work?

This tool uses the official REST API from Datadog to retrieve data. The data is then scoped to a specific host (I have 3
hosts working in this manner) and an average is calculated for the metrics. This newly crafted data is then persisted to
InfluxDB.

Currently I use it to retrieve these metrics:

- CPU usage
- RAM usage
- uptime

The resulting data in InfluxDB is used to display data on my Inkplate Dashboard.
