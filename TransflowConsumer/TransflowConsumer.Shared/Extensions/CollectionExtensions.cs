namespace TransflowConsumer.Shared.Extensions
{
    public static class CollectionExtensions
    {
        public static bool IsNullOrEmpty<T>(this IEnumerable<T>? collection)
        {
            return collection is null || !collection.Any();
        }

        public static bool HasItems<T>(this IEnumerable<T>? collection)
        {
            return !collection.IsNullOrEmpty();
        }
    }
}
