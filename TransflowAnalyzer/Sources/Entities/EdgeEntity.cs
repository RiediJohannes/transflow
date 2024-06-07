namespace TransflowAnalyzer.Sources.Entities
{
    public class EdgeEntity : TimeSeriesData
    {
        public int? LaneCount { get; set; }
        public string? StreetName { get; set; }
        public double? SumCo2MgPerSecond { get; set; }
        public double? SumCoMgPerSecond { get; set; }
        public double? SumHcMgPerSecond { get; set; }
        public double? SumPmxMgPerSecond { get; set; }
        public double? SumNoxMgPerSecond { get; set; }
        public double? SumFuelConsumption { get; set; }
        public double? SumElectricityConsumption { get; set; }
        public double? SumNoiseDba { get; set; }
        public int? VehicleCount { get; set; }
        public double? MeanVehicleSpeed { get; set; }
        public double? MeanVehicleLength { get; set; }
        public string[] VehicleIds { get; set; } = [];
        public string[] PersonIds { get; set; } = [];
        public double? TimeOccupancyPercentage { get; set; }
        public double? CurrentTravelDuration { get; set; }
        public int? HaltingVehiclesCount { get; set; }
        public double? SumWaitingTime { get; set; }
    }
}
