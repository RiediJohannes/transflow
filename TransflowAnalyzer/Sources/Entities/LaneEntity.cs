﻿namespace TransflowAnalyzer.Sources.Entities
{
    public class LaneEntity
    {
        public string Id { get; set; }
        public string EdgeId { get; set; }
        public object[] AllowedVehicleTypes { get; set; }
        public object[] DisallowedVehicleTypes { get; set; }
        public float Length { get; set; }
        public float Width { get; set; }
        public object[] Shape { get; set; }
        public float MaxSpeed { get; set; }
        public float SumCo2MgPerSecond { get; set; }
        public float SumCoMgPerSecond { get; set; }
        public float SumHcMgPerSecond { get; set; }
        public float SumPmxMgPerSecond { get; set; }
        public float SumNoxMgPerSecond { get; set; }
        public float SumFuelConsumption { get; set; }
        public float SumElectricityConsumption { get; set; }
        public float SumNoiseDba { get; set; }
        public int VehicleCount { get; set; }
        public object[] VehicleIds { get; set; }
        public float TimeOccupancyPercentage { get; set; }
        public float MeanVehicleSpeed { get; set; }
        public float MeanVehicleLength { get; set; }
        public float SumWaitingTime { get; set; }
        public float CurrentTravelDuration { get; set; }
        public int HaltingVehiclesCount { get; set; }
        public string[] LaneChangeAllowedLeft { get; set; }
        public string[] LaneChangeAllowedRight { get; set; }
        public LinkEntity[] Links { get; set; }
    }
}
