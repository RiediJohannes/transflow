using Sprache;
using TransflowAnalyzer.Api.Endpoints;
using TransflowAnalyzer.Api.Mapping;
using TransflowAnalyzer.Sources.Entities;
using TransflowAnalyzer.Sources.Messaging.Mqtt;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Analysis.Memory
{
    public class SimulationStorage : ISimulationDataSink, ISimulationRepository, IVehicleRepository
    {
        private readonly Dictionary<string, TimeSeriesDatabase> _simulationDatabases = [];

        private readonly IConverter<VehicleEntity, VehicleData> _vehicleMapper;
        private readonly IConverter<VehicleTypeEntity, VehicleTypeData> _vehicleTypeMapper;

        public SimulationStorage(
            IConverter<VehicleEntity, VehicleData> vehicleMapper,
            IConverter<VehicleTypeEntity, VehicleTypeData> vehicleTypeMapper)
        {
            _vehicleMapper = vehicleMapper;
            _vehicleTypeMapper = vehicleTypeMapper;
        }


        public TimeSeriesDatabase GetOrCreateDatabase(string simulationId)
        {
            if (_simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database))
            {
                return database;
            }
            else
            {
                database = new TimeSeriesDatabase();
                _simulationDatabases.Add(simulationId, database);

                return database;
            }
        }


        public Task<IEnumerable<SimulationId>> GetSimulationIds()
        {
            var mappedSimulationsIds = _simulationDatabases.Keys.Select(id => new SimulationId() { Id = id });
            return Task.FromResult(mappedSimulationsIds);
        }

        public Task<IEnumerable<VehicleKey>> GetVehicles(string simulationId)
        {
            var result = _simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database)
                ? database
                    .GetFirstOfEach<VehicleEntity>()
                    .Select(veh => new VehicleKey { Id = veh.Id, VehicleTypeId = veh.VehicleTypeId })
                : [];
            
            return Task.FromResult(result);
        }

        public Task<IEnumerable<VehicleTypeKey>> GetVehicleTypes(string simulationId)
        {
            var result = _simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database)
                ? database
                    .Keys<VehicleTypeEntity>()
                    .Select(id => new VehicleTypeKey { Id = id })
                : [];

            return Task.FromResult(result);
        }


        public Task<IEnumerable<VehicleData>> GetVehicleData(string simulationId, string vehicleId)
        {
            var result = _simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database)
                ? database
                    .GetSeries<VehicleEntity>(vehicleId)
                    .Select(_vehicleMapper.Convert)
                : [];

            return Task.FromResult(result);
        }

        public Task<VehicleDataPoint> GetVehicleData(string simulationId, string vehicleId, long timeStep)
        {
            var message = new VehicleDataPoint();
            if (_simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database))
            {
                var entity = database.GetPoint<VehicleEntity>(vehicleId, timeStep);
                if (entity is not null)
                {
                    message.Data = _vehicleMapper.Convert(entity);
                }
            }

            return Task.FromResult(message);
        }

        public Task<IEnumerable<VehicleData>> GetVehicleData(string simulationId, string vehicleId, long from, long to)
        {
            var result = _simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database)
                ? database
                    .GetRange<VehicleEntity>(vehicleId, from, to)
                    .Select(_vehicleMapper.Convert)
                : [];

            return Task.FromResult(result);
        }

        public Task<VehicleTypeDataPoint> GetVehicleTypeData(string simulationId, string vehicleTypeId)
        {
            var message = new VehicleTypeDataPoint();
            if (_simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database))
            {
                message.Data = database
                    .GetSeries<VehicleTypeEntity>(vehicleTypeId)
                    .Select(_vehicleTypeMapper.Convert)
                    .FirstOrDefault();
            }

            return Task.FromResult(message);
        }
    }
}
