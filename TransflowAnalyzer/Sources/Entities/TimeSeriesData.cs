namespace TransflowAnalyzer.Sources.Entities
{
    public abstract class TimeSeriesData : IComparable<TimeSeriesData>, IComparable
    {
        public long TimeStep { get; set; } = 0;
        public string Id { get; set; } = string.Empty;



        public int CompareTo(TimeSeriesData? other)
        {
            return other is not null
                ? TimeStep.CompareTo(other.TimeStep)
                : 1;
        }

        public int CompareTo(object? obj)
        {
            return CompareTo(obj as TimeSeriesData);
        }
    }
}
