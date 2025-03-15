package io.github.cichlidmc.tinycodecs.codec.map;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinycodecs.MapCodec;
import io.github.cichlidmc.tinycodecs.util.Utils;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNull;

public class FieldOfCodec<T> implements MapCodec<T> {
	private final Codec<T> wrapped;
	private final String name;

	public FieldOfCodec(Codec<T> wrapped, String name) {
		this.wrapped = wrapped;
		this.name = name;
	}

	@Override
	public DecodeResult<T> decode(JsonObject json) {
		JsonValue value = Utils.getOrJsonNull(json, this.name);
		return this.wrapped.decode(value);
	}

	@Override
	public void encode(JsonObject json, T value) {
		JsonValue encoded = this.wrapped.encode(value);
		if (!(encoded instanceof JsonNull)) {
			json.put(this.name, encoded);
		}
	}
}
