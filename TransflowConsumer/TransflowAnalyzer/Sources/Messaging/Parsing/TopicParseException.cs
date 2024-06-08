namespace TransflowAnalyzer.Sources.Messaging.Json
{
    public class TopicParseException : Exception
    {
        public string Topic { get; init; }

        public TopicParseException(string topic)
        {
            Topic = topic;
        }

        public TopicParseException(string topic, string message) : base(message)
        {
            Topic = topic;
        }

        public TopicParseException(string topic, string message, Exception innerException) : base(message, innerException)
        {
            Topic = topic;
        }


        public override string ToString()
        {
            string reason = InnerException != null
                ? $"\nReason: {InnerException.GetType} - {InnerException.Message}"
                : string.Empty;

            return $"{nameof(TopicParseException)}: Encountered unexpected structure of topic '{Topic}'.{reason}";
        }
    }
}
