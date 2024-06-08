using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Analysis.Memory
{
    public class TimeSeriesDatabase
    {
        private readonly Dictionary<Type, object> _datasets = [];

        public void Add<T>(T data)
            where T : TimeSeriesData, new()
        {
            _datasets.TryAdd(typeof(T), new TimeSeriesDataset<T>()); // add a new list of isn't one already

            if (_datasets.TryGetValue(typeof(T), out object? value))
            {
                var dataset = value as TimeSeriesDataset<T>;
                dataset?.Add(data);
            }
        }

        public T? Get<T>(string id, long timeStep)
            where T : TimeSeriesData, new()
        {
            var dataset = _datasets.GetValueOrDefault(typeof(T), new TimeSeriesDataset<T>()) as TimeSeriesDataset<T>;
            if (dataset is not null)
            {
                return dataset.GetAtPoint(id, timeStep);
            }

            return default;
        }

        public SortedSet<T> GetRange<T>(string id, long lowerTimeBound, long upperTimeBound)
            where T : TimeSeriesData, new()
        {
            var dataset = _datasets.GetValueOrDefault(typeof(T), new TimeSeriesDataset<T>()) as TimeSeriesDataset<T>;
            if (dataset is not null)
            {
                return dataset.GetInRange(id, lowerTimeBound, upperTimeBound);
            }

            return [];
        }
    }
}
