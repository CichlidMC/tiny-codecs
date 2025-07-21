package fish.cichlidmc.tinycodecs.map;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.codec.map.MapCodecAsCodec;
import fish.cichlidmc.tinycodecs.codec.map.UnitMapCodec;
import fish.cichlidmc.tinycodecs.util.Lazy;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.function.Supplier;

/**
 * Similar to a {@link Codec}, but operates specifically on maps, aka objects.
 */
public interface MapCodec<T> {
	/**
	 * Attempt to decode an object from fields on the given JSON object.
	 */
	CodecResult<T> decode(JsonObject json);

	/**
	 * Encode the given value onto the given JSON object.
	 * @return a result, containing the given JSON if successful
	 */
	CodecResult<JsonObject> encode(JsonObject json, T value);

	default Codec<T> asCodec() {
		return new MapCodecAsCodec<>(this);
	}

	static <T> MapCodec<T> unit(T unit) {
		return new UnitMapCodec<>(unit);
	}

	static <T> MapCodec<T> lazy(Supplier<? extends MapCodec<T>> factory) {
		Lazy<MapCodec<T>> lazy = new Lazy<>(factory);

		return new MapCodec<>() {
			@Override
			public CodecResult<T> decode(JsonObject json) {
				return lazy.get().decode(json);
			}

			@Override
			public CodecResult<JsonObject> encode(JsonObject json, T value) {
				return lazy.get().encode(json, value);
			}
		};
	}
}
