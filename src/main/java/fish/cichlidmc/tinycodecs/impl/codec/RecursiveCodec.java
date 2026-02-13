package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.impl.Lazy;
import fish.cichlidmc.tinyjson.value.JsonValue;

import java.util.function.UnaryOperator;

public final class RecursiveCodec<T> implements Codec<T> {
	private final Lazy<Codec<T>> lazy;

	public RecursiveCodec(UnaryOperator<Codec<T>> factory) {
		this.lazy = new Lazy<>(() -> factory.apply(this));
	}

	@Override
	public Result<? extends JsonValue> encode(T value) {
		return this.lazy.get().encode(value);
	}

	@Override
	public Result<T> decode(JsonValue json) {
		return this.lazy.get().decode(json);
	}
}
