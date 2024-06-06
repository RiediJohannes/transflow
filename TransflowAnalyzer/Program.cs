using TransflowAnalyzer.Api.Services;
using TransflowAnalyzer.Sources.Messaging;
using DotNetEnv;


try
{
    // load environment variables from the .env file immediately outside this subproject's root
    Env.Load("../runtime.env");

    var builder = WebApplication.CreateBuilder(args);


    string? mqttClientId = Environment.GetEnvironmentVariable("MQTT_ANALYZER_ID");
    string? mqttMetricsTopic = Environment.GetEnvironmentVariable("MQTT_METRICS_TOPIC");

    if (mqttClientId is null || mqttMetricsTopic is null)
        throw new Exception("Failed to find required environment variables!");

    var mqttParameters = new MqttParameters(mqttClientId, "localhost", [mqttMetricsTopic]);


    // Add services to the container.
    builder.Services.AddSingleton(mqttParameters);
    builder.Services.AddHostedService<MqttConsumerService>();
    builder.Services.AddGrpc();

    var webApi = builder.Build();

    // Configure the HTTP request pipeline.
    webApi.MapGet("/", () => "This service only supports communication through a gRPC client. HTTP/1.1 GET request is not supported.");
    webApi.MapGrpcService<ExampleService>();

    webApi.Run();
}
catch (Exception exp)
{
    Console.Error.WriteLine(exp.ToString());
    Environment.Exit(100);
}

