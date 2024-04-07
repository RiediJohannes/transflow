namespace TransflowAnalyzer.Sources.Entities
{
    public class EdgeEntity
    {
        public string Id { get; set; }
        public int LaneCount { get; set; }
        public string StreetName { get; set; }
        public float SumCo2MgPerSecond { get; set; }
        public float SumCoMgPerSecond { get; set; }
        public float SumHcMgPerSecond { get; set; }
        public float SumPmxMgPerSecond { get; set; }
        public float SumNoxMgPerSecond { get; set; }
        public float SumFuelConsumption { get; set; }
        public float SumElectricityConsumption { get; set; }
        public float SumNoiseDba { get; set; }
        public int VehicleCount { get; set; }
        public float MeanVehicleSpeed { get; set; }
        public float MeanVehicleLength { get; set; }
        public object[] VehicleIds { get; set; }
        public object[] PersonIds { get; set; }
        public float TimeOccupancyPercentage { get; set; }
        public float CurrentTravelDuration { get; set; }
        public int HaltingVehiclesCount { get; set; }
        public float SumWaitingTime { get; set; }
    }
}
