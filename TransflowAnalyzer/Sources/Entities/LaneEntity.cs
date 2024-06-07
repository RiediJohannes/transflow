namespace TransflowAnalyzer.Sources.Entities
{
    public class LaneEntity : TimeSeriesData
    {
        public string? EdgeId { get; set; }
        public string[] AllowedVehicleTypes { get; set; } = [];
        public string[] DisallowedVehicleTypes { get; set; } = [];
        public double? Length { get; set; }
        public double? Width { get; set; }
        public double[] Shape { get; set; } = [];
        public double? MaxSpeed { get; set; }
        public double? SumCo2MgPerSecond { get; set; }
        public double? SumCoMgPerSecond { get; set; }
        public double? SumHcMgPerSecond { get; set; }
        public double? SumPmxMgPerSecond { get; set; }
        public double? SumNoxMgPerSecond { get; set; }
        public double? SumFuelConsumption { get; set; }
        public double? SumElectricityConsumption { get; set; }
        public double? SumNoiseDba { get; set; }
        public int? VehicleCount { get; set; }
        public string[] VehicleIds { get; set; } = [];
        public double? TimeOccupancyPercentage { get; set; }
        public double? MeanVehicleSpeed { get; set; }
        public double? MeanVehicleLength { get; set; }
        public double? SumWaitingTime { get; set; }
        public double? CurrentTravelDuration { get; set; }
        public int? HaltingVehiclesCount { get; set; }
        public string[] LaneChangeAllowedLeft { get; set; } = [];
        public string[] LaneChangeAllowedRight { get; set; } = [];
        public LinkEntity[] Links { get; set; } = [];
    }
}
