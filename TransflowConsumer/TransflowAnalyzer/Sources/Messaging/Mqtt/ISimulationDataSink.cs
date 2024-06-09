using TransflowAnalyzer.Analysis.Memory;

namespace TransflowAnalyzer.Sources.Messaging.Mqtt
{
    public interface ISimulationDataSink
    {
        TimeSeriesDatabase GetOrCreateDatabase(string simulationId);
    }
}
