package dev.itobey.adapter.api.datadog.collector.adapter;

import com.datadog.api.client.ApiClient;
import com.datadog.api.client.ApiException;
import com.datadog.api.client.v1.api.MetricsApi;
import com.datadog.api.client.v1.model.MetricsQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Query the Datadog API.
 */
@Slf4j
@Service
public class DatadogAdapter {

    /**
     * Query metrics to a specific timeframe.
     * https://docs.datadoghq.com/api/latest/metrics/#query-timeseries-points
     *
     * @param query the query
     * @param from  unix epoch from
     * @param to    unix epoch to
     * @return the query response
     */
    public MetricsQueryResponse queryMetrics(String query, long from, long to) throws ApiException {
        ApiClient defaultClient = ApiClient.getDefaultApiClient();

        MetricsApi apiInstance = new MetricsApi(defaultClient);
        try {
            MetricsQueryResponse result = apiInstance.queryMetrics(from, to, query);
            log.debug(result.toString());
            return result;
        } catch (ApiException e) {
            log.error("Exception when calling MetricsApi#queryMetrics");
            log.error("Status code: " + e.getCode());
            log.error("Reason: " + e.getResponseBody());
            log.error("Response headers: " + e.getResponseHeaders());
            throw e;
        }
    }
}
