package io.github.cichlidmc.tinycodecs.codec.map;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinycodecs.map.MapCodec;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;

public class MapCodecAsCodec<T> implements Codec<T> {
	private final MapCodec<T> mapCodec;

	public MapCodecAsCodec(MapCodec<T> mapCodec) {
		this.mapCodec = mapCodec;
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		if (value instanceof JsonObject) {
			return this.mapCodec.decode(value.asObject());
		} else {
			return CodecResult.error("Not an object");
		}
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		JsonObject json = new JsonObject();
		return this.mapCodec.encode(json, value);
	}
}
