using TransflowAnalyzer.Sources.Entities;
using TransflowConsumer.Shared.Protobuffs;

namespace TransflowAnalyzer.Api.Mapping
{
    public class PositionMapper : IConverter<PositionEntity?, PositionData>
    {
        public PositionMapper() { }

        public PositionData Convert(PositionEntity? entity)
        {
            if (entity == null)
                return new PositionData();

            return new PositionData()
            {
                X = entity.Value.X ?? 0,
                Y = entity.Value.Y ?? 0,
                Z = entity.Value.Z ?? 0,
            };
        }
    }
}
