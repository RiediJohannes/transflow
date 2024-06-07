﻿using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Analysis
{
    public class TimeSeriesStorage<T> : Dictionary<string, SortedSet<T>>
        where T : TimeSeriesData, new()
    {

        public void Add(T data)
        {
            TryAdd(data.Id, []); // add a new list of isn't one already

            if (TryGetValue(data.Id, out SortedSet<T>? dataset))
            {
                dataset.Add(data);
            }
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