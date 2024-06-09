using Grpc.Core;
using TransflowAnalyzer.Api.Endpoints;
using TransflowAnalyzer.Sources.Entities;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Services
{
    public class VehicleService : Vehicles.VehiclesBase
    {
        private readonly IVehicleRepository _vehicleRepository;

        public VehicleService(IVehicleRepository vehicleRepository)
        {
            _vehicleRepository = vehicleRepository;
        }


        public override async Task<VehicleList> GetVehicles(SimulationId request, ServerCallContext context)
        {
            var vehicles = await _vehicleRepository.GetVehicles(request.Id);
            return new VehicleList { Vehicles = { vehicles }};
        }

        public override async Task<VehicleTypeList> GetVehicleTypes(SimulationId request, ServerCallContext context)
        {
            var vehicleTypes = await _vehicleRepository.GetVehicleTypes(request.Id);
            return new VehicleTypeList { VehicleTypes = { vehicleTypes }};
        }

        public override async Task<VehicleDataRange> GetVehicleDataFull(SingleEntity request, ServerCallContext context)
        {
            var vehicleData = await _vehicleRepository.GetVehicleData(request.SimulationId.Id, request.EntityId);
            return new VehicleDataRange { Points = { vehicleData }};
        }

        public override async Task<VehicleDataPoint> GetVehicleDataPoint(SingleEntityDataPoint request, ServerCallContext context)
        {
            return await _vehicleRepository.GetVehicleData(request.SimulationId.Id, request.EntityId, request.TimeStep);
        }

        public override async Task<VehicleDataRange> GetVehicleDataRange(SingleEntityDataRange request, ServerCallContext context)
        {
            var vehicleDataRange = await _vehicleRepository.GetVehicleData(request.SimulationId.Id, request.EntityId, request.From, request.To);
            return new VehicleDataRange { Points = { vehicleDataRange }};
        }

        public override async Task<VehicleTypeDataPoint> GetVehicleTypeData(SingleEntity request, ServerCallContext context)
        {
            return await _vehicleRepository.GetVehicleTypeData(request.SimulationId.Id, request.EntityId);
        }
    }
}
