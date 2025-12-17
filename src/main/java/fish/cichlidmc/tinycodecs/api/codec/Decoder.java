package fish.cichlidmc.tinycodecs.api.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.map.MapDecoder;
import fish.cichlidmc.tinyjson.value.JsonValue;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/// Decodes a value from JSON.
/// @see MapDecoder
@FunctionalInterface
public interface Decoder<T> {
	/// Attempt to decode the given JsonValue.
	/// @param json the JSON to decode. A JsonNull may indicate either a literal null or a missing field
	/// @implNote implementations are expected to always return an Error result instead of throwing an exception
	Result<T> decode(JsonValue json);

	/// @return a new Decoder that applies the given function to decoded values
	@ApiStatus.NonExtendable
	default <B> Decoder<B> map(Function<? super T, ? extends B> function) {
		return json -> this.decode(json).map(function);
	}

	/// @return a new MapDecoder that applies the given result-bearing function to decoded values
	@ApiStatus.NonExtendable
	default <B> Decoder<B> flatMap(Function<? super T, ? extends Result<? extends B>> function) {
		// load-bearing Function.identity()
		return json -> this.decode(json).flatMap(value -> function.apply(value).map(Function.identity()));
	}
}
