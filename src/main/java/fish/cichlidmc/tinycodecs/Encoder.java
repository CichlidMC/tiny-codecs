package fish.cichlidmc.tinycodecs;

import fish.cichlidmc.tinyjson.value.JsonValue;

import java.util.function.Function;

/**
 * Encodes a value to JSON.
 */
@FunctionalInterface
public interface Encoder<T> {
	/**
	 * Attempt to encode the given value to JSON.
	 * Should never throw an exception.
	 */
	CodecResult<? extends JsonValue> encode(T value);

	default <B> Encoder<B> comap(Function<? super B, ? extends T> function) {
		return value -> this.encode(function.apply(value));
	}

	default <B> Encoder<B> flatComap(Function<? super B, ? extends CodecResult<? extends T>> function) {
		return value -> function.apply(value).flatMap(this::encode);
	}
}
