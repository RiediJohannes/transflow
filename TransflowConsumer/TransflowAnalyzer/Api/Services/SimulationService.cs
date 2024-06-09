using Google.Protobuf.WellKnownTypes;
using Grpc.Core;
using TransflowAnalyzer.Api.Endpoints;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Services
{
    public class SimulationService : Simulations.SimulationsBase
    {
        private readonly ISimulationRepository _simulationRepository;

        public SimulationService(ISimulationRepository simulationRepository)
        {
            _simulationRepository = simulationRepository;
        }

        public override async Task<SimulationResponse> GetSimulations(Empty request, ServerCallContext context)
        {
            var simulationIds = await _simulationRepository.GetSimulationIds();

            var response = new SimulationResponse();
            response.SimulationsIds.AddRange(simulationIds);

            return response;
        }
    }
}
