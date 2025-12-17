package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;

public record UnitMapCodec<T>(T unit) implements MapCodec<T> {
	@Override
	public Optional<String> encode(JsonObject json, T value) {
		return Optional.empty();
	}

	@Override
	public Result<T> decode(JsonObject json) {
		return Result.success(this.unit);
	}
}
