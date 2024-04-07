using MQTTnet.Client;
using MQTTnet;

namespace TransflowAnalyzer.Sources.Messaging
{
    public static class MqttServiceFactory
    {
        public static async Task<IMqttClient> CreateAsync(string brokerUrl, string clientId)
        {
            try
            {
                var options = new MqttClientOptionsBuilder()
                    .WithTcpServer(brokerUrl)
                    .WithClientId(clientId)
                    .WithCleanSession()
                    .Build();

                var mqttClient = new MqttFactory().CreateMqttClient();
                var connectionResult = await mqttClient.ConnectAsync(options, CancellationToken.None);

                if (connectionResult.ResultCode != MqttClientConnectResultCode.Success)
                {
                    throw new IOException($"Failed to establish connection to MQTT broker at URL: {brokerUrl};\n" +
                        $"Status code: {connectionResult.ResultCode}");
                }

                return mqttClient;
            }
            catch (Exception exp)
            {
                throw new IOException("Ran into error when connecting to MQTT broker! Reason: " + exp.Message);
            }
        }
    }
}
