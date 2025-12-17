package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;

public record MapCodecAsCodec<T>(MapCodec<T> mapCodec) implements Codec<T> {
	@Override
	public Result<? extends JsonValue> encode(T value) {
		JsonObject json = new JsonObject();
		Optional<String> error = this.mapCodec.encode(json, value);
		return error.isPresent() ? Result.error(error.get()) : Result.success(json);
	}

	@Override
	public Result<T> decode(JsonValue value) {
		if (value instanceof JsonObject) {
			return this.mapCodec.decode(value.asObject());
		} else {
			return Result.error("Not an object");
		}
	}
}
