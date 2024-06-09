using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Endpoints
{
    public interface ISimulationRepository
    {
        Task<IEnumerable<SimulationId>> GetSimulationIds();
    }
}
