using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Protocol;
using MQTTnet.Server;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;
using TransflowAnalyzer.Sources.Entities;
using TransflowAnalyzer.Sources.Messaging.Json;
using TransflowAnalyzer.Sources.Messaging.Mqtt;


namespace TransflowAnalyzer.Sources.Messaging
{
    public partial class MqttConsumerService : BackgroundService
    {
        private readonly MqttParameters _parameters;
        private readonly ISimulationDataSink _storage;
        private readonly IMqttClient _mqttClient;
        private readonly MqttClientOptions _mqttOptions;
        private readonly JsonSerializerOptions _jsonOptions;

        public MqttConsumerService(MqttParameters parameters, ISimulationDataSink storage)
        {
            _parameters = parameters;
            _storage = storage;

            _mqttOptions = new MqttClientOptionsBuilder()
                    .WithProtocolVersion(MQTTnet.Formatter.MqttProtocolVersion.V500)
                    .WithTcpServer(parameters.BrokerUrl)
                    .WithClientId(parameters.ClientId)
                    .WithCleanSession()
                    .Build();

            _mqttClient = new MqttFactory().CreateMqttClient();

            _jsonOptions = new JsonSerializerOptions(JsonSerializerDefaults.Web)
            {
                WriteIndented = true,
                TypeInfoResolver = SimDataJsonContext.Default,
                DefaultIgnoreCondition = JsonIgnoreCondition.Never,
                UnmappedMemberHandling = JsonUnmappedMemberHandling.Skip,
            };
        }

        protected async override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            try
            {
                var connectionResult = await _mqttClient.ConnectAsync(_mqttOptions, stoppingToken);

                if (connectionResult.ResultCode != MqttClientConnectResultCode.Success)
                {
                    throw new IOException($"Failed to establish connection to MQTT broker at URL: {_parameters.BrokerUrl};\n" +
                        $"Status code: {connectionResult.ResultCode}");
                }

                // callback function when a message is received
                _mqttClient.ApplicationMessageReceivedAsync += args =>
                {
                    return AcceptSimDataMessage(args);
                };

                // subscribe to every topic
                foreach (var topic in _parameters.Subscriptions)
                {
                    await _mqttClient.SubscribeAsync(topic, MqttQualityOfServiceLevel.AtLeastOnce, stoppingToken);
                }
            }
            catch (Exception exp)
            {
                throw new IOException("Ran into error when connecting to MQTT broker! Reason: " + exp.Message);
            }
        }


        private Task AcceptSimDataMessage(MqttApplicationMessageReceivedEventArgs args)
        {
            try
            {
                SimDataTopic topicData = ParseTopic(args.ApplicationMessage.Topic);

                switch (topicData.Domain)
                {
                    case Domain.Vehicles:
                        ParseAndStore<VehicleEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    case Domain.VehicleTypes:
                        ParseAndStore<VehicleTypeEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    case Domain.Edges:
                        ParseAndStore<EdgeEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    case Domain.Lanes:
                        ParseAndStore<LaneEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    case Domain.Junctions:
                        ParseAndStore<JunctionEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    case Domain.Routes:
                        ParseAndStore<RouteEntity>(args.ApplicationMessage.PayloadSegment, topicData.SimulationId, topicData.Time);
                        break;
                    default:
                        throw new TopicParseException(args.ApplicationMessage.Topic,
                        $"Unrecognized metric domain '{topicData.Domain}'");
                }
            }
            catch (TopicParseException exp)
            {
                Console.Error.WriteLine(exp.ToString());
            }

            return Task.CompletedTask;
        }

        // these warnings are false positives according to https://stackoverflow.com/a/78579373/18284107
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Trimming", "IL2026:Members annotated with 'RequiresUnreferencedCodeAttribute' require dynamic access otherwise can break functionality when trimming application code", Justification = "<Pending>")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("AOT", "IL3050:Calling members annotated with 'RequiresDynamicCodeAttribute' may break functionality when AOT compiling.", Justification = "<Pending>")]
        private void ParseAndStore<T>(ReadOnlySpan<byte> utf8json, string simulationId, long timeStep)
            where T : TimeSeriesData, new()
        {
            T? timeSeriesEntity = JsonSerializer.Deserialize<T>(utf8json, _jsonOptions);

            if (timeSeriesEntity is not null)
            {
                timeSeriesEntity.TimeStep = timeStep;
                _storage.GetOrCreateDatabase(simulationId).Add(timeSeriesEntity);
            }
        }

        private static SimDataTopic ParseTopic(string topic)
        {
            try
            {
                Match match = TopicRegex().Match(topic);

                if (match.Success)
                {
                    string domainMatch = match.Groups["domain"].Value.Replace("_", "");
                    Domain domain = (Domain) Enum.Parse(typeof(Domain), domainMatch, true) ;

                    return new SimDataTopic(
                        match.Groups["sim"].Value,
                        domain,
                        long.Parse(match.Groups["time"].Value)
                    );
                }
                else
                {
                    throw new TopicParseException(topic, "Given topic did not match the structure of a sim data topic.");
                }
            }
            catch (Exception exp)
            {
                throw new TopicParseException(topic, "Failed to parse topic as metrics topic!", exp);
            }
        }

        [GeneratedRegex("^sim/(?<sim>[^/]+)/metrics/(?<domain>[^/]+)/(?<time>[^/]+)$")]
        private static partial Regex TopicRegex();
    }
}
