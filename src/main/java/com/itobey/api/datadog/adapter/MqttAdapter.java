package com.itobey.api.datadog.adapter;

import com.itobey.api.datadog.domain.Metrics;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
public class MqttAdapter {

    public void sendMessage(Metrics metrics) throws MqttException {

        String message = String.format("%s cpu=%s,ram=%s,uptime=%s",
                metrics.getHostname().toString(), metrics.getCpuUsedPercentage(), metrics.getRamUsedPercentage(), metrics.getUptimeInSeconds());

        //TODO property
        MqttClient client = new MqttClient(
                "tcp://192.168.0.34", "datadog-api-gatherer");

        client.connect();

        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        client.publish("metrics/datadog", mqttMessage);

        client.disconnect();
    }

}
