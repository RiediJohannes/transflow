namespace TransflowAnalyzer.Sources.Messaging.Mqtt
{
    public record MqttParameters
    (
        string ClientId,
        string BrokerUrl,
        string RootTopic,
        string MetricsTopic,
        string[] Subscriptions
    )
    { }
}
