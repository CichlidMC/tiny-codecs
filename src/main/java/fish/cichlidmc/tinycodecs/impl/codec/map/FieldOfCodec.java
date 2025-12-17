package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.Optional;

public record FieldOfCodec<T>(Codec<T> wrapped, String name) implements MapCodec<T> {
	@Override
	public Optional<String> encode(JsonObject json, T value) {
		return switch (this.wrapped.encode(value)) {
			case Result.Error(String message) -> Optional.of(message);
			case Result.Success(JsonValue encoded) -> {
				if (!(encoded instanceof JsonNull)) {
					json.put(this.name, encoded);
				}

				yield Optional.empty();
			}
		};
	}

	@Override
	public Result<T> decode(JsonObject json) {
		return this.wrapped.decode(json.getOrJsonNull(this.name));
	}
}
