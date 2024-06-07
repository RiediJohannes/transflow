namespace TransflowAnalyzer.Sources.Entities
{
    public class VehicleTypeEntity : TimeSeriesData
    {
        public string? VehicleClass { get; set; }
        public double? Length { get; set; }
        public double? Width { get; set; }
        public double? Height { get; set; }
        public double[] Shape { get; set; } = [];
        public string? HexColor { get; set; }
        public double? MaxSpeed { get; set; }
        public double? MaxSpeedLateral { get; set; }
        public double? MaxAcceleration { get; set; }
        public double? MaxDeceleration { get; set; }
        public double SpeedFactor { get; set; }
        public double? SpeedDeviation { get; set; }
        public string? EmissionClass { get; set; }
        public double? Scale { get; set; }
        public double? Tau { get; set; }
        public double? Sigma { get; set; }
        public double? MinGap { get; set; }
        public double? MinLateralGap { get; set; }
        public string? LateralAlignment { get; set; }
        public double? ActionStepLength { get; set; }
        public int? PersonCapacity { get; set; }
        public double? BoardingDuration { get; set; }
    }
}
