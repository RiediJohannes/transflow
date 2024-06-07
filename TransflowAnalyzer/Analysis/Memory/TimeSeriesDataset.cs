using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Analysis.Memory
{
    public class TimeSeriesDataset<T> : Dictionary<string, SortedSet<T>>
        where T : TimeSeriesData, new()
    {

        public void Add(T data)
        {
            TryAdd(data.Id, []); // add a new list of isn't one already

            if (TryGetValue(data.Id, out SortedSet<T>? timeSeries))
            {
                timeSeries.Add(data);
            }
        }

        public T? GetAtPoint(string id, long time)
        {
            var timeSeries = this.GetValueOrDefault(id, []);

            var key = new T() { TimeStep = time };
            timeSeries.TryGetValue(key, out T? result);

            return result;
        }

        public SortedSet<T> GetInRange(string id, long startTime, long endTime)
        {
            var timeSeries = this.GetValueOrDefault(id, []);

            var from = new T() { TimeStep = startTime };
            var to = new T() { TimeStep = endTime };
            return timeSeries.GetViewBetween(from, to);
        }
    }
}
