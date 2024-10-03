package dev.itobey.adapter.api.datadog.collector.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDbConfig {

    @Value("${collector.influxdb.url}")
    private String influxUrl;

    @Value("${collector.influxdb.token}")
    private String token;

    @Value("${collector.influxdb.bucket}")
    private String bucket;

    @Value("${collector.influxdb.organization}")
    private String organization;

    // Define a Bean for InfluxDBClient
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxUrl, token.toCharArray(), organization, bucket);
    }
}
