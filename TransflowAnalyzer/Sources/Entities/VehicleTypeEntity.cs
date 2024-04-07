namespace TransflowAnalyzer.Sources.Entities
{
    public class VehicleTypeEntity
    {
        public string Id { get; set; }
        public string VehicleClass { get; set; }
        public float Length { get; set; }
        public float Width { get; set; }
        public float Height { get; set; }
        public object[] Shape { get; set; }
        public string HexColor { get; set; }
        public float MaxSpeed { get; set; }
        public float MaxSpeedLateral { get; set; }
        public float MaxAcceleration { get; set; }
        public float MaxDeceleration { get; set; }
        public float SpeedFactor { get; set; }
        public float SpeedDeviation { get; set; }
        public string EmissionClass { get; set; }
        public float Scale { get; set; }
        public float Tau { get; set; }
        public float Sigma { get; set; }
        public float MinGap { get; set; }
        public float MinLateralGap { get; set; }
        public string LateralAlignment { get; set; }
        public float ActionStepLength { get; set; }
        public int PersonCapacity { get; set; }
        public float BoardingDuration { get; set; }
    }
}
