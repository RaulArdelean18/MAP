package domain;

/**
 * Simple immutable pair, used as composite id for friendships.
 */
public record Tuple<V1, V2>(V1 first, V2 second) {
}
