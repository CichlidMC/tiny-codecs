package fish.cichlidmc.tinycodecs.api.codec;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.map.MapEncoder;
import fish.cichlidmc.tinyjson.value.JsonValue;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * Encodes a value to JSON.
 * @see MapEncoder
 */
@FunctionalInterface
public interface Encoder<T> {
	/**
	 * Attempt to encode the given value to JSON.
	 * @implNote implementations are expected to always return an Error result instead of throwing an exception
	 */
	CodecResult<? extends JsonValue> encode(T value);

	/**
	 * @return a new Encoder that applies the given function before encoding
	 */
	@ApiStatus.NonExtendable
	default <B> Encoder<B> comap(Function<? super B, ? extends T> function) {
		return value -> this.encode(function.apply(value));
	}

	/**
	 * @return a new Encoder that applies the given result-bearing function before encoding
	 */
	@ApiStatus.NonExtendable
	default <B> Encoder<B> flatComap(Function<? super B, ? extends CodecResult<? extends T>> function) {
		return value -> function.apply(value).flatMap(this::encode);
	}
}
