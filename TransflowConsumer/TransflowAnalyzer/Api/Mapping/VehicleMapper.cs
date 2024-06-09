using TransflowAnalyzer.Sources.Entities;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Mapping
{
    public class VehicleMapper : IConverter<VehicleEntity, VehicleData>
    {
        private readonly IConverter<PositionEntity?, PositionData> _positionMapper;

        public VehicleMapper(IConverter<PositionEntity?, PositionData> positionMapper)
        {
            _positionMapper = positionMapper;
        }


        public VehicleData Convert(VehicleEntity entity)
        {
            return new VehicleData()
            {
                TimeStep = entity.TimeStep,
                Speed = entity.Speed,
                Acceleration = entity.Acceleration,
                FuelConsumption = entity.FuelConsumption,
                Position = _positionMapper.Convert(entity.Position),
            };
        }
    }
}
