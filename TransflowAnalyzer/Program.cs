using TransflowAnalyzer.Api.Services;
using TransflowAnalyzer.Sources.Messaging;
using DotNetEnv;


try
{
    // load environment variables from the .env file immediately outside this subproject's root
    Env.Load("../runtime.env");

    var builder = WebApplication.CreateBuilder(args);


    string? metricsTopic = Environment.GetEnvironmentVariable("MQTT_METRICS_TOPIC");
    string? mqttClientId = Environment.GetEnvironmentVariable("MQTT_ANALYZER_ID");
    string? mqttMetricsTopic = Environment.GetEnvironmentVariable("MQTT_METRICS_TOPIC");

    if (metricsTopic is null || mqttClientId is null || mqttMetricsTopic is null)
        throw new Exception("Failed to find required environment variables!");

    var mqttClient = await MqttServiceFactory.CreateAsync("localhost", mqttClientId, [mqttMetricsTopic]);


    // Add services to the container.
    builder.Services.AddGrpc();
    builder.Services.AddSingleton(mqttClient);

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

