using MQTTnet.Client;
using MQTTnet;
using MQTTnet.Protocol;
using System.Text;
using MQTTnet.Server;

namespace TransflowAnalyzer.Sources.Messaging
{
    public class MqttConsumerService : BackgroundService
    {
        private readonly MqttParameters _parameters;
        private readonly IMqttClient _mqttClient;
        private readonly MqttClientOptions _options;

        public MqttConsumerService(MqttParameters parameters)
        {
            _parameters = parameters;

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
                _mqttClient.ApplicationMessageReceivedAsync += e =>
                {
                    Console.WriteLine($"Payload: {Encoding.UTF8.GetString(e.ApplicationMessage.PayloadSegment)}");
                    Console.WriteLine($"Topic: {e.ApplicationMessage.Topic}");
                    Console.WriteLine();
                    return Task.CompletedTask;
                };

                // subscribe to every topic
                foreach (var topic in _parameters.Topics)
                {
                    await _mqttClient.SubscribeAsync(topic, MqttQualityOfServiceLevel.AtLeastOnce, stoppingToken);
                }
            }
            catch (Exception exp)
            {
                throw new IOException("Ran into error when connecting to MQTT broker! Reason: " + exp.Message);
            }
        }
    }
}
