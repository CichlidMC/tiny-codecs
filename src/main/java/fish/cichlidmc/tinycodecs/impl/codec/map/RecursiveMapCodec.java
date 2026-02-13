package fish.cichlidmc.tinycodecs.impl.codec.map;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class RecursiveMapCodec<T> implements MapCodec<T> {
	@Nullable
	private UnaryOperator<MapCodec<T>> factory;
	@Nullable
	private MapCodec<T> wrapped;

	public RecursiveMapCodec(UnaryOperator<MapCodec<T>> factory) {
		this.factory = factory;
	}

	@Override
	public Optional<String> encode(JsonObject json, T value) {
		return this.get().encode(json, value);
	}

	@Override
	public Result<T> decode(JsonObject json) {
		return this.get().decode(json);
	}

	private MapCodec<T> get() {
		if (this.factory != null) {
			this.wrapped = this.factory.apply(this);
			this.factory = null;
		}

		return Objects.requireNonNull(this.wrapped);
	}
}
