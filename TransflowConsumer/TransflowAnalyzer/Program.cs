using DotNetEnv;
using GrpcBrowser.Configuration;
using System.IO.Compression;
using TransflowAnalyzer.Analysis.Memory;
using TransflowAnalyzer.Api.Endpoints;
using TransflowAnalyzer.Api.Mapping;
using TransflowAnalyzer.Api.Services;
using TransflowAnalyzer.Sources.Entities;
using TransflowAnalyzer.Sources.Messaging;
using TransflowAnalyzer.Sources.Messaging.Mqtt;
using TransflowConsumer.Shared.Protobuffs;


try
{
    // load environment variables from the .env file immediately outside this subproject's root
    Env.Load("../../runtime.env");

    var builder = WebApplication.CreateBuilder(args);


    string? mqttClientId = Environment.GetEnvironmentVariable("MQTT_ANALYZER_ID");
    string? mqttRootTopic = Environment.GetEnvironmentVariable("MQTT_TOPIC_ROOT");
    string? mqttMetricsTopic = Environment.GetEnvironmentVariable("MQTT_TOPIC_METRICS");
    string[] mqttSubscriptions = Environment.GetEnvironmentVariable("MQTT_SUBSCRIPTIONS")?.Split(",") ?? [];

    if (mqttClientId is null || mqttRootTopic is null || mqttMetricsTopic is null)
        throw new Exception("Failed to find required environment variables!");

    var mqttParameters = new MqttParameters(mqttClientId, "localhost",
        mqttRootTopic, mqttMetricsTopic, mqttSubscriptions);


    // Add services to the container.
    builder.Services.AddTransient<IConverter<PositionEntity?, PositionData>, PositionMapper>();
    builder.Services.AddTransient<IConverter<VehicleEntity, VehicleData>, VehicleMapper>();
    builder.Services.AddTransient<IConverter<VehicleTypeEntity, VehicleTypeData>, VehicleTypeMapper>();

    builder.Services.AddSingleton<SimulationStorage>();
    builder.Services.AddSingleton<ISimulationDataSink>(x => x.GetRequiredService<SimulationStorage>());
    builder.Services.AddSingleton<ISimulationRepository>(x => x.GetRequiredService<SimulationStorage>());
    builder.Services.AddSingleton<IVehicleRepository>(x => x.GetRequiredService<SimulationStorage>());

    builder.Services.AddSingleton(mqttParameters);
    builder.Services.AddHostedService<MqttConsumerService>();

    builder.Services.AddGrpc(
        config => { config.ResponseCompressionLevel = CompressionLevel.Fastest; }
    );
    builder.Services.AddGrpcBrowser();

    builder.Services.AddCors(options =>
    {
        options.AddPolicy("AllowAllGrpc", builder =>
        {
            builder.AllowAnyOrigin()
                .AllowAnyMethod()
                .AllowAnyHeader()
                .WithExposedHeaders("Grpc-Status", "Grpc-Message", "Grpc-Encoding", "Grpc-Accept-Encoding");
        });

        // stricter alternative: Only allow specific origins (the domains of specific clients)
        //options.AddPolicy(name: "TransflowVisualizerPolicy",
        //    policy =>
        //    {
        //        policy.WithOrigins("https://localhost:7013", "http://localhost:5159")
        //            .AllowAnyHeader()
        //            .AllowAnyMethod();
        //    });
    });


    var webApi = builder.Build();

#if DEBUG
    webApi.UseWebAssemblyDebugging();
#endif

    webApi.UseRouting();
    webApi.UseCors();

    webApi.UseGrpcWeb();
    webApi.UseGrpcBrowser();

    // Configure the HTTP request pipeline.
    webApi.MapGet("/", () => "This service only supports communication through a gRPC client. HTTP/1.1 GET request is not supported.");

    webApi.MapGrpcService<SimulationService>().EnableGrpcWeb()
        .RequireCors("AllowAllGrpc").AddToGrpcBrowserWithClient<Simulations.SimulationsClient>();
    webApi.MapGrpcService<VehicleService>().EnableGrpcWeb()
        .RequireCors("AllowAllGrpc").AddToGrpcBrowserWithClient<Vehicles.VehiclesClient>();

    webApi.MapGrpcBrowser();

    webApi.Run();
}
catch (Exception exp)
{
    Console.Error.WriteLine(exp.ToString());
    Environment.Exit(100);
}

