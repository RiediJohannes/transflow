using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Protocol;
using MQTTnet.Server;
using System.Text.Json;
using System.Text.RegularExpressions;
using TransflowAnalyzer.Analysis;
using TransflowAnalyzer.Sources.Entities;

namespace TransflowAnalyzer.Sources.Messaging
{
    public partial class MqttConsumerService : BackgroundService
    {
        private readonly MqttParameters _parameters;
        private readonly SimulationDatabase _db;
        private readonly IMqttClient _mqttClient;
        private readonly MqttClientOptions _options;

        public MqttConsumerService(MqttParameters parameters, SimulationDatabase database)
        {
            _parameters = parameters;
            _db = database;

            _options = new MqttClientOptionsBuilder()
                    .WithProtocolVersion(MQTTnet.Formatter.MqttProtocolVersion.V500)
                    .WithTcpServer(parameters.BrokerUrl)
                    .WithClientId(parameters.ClientId)
                    .WithCleanSession()
                    .Build();

            _mqttClient = new MqttFactory().CreateMqttClient();
        }

        protected async override Task ExecuteAsync(CancellationToken stoppingToken)
        {
            try
            {
                var connectionResult = await _mqttClient.ConnectAsync(_options, stoppingToken);

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

        private static Task AcceptSimDataMessage(MqttApplicationMessageReceivedEventArgs args)
        {
            //Console.WriteLine($"Payload: {Encoding.UTF8.GetString(args.ApplicationMessage.PayloadSegment)}");

            try
            {
                SimDataTopic dataTopic = ParseTopic(args.ApplicationMessage.Topic);

                var options = new JsonSerializerOptions
                {
                    PropertyNameCaseInsensitive = true
                };

                switch (dataTopic.Domain)
                {
                    case Domain.Vehicles:
                    {
                        VehicleEntity? vehicle = JsonSerializer.Deserialize<VehicleEntity>(args.ApplicationMessage.PayloadSegment, options);
                        
                        break;
                    }
                    case Domain.VehicleTypes:
                    {
                        break;
                    }
                    case Domain.Edges:
                    {
                        break;
                    }
                    case Domain.Lanes:
                    {
                        break;
                    }
                    case Domain.Junctions:
                    {
                        break;
                    }
                    case Domain.Routes:
                    {
                        break;
                    }
                    default:
                        throw new TopicParseException(args.ApplicationMessage.Topic,
                        $"Unrecognized metric domain '{dataTopic.Domain}'");
                }
            }
            catch (TopicParseException exp)
            {
                Console.Error.WriteLine(exp.ToString());
            }

            return Task.CompletedTask;
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
