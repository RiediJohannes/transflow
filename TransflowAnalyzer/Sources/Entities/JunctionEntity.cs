namespace TransflowAnalyzer.Sources.Entities
{
    public class JunctionEntity
    {
        public string Id { get; set; } = string.Empty;
        public PositionEntity? Position { get; set; }
        public double[] Shape { get; set; } = [];
    }
}
