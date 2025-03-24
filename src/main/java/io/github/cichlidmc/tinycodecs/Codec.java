package io.github.cichlidmc.tinycodecs;

import io.github.cichlidmc.tinycodecs.util.Either;
import io.github.cichlidmc.tinyjson.value.JsonValue;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A Codec determines how an object is (de)serialized.
 * It accepts and outputs any type of JSON.
 */
public interface Codec<T> {
	/**
	 * Attempt to decode the given JsonValue.
	 * Should never throw an exception.
	 * @param value the JSON to decode. A JsonNull may indicate either a literal null or a missing field
	 */
	CodecResult<T> decode(JsonValue value);

	/**
	 * Attempt to encode the given value to JSON.
	 * Should never throw an exception.
	 */
	CodecResult<? extends JsonValue> encode(T value);

	// transforms
	
	default Codec<T> validate(Predicate<T> validator) {
		return Codecs.validate(this, validator);
	}

	default Codec<T> map(UnaryOperator<T> function) {
		return Codecs.map(this, function);
	}

	default Codec<T> mapResult(UnaryOperator<CodecResult<T>> function) {
		return Codecs.mapResult(this, function);
	}

	default Codec<T> flatMap(Function<T, CodecResult<T>> function) {
		return Codecs.flatMap(this, function);
	}

	default <B> Codec<B> xmap(Function<? super T, ? extends B> aToB, Function<? super B, ? extends T> bToA) {
		return Codecs.xmap(this, aToB, bToA);
	}

	default <B> Codec<B> flatXmap(Function<T, CodecResult<B>> aToB, Function<B, T> bToA) {
		return Codecs.flatXmap(this, aToB, bToA);
	}

	default <B> Codec<B> dispatch(String key, Function<? super B, ? extends T> typeGetter, Function<? super T, MapCodec<? extends B>> codecGetter) {
		return Codecs.dispatch(this, key, typeGetter, codecGetter);
	}

	default <B> Codec<B> dispatch(Function<? super B, ? extends T> typeGetter, Function<? super T, MapCodec<? extends B>> codecGetter) {
		return Codecs.dispatch(this, typeGetter, codecGetter);
	}

	default Codec<T> withAlternative(Codec<? extends T> alternative) {
		return Codecs.withAlternative(this, alternative);
	}

	default Codec<List<T>> listOf() {
		return Codecs.listOf(this);
	}

	default Codec<List<T>> listOrSingle() {
		return Codecs.listOrSingle(this);
	}

	default Codec<Optional<T>> optional() {
		return Codecs.optional(this);
	}

	default Codec<T> optional(Supplier<T> fallback) {
		return Codecs.optional(this, fallback);
	}

	default Codec<T> optional(T fallback) {
		return Codecs.optional(this, fallback);
	}

	default <B> Codec<Either<T, B>> xor(Codec<B> otherCodec) {
		return Codecs.xor(this, otherCodec);
	}

	default MapCodec<T> fieldOf(String name) {
		return Codecs.fieldOf(this, name);
	}
}
