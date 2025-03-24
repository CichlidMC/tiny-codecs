package io.github.cichlidmc.tinycodecs.codec.optional;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNull;

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
	public CodecResult<T> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return CodecResult.success(this.fallback.get());
		} else {
			return this.wrapped.decode(value);
		}
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		if (value.equals(this.cached)) {
			return CodecResult.success(new JsonNull());
		} else {
			return this.wrapped.encode(value);
		}
	}
}
