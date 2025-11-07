package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

public record UnitCodec<T>(T unit) implements Codec<T> {
	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		return CodecResult.success(new JsonNull());
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		return CodecResult.success(this.unit);
	}
}
