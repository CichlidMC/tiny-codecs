package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

public final class RecursiveCodec<T> implements Codec<T> {
	@Nullable
	private UnaryOperator<Codec<T>> factory;
	@Nullable
	private Codec<T> wrapped;

	public RecursiveCodec(UnaryOperator<Codec<T>> factory) {
		this.factory = factory;
	}

	@Override
	public Result<? extends JsonValue> encode(T value) {
		return this.get().encode(value);
	}

	@Override
	public Result<T> decode(JsonValue json) {
		return this.get().decode(json);
	}

	private Codec<T> get() {
		if (this.factory != null) {
			this.wrapped = this.factory.apply(this);
			this.factory = null;
		}

		return Objects.requireNonNull(this.wrapped);
	}
}
