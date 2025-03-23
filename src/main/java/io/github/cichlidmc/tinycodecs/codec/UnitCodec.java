package io.github.cichlidmc.tinycodecs.codec;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNull;

public final class UnitCodec<T> implements Codec<T> {
	private final T unit;

	public UnitCodec(T unit) {
		this.unit = unit;
	}

	@Override
	public DecodeResult<T> decode(JsonValue value) {
		return DecodeResult.success(this.unit);
	}

	@Override
	public JsonValue encode(T value) {
		return new JsonNull();
	}
}
