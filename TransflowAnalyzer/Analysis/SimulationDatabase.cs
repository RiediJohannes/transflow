using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Analysis
{
    public class SimulationDatabase
    {
        private readonly Dictionary<Type, object> _databases = [];

        public void Add<T>(T data)
            where T : TimeSeriesData, new()
        {
            var dataset = _databases.GetValueOrDefault(typeof(T), new TimeSeriesStorage<T>()) as TimeSeriesStorage<T>;
            dataset?.Add(data);
        }

        public T? Get<T>(string id, long timeStep)
            where T : TimeSeriesData, new()
        {
            var dataset = _databases.GetValueOrDefault(typeof(T), new TimeSeriesStorage<T>()) as TimeSeriesStorage<T>;
            if (dataset is not null)
            {
                return dataset.GetAtPoint(id, timeStep);
            }

            return default;
        }

        public SortedSet<T> GetRange<T>(string id, long lowerTimeBound, long upperTimeBound)
            where T : TimeSeriesData, new()
        {
            var dataset = _databases.GetValueOrDefault(typeof(T), new TimeSeriesStorage<T>()) as TimeSeriesStorage<T>;
            if (dataset is not null)
            {
                return dataset.GetInRange(id, lowerTimeBound, upperTimeBound);
            }

            return [];
        }
    }
}
