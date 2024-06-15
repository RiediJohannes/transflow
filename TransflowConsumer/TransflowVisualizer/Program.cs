using Grpc.Net.Client;
using Grpc.Net.Client.Web;
using Microsoft.AspNetCore.Components.Web;
using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using MudBlazor.Services;
using TransflowConsumer.Shared.Protobuffs;
using TransflowVisualizer;


const string transflowApiUrl = "https://localhost:7053";

var builder = WebAssemblyHostBuilder.CreateDefault(args);
builder.RootComponents.Add<App>("#app");
builder.RootComponents.Add<HeadOutlet>("head::after");
builder.Services.AddScoped(sp => new HttpClient { BaseAddress = new Uri(builder.HostEnvironment.BaseAddress) });

builder.Services.AddMudServices(); // MudBlazor services

// gRPC services
var grpcWebClient = new HttpClient(new GrpcWebHandler(GrpcWebMode.GrpcWeb, new HttpClientHandler()));
var grpcChannel = GrpcChannel.ForAddress(transflowApiUrl, new GrpcChannelOptions { HttpClient = grpcWebClient });

builder.Services.AddSingleton(services => new Simulations.SimulationsClient(grpcChannel));
builder.Services.AddSingleton(services => new Vehicles.VehiclesClient(grpcChannel));

await builder.Build().RunAsync();
