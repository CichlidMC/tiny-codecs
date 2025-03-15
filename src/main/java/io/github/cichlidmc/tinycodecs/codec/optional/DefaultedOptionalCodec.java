package io.github.cichlidmc.tinycodecs.codec.optional;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.Optional;
import java.util.function.Supplier;

public final class DefaultedOptionalCodec<T> implements Codec<T> {
	private final Codec<T> wrapped;
	private final Supplier<T> fallback;
	// keep one for comparisons
	private final T cached;

	public DefaultedOptionalCodec(Codec<T> wrapped, Supplier<T> fallback) {
		this.wrapped = wrapped;
		this.fallback = fallback;
		this.cached = fallback.get();
	}

	@Override
	public DecodeResult<T> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return DecodeResult.success(this.fallback.get());
		} else {
			return this.wrapped.decode(value);
		}
	}

	@Override
	public JsonValue encode(T value) {
		if (value.equals(this.cached)) {
			return new JsonNull();
		} else {
			return this.wrapped.encode(value);
		}
	}
}
