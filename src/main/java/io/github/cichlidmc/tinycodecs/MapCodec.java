package io.github.cichlidmc.tinycodecs;

import io.github.cichlidmc.tinyjson.value.composite.JsonObject;

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
	 */
	void encode(JsonObject json, T value);

	default Codec<T> asCodec() {
		return Codecs.asCodec(this);
	}
}
