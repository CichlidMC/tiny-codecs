package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

public record UnitCodec<T>(T unit) implements Codec<T> {
	@Override
	public Result<? extends JsonValue> encode(T value) {
		return Result.success(new JsonNull());
	}

	@Override
	public Result<T> decode(JsonValue value) {
		return Result.success(this.unit);
	}
}
