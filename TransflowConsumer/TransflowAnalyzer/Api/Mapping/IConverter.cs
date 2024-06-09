namespace TransflowAnalyzer.Api.Mapping
{
    /// <summary>
    /// Simple interface to have a converter (mapper) between two types.<br/><br/>
    /// <b>Hint:</b> Do <b>not</b> use <see langword="static"/> classes and methods for mappers classes that implement this interface.
    /// This will prevent you from adding potentially needed dependencies, database connections etc. to them later.
    /// Instead, use <b>dependency injection</b> to inject the mapper instances when needed.
    /// </summary>
    public interface IConverter<TInput, TOutput>
    {
        TOutput Convert(TInput input);
    }
}
