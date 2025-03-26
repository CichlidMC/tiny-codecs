package io.github.cichlidmc.tinycodecs;

import io.github.cichlidmc.tinyjson.value.JsonValue;

import java.util.function.Function;

/**
 * Decodes a value from JSON.
 */
@FunctionalInterface
public interface Decoder<T> {
	/**
	 * Attempt to decode the given JsonValue.
	 * Should never throw an exception.
	 * @param json the JSON to decode. A JsonNull may indicate either a literal null or a missing field
	 */
	CodecResult<T> decode(JsonValue json);

	default <B> Decoder<B> map(Function<? super T, ? extends B> function) {
		return json -> this.decode(json).map(function);
	}

	default <B> Decoder<B> flatMap(Function<? super T, ? extends CodecResult<? extends B>> function) {
		// load-bearing Function.identity()
		return json -> this.decode(json).flatMap(value -> function.apply(value).map(Function.identity()));
	}
}
