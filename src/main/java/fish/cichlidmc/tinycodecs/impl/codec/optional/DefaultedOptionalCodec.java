package fish.cichlidmc.tinycodecs.impl.codec.optional;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.function.Supplier;

public final class DefaultedOptionalCodec<T> implements Codec<T> {
	private final Codec<T> wrapped;
	private final Supplier<? extends T> fallback;
	// keep one for comparisons
	private final T cached;

	public DefaultedOptionalCodec(Codec<T> wrapped, Supplier<? extends T> fallback) {
		this.wrapped = wrapped;
		this.fallback = fallback;
		this.cached = fallback.get();
	}

	@Override
	public Result<? extends JsonValue> encode(T value) {
		if (value.equals(this.cached)) {
			return Result.success(new JsonNull());
		} else {
			return this.wrapped.encode(value);
		}
	}

	@Override
	public Result<T> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return Result.success(this.fallback.get());
		} else {
			return this.wrapped.decode(value);
		}
	}
}
