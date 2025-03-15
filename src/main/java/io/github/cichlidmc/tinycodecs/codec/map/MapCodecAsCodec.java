package io.github.cichlidmc.tinycodecs.codec.map;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinycodecs.MapCodec;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;

public class MapCodecAsCodec<T> implements Codec<T> {
	private final MapCodec<T> mapCodec;

	public MapCodecAsCodec(MapCodec<T> mapCodec) {
		this.mapCodec = mapCodec;
	}

	@Override
	public DecodeResult<T> decode(JsonValue value) {
		if (value instanceof JsonObject) {
			return this.mapCodec.decode(value.asObject());
		} else {
			return DecodeResult.error("Not an object");
		}
	}

	@Override
	public JsonValue encode(T value) {
		JsonObject json = new JsonObject();
		this.mapCodec.encode(json, value);
		return json;
	}
}
