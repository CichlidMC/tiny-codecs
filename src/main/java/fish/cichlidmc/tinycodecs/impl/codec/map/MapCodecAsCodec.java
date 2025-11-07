package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

public record MapCodecAsCodec<T>(MapCodec<T> mapCodec) implements Codec<T> {
	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		JsonObject json = new JsonObject();
		return CodecResult.of(json, this.mapCodec.encode(json, value));
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		if (value instanceof JsonObject) {
			return this.mapCodec.decode(value.asObject());
		} else {
			return CodecResult.error("Not an object");
		}
	}
}
