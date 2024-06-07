namespace TransflowAnalyzer.Sources.Messaging
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
