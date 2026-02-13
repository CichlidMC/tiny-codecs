package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinycodecs.impl.Lazy;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;
import java.util.function.UnaryOperator;

public final class RecursiveMapCodec<T> implements MapCodec<T> {
	private final Lazy<MapCodec<T>> lazy;

	public RecursiveMapCodec(UnaryOperator<MapCodec<T>> factory) {
		this.lazy = new Lazy<>(() -> factory.apply(this));
	}

	@Override
	public Optional<String> encode(JsonObject json, T value) {
		return this.lazy.get().encode(json, value);
	}

	@Override
	public Result<T> decode(JsonObject json) {
		return this.lazy.get().decode(json);
	}
}
