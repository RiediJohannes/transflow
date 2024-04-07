using MQTTnet.Client;
using TransflowAnalyzer.Api.Services;
using TransflowAnalyzer.Sources.Messaging;

var builder = WebApplication.CreateBuilder(args);

var mqttClient = await MqttServiceFactory.CreateAsync("localhost", "transflow_analyzer");

// Add services to the container.
builder.Services.AddGrpc();
builder.Services.AddSingleton(mqttClient);

var webApi = builder.Build();

// Configure the HTTP request pipeline.
webApi.MapGet("/", () => "This service only supports communication through a gRPC client. HTTP/1.1 GET request is not supported.");
webApi.MapGrpcService<ExampleService>();

webApi.Run();
