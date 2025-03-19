package io.github.cichlidmc.tinycodecs.codec;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinyjson.value.JsonValue;

public final class AlternativeCodec<T> implements Codec<T> {
	private final Codec<T> first;
	private final Codec<T> second;

	public AlternativeCodec(Codec<T> first, Codec<T> second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public DecodeResult<T> decode(JsonValue value) {
		DecodeResult<T> firstResult = this.first.decode(value);
		if (firstResult.isSuccess())
			return firstResult;

		DecodeResult<T> secondResult = this.second.decode(value);
		if (secondResult.isSuccess())
			return secondResult;

		// both failed, merge errors
		String firstMessage = firstResult.asError().message;
		String secondMessage = secondResult.asError().message;
		return DecodeResult.error("Both formats failed to decode: " + firstMessage + "; " + secondMessage);
	}

	@Override
	public JsonValue encode(T value) {
		return this.first.encode(value);
	}
}
