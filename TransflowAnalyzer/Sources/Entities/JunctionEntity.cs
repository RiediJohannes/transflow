namespace TransflowAnalyzer.Sources.Entities
{
    public class JunctionEntity : TimeSeriesData
    {
        public PositionEntity? Position { get; set; }
        public double[] Shape { get; set; } = [];
    }
}
