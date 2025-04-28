package fish.cichlidmc.tinycodecs.codec;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

public final class UnitCodec<T> implements Codec<T> {
	private final T unit;

	public UnitCodec(T unit) {
		this.unit = unit;
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		return CodecResult.success(this.unit);
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		return CodecResult.success(new JsonNull());
	}
}
