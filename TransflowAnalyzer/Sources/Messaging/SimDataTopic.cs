namespace TransflowAnalyzer.Sources.Messaging
{
    public record SimDataTopic
    (
        string SimulationId,
        Domain Domain,
        long Time
    )
    { }
}
