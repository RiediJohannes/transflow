namespace TransflowAnalyzer.Sources.Messaging.Json
{
    public record SimDataTopic
    (
        string SimulationId,
        Domain Domain,
        long Time
    )
    { }
}
