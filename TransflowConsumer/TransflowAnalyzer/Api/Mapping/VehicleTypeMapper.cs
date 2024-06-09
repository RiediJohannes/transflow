using TransflowAnalyzer.Sources.Entities;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Mapping
{
    public class VehicleTypeMapper : IConverter<VehicleTypeEntity, VehicleTypeData>
    {
        public VehicleTypeMapper() { }

        public VehicleTypeData Convert(VehicleTypeEntity entity)
        {
            return new VehicleTypeData()
            {
                Height = entity.Height,
                Width = entity.Width,
                Length = entity.Length,
                MaxSpeed = entity.MaxSpeed,
                MaxAcceleration = entity.MaxAcceleration,
                MaxDeceleration = entity.MaxDeceleration,
                Seats = entity.PersonCapacity,
            };
        }
    }
}
