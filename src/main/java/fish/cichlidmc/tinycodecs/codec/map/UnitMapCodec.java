package fish.cichlidmc.tinycodecs.codec.map;

import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.map.MapCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

public record UnitMapCodec<T>(T unit) implements MapCodec<T> {
	@Override
	public CodecResult<T> decode(JsonObject json) {
		return CodecResult.success(this.unit);
	}

	@Override
	public CodecResult<JsonObject> encode(JsonObject json, T value) {
		return CodecResult.success(json);
	}
}
