package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public record MapMapCodec<K, V, M extends Map<K, V>>(Supplier<M> mapFactory, Codec<K> keyCodec, Codec<V> valueCodec) implements MapCodec<M> {
	@Override
	public Optional<String> encode(JsonObject json, M map) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			Result<? extends JsonValue> keyResult = this.keyCodec.encode(entry.getKey());
			if (keyResult instanceof Result.Error(String message)) {
				return Optional.of("Failed to encode key: " + message);
			}

			if (!(keyResult.valueOrThrow() instanceof JsonString key)) {
				return Optional.of("Map key must encode to a string: " + entry.getKey());
			}

			Result<? extends JsonValue> valueResult = this.valueCodec.encode(entry.getValue());
			if (valueResult instanceof Result.Error(String message)) {
				return Optional.of("Failed to encode value: " + message);
			}

			json.put(key.value(), valueResult.valueOrThrow());
		}

		return Optional.empty();
	}

	@Override
	public Result<M> decode(JsonObject json) {
		M map = this.mapFactory.get();

		for (Map.Entry<String, JsonValue> entry : json.entrySet()) {
			Result<K> keyResult = this.keyCodec.decode(new JsonString(entry.getKey()));
			if (keyResult instanceof Result.Error(String message)) {
				return Result.error("Failed to decode key: " + message);
			}

			Result<V> valueResult = this.valueCodec.decode(entry.getValue());
			if (valueResult instanceof Result.Error(String message)) {
				return Result.error("Failed to decode value: " + message);
			}

			map.put(keyResult.valueOrThrow(), valueResult.valueOrThrow());
		}

		return Result.success(map);
	}
}
