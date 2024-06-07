using System.Text.Json.Serialization;
using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Sources.Messaging
{

    [JsonSerializable(typeof(VehicleEntity))]
    [JsonSerializable(typeof(VehicleTypeEntity))]
    [JsonSerializable(typeof(LaneEntity))]
    [JsonSerializable(typeof(EdgeEntity))]
    [JsonSerializable(typeof(JunctionEntity))]
    [JsonSerializable(typeof(RouteEntity))]
    public partial class SimDataJsonContext : JsonSerializerContext
    {
    }
}
