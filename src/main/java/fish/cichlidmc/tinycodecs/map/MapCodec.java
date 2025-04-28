package fish.cichlidmc.tinycodecs.map;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.codec.map.MapCodecAsCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

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
}
