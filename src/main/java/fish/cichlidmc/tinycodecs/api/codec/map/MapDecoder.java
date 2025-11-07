package fish.cichlidmc.tinycodecs.api.codec.map;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Decoder;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * Decodes a value from a {@link JsonObject}.
 * @see Decoder
 */
public interface MapDecoder<T> {
	/**
	 * Attempt to decode an object from fields on the given {@link JsonObject}.
	 * @implNote implementations are expected to always return an Error result instead of throwing an exception
	 */
	CodecResult<T> decode(JsonObject json);

	/**
	 * @return a new MapDecoder that applies the given function to decoded values
	 */
	@ApiStatus.NonExtendable
	default <B> MapDecoder<B> map(Function<? super T, ? extends B> function) {
		return json -> this.decode(json).map(function);
	}

	/**
	 * @return a new MapDecoder that applies the given result-bearing function to decoded values
	 */
	@ApiStatus.NonExtendable
	default <B> MapDecoder<B> flatMap(Function<? super T, ? extends CodecResult<? extends B>> function) {
		// load-bearing Function.identity()
		return json -> this.decode(json).flatMap(value -> function.apply(value).map(Function.identity()));
	}
}
