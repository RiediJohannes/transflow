namespace TransflowAnalyzer.Analysis.Memory
{
    public class SimulationStorage
    {
        private readonly Dictionary<string, TimeSeriesDatabase> _simulationDatabases = [];

        public SimulationStorage() { }


        public TimeSeriesDatabase Database(string simulationId)
        {
            if (_simulationDatabases.TryGetValue(simulationId, out TimeSeriesDatabase? database))
            {
                return database;
            }
            else
            {
                database = new TimeSeriesDatabase();
                _simulationDatabases.Add(simulationId, database);

                return database;
            }
        }
    }
}
