using TransflowAnalyzer.Sources.Entities;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace TransflowAnalyzer.Analysis
{
    public class TimeSeriesStorage<T> : Dictionary<string, SortedSet<T>>
        where T : TimeSeriesData, new()
    {

        public void Add(T data)
        {
            var dataset = this.GetValueOrDefault(data.Id, []);
            dataset.Add(data);
        }

        public T? GetAtPoint(string id, long time)
        {
            var dataset = this.GetValueOrDefault(id, []);

            var key = new T() { TimeStep = time };
            dataset.TryGetValue(key, out T? result);

            return result;
        }

        public SortedSet<T> GetInRange(string id, long startTime, long endTime)
        {
            var dataset = this.GetValueOrDefault(id, []);

            var from = new T() { TimeStep = startTime };
            var to = new T() { TimeStep = endTime };
            return dataset.GetViewBetween(from, to);
        }
    }
}
