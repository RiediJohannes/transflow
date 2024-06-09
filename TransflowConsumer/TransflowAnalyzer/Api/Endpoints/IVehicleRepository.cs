using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Endpoints
{
    public interface IVehicleRepository
    {
        Task<IEnumerable<VehicleKey>> GetVehicles(string simulationId);
        Task<IEnumerable<VehicleTypeKey>> GetVehicleTypes(string simulationId);
        Task<IEnumerable<VehicleData>> GetVehicleData(string simulationId, string vehicleId);
        Task<VehicleDataPoint> GetVehicleData(string simulationId, string vehicleId, long timeStep);
        Task<IEnumerable<VehicleData>> GetVehicleData(string simulationId, string vehicleId, long from, long to);
        Task<VehicleTypeDataPoint> GetVehicleTypeData(string simulationId, string vehicleTypeId);
    }
}
