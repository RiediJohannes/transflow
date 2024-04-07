namespace TransflowAnalyzer.Sources.Entities
{
    public class LinkEntity
    {
        public string? ApproachedLane { get; set; }
        public bool? HasPrio { get; set; }
        public bool? IsOpen { get; set; }
        public bool? HasFoe { get; set; }
        public string? ApproachedInternal { get; set; }
        public string? State { get; set; }
        public string? Direction { get; set; }
        public double? Length { get; set; }
    }
}
