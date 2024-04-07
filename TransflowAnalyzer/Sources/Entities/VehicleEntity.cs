namespace TransflowAnalyzer.Sources.Entities
{
    public class VehicleEntity
    {
        public string Id { get; set; }
        public string HexColor { get; set; }
        public string VehicleTypeId { get; set; }
        public float Length { get; set; }
        public float Width { get; set; }
        public float Height { get; set; }
        public int PersonCapacity { get; set; }
        public float MaxSpeed { get; set; }
        public float MaxAcceleration { get; set; }
        public float MaxDeceleration { get; set; }
        public float SpeedFactor { get; set; }
        public float SpeedDeviation { get; set; }
        public string ShapeClass { get; set; }
        public float Tau { get; set; }
        public float Sigma { get; set; }
        public string RouteId { get; set; }
        public float MinFrontGap { get; set; }
        public float MinLateralGap { get; set; }
        public float Speed { get; set; }
        public float Acceleration { get; set; }
        public float LateralSpeed { get; set; }
        public float AllowedSpeed { get; set; }
        public float Angle { get; set; }
        public string RoadId { get; set; }
        public int EdgeInRoute { get; set; }
        public int Lane { get; set; }
        public PositionEntity Position { get; set; }
        public float PositionOnLane { get; set; }
        public int LaneChangeState { get; set; }
        public int SignalState { get; set; }
        public int StopState { get; set; }
        public object[] PersonIds { get; set; }
        public float CO2mgPerSecond { get; set; }
        public float HCmgPerSecond { get; set; }
        public float PMXmgPerSecond { get; set; }
        public float NOXmgPerSecond { get; set; }
        public float FuelConsumption { get; set; }
        public float ElectricityConsumption { get; set; }
        public float NoiseDBA { get; set; }
        public float TotalDistance { get; set; }
        public float TotalWaitingTime { get; set; }
        public float TotalTimeLoss { get; set; }
        public float BoardingDuration { get; set; }
        public object LeaderVehicleId { get; set; }
        public object LeaderVehicleDistance { get; set; }
    }
}
