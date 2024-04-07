namespace TransflowAnalyzer.Sources.Entities
{
    public class VehicleEntity
    {
        public string Id { get; set; } = string.Empty;
        public string? HexColor { get; set; }
        public string? VehicleTypeId { get; set; }
        public double? Length { get; set; }
        public double? Width { get; set; }
        public double? Height { get; set; }
        public int? PersonCapacity { get; set; }
        public double? MaxSpeed { get; set; }
        public double? MaxAcceleration { get; set; }
        public double? MaxDeceleration { get; set; }
        public double? SpeedFactor { get; set; }
        public double? SpeedDeviation { get; set; }
        public string? ShapeClass { get; set; }
        public double? Tau { get; set; }
        public double? Sigma { get; set; }
        public string? RouteId { get; set; }
        public double? MinFrontGap { get; set; }
        public double? MinLateralGap { get; set; }
        public double? Speed { get; set; }
        public double? Acceleration { get; set; }
        public double? LateralSpeed { get; set; }
        public double? AllowedSpeed { get; set; }
        public double? Angle { get; set; }
        public string? RoadId { get; set; }
        public int? EdgeInRoute { get; set; }
        public int? Lane { get; set; }
        public PositionEntity? Position { get; set; }
        public double? PositionOnLane { get; set; }
        public int? LaneChangeState { get; set; }
        public int? SignalState { get; set; }
        public int StopState { get; set; }
        public string[] PersonIds { get; set; } = [];
        public double? CO2mgPerSecond { get; set; }
        public double? HCmgPerSecond { get; set; }
        public double? PMXmgPerSecond { get; set; }
        public double? NOXmgPerSecond { get; set; }
        public double? FuelConsumption { get; set; }
        public double? ElectricityConsumption { get; set; }
        public double? NoiseDBA { get; set; }
        public double? TotalDistance { get; set; }
        public double? TotalWaitingTime { get; set; }
        public double? TotalTimeLoss { get; set; }
        public double? BoardingDuration { get; set; }
        public string? LeaderVehicleId { get; set; }
        public double? LeaderVehicleDistance { get; set; }
    }
}
