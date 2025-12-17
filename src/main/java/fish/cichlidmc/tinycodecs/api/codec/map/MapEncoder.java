package fish.cichlidmc.tinycodecs.api.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Encoder;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Function;

/**
 * Encodes a value to a {@link JsonObject}.
 * @see Encoder
 */
@FunctionalInterface
public interface MapEncoder<T> {
	/**
	 * Encode the given value onto the given {@link JsonObject}.
	 * @return an optional containing an error message if encoding failed
	 * @implNote implementations are expected to always return an error message instead of throwing an exception.
	 * In addition, if an error occurs, the given {@link JsonObject} should not be modified.
	 */
	Optional<String> encode(JsonObject json, T value);

	/**
	 * @return a new MapEncoder that applies the given function before encoding
	 */
	@ApiStatus.NonExtendable
	default <B> MapEncoder<B> comap(Function<? super B, ? extends T> function) {
		return (json, value) -> this.encode(json, function.apply(value));
	}

	/**
	 * @return a new MapEncoder that applies the given result-bearing function before encoding
	 */
	@ApiStatus.NonExtendable
	default <B> MapEncoder<B> flatComap(Function<? super B, ? extends Result<? extends T>> function) {
		return (json, value) -> switch (function.apply(value)) {
			case Result.Success(/* It's!! */ T v /* time!!! */) -> this.encode(json, v);
			case Result.Error(String message) -> Optional.of(message);
		};
	}
}
