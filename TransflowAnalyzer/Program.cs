using TransflowAnalyzer.Api.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddGrpc();

var app = builder.Build();

// Configure the HTTP request pipeline.
app.MapGet("/", () => "This service only supports communication through a gRPC client. HTTP/1.1 GET request is not supported.");
app.MapGrpcService<ExampleService>();

app.Run();
