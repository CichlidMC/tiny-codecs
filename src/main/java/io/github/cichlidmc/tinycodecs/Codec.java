package io.github.cichlidmc.tinycodecs;

import io.github.cichlidmc.tinycodecs.codec.ByNameCodec;
import io.github.cichlidmc.tinycodecs.codec.DispatchCodec;
import io.github.cichlidmc.tinycodecs.codec.EitherCodec;
import io.github.cichlidmc.tinycodecs.codec.ListCodec;
import io.github.cichlidmc.tinycodecs.codec.UnitCodec;
import io.github.cichlidmc.tinycodecs.codec.map.CompositeCodec;
import io.github.cichlidmc.tinycodecs.codec.map.FieldOfCodec;
import io.github.cichlidmc.tinycodecs.codec.optional.DefaultedOptionalCodec;
import io.github.cichlidmc.tinycodecs.codec.optional.OptionalCodec;
import io.github.cichlidmc.tinycodecs.map.MapCodec;
import io.github.cichlidmc.tinycodecs.util.Either;
import io.github.cichlidmc.tinyjson.JsonException;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.primitive.JsonBool;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNumber;
import io.github.cichlidmc.tinyjson.value.primitive.JsonString;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A Codec is both an encoder and decoder of some type of object.
 * @see CompositeCodec
 */
public interface Codec<T> extends Encoder<T>, Decoder<T> {
	// base implementations
	Codec<Boolean> BOOL = throwing(json -> json.asBoolean().value(), JsonBool::new);
	Codec<String> STRING = throwing(json -> json.asString().value(), JsonString::new);
	Codec<Byte> BYTE = number(Number::byteValue);
	Codec<Short> SHORT = number(Number::shortValue);
	Codec<Integer> INT = number(Number::intValue);
	Codec<Long> LONG = number(Number::longValue);
	Codec<Float> FLOAT = number(Number::floatValue);
	Codec<Double> DOUBLE = number(Number::doubleValue);

	// transforms

	default Codec<T> validate(Function<? super T, ? extends CodecResult<T>> validator) {
		return this.flatXmap(validator, validator);
	}

	default <B> Codec<B> xmap(Function<? super T, ? extends B> to, Function<? super B, ? extends T> from) {
		return Codec.of(this.comap(from), this.map(to));
	}

	default <B> Codec<B> comapFlatMap(Function<? super T, ? extends CodecResult<? extends B>> to, Function<? super B, ? extends T> from) {
		return Codec.of(this.comap(from), this.flatMap(to));
	}

	default <B> Codec<B> flatComapMap(Function<? super T, ? extends B> to, Function<? super B, ? extends CodecResult<? extends T>> from) {
		return Codec.of(this.flatComap(from), this.map(to));
	}

	default <B> Codec<B> flatXmap(Function<? super T, ? extends CodecResult<? extends B>> to, Function<? super B, ? extends CodecResult<? extends T>> from) {
		return Codec.of(this.flatComap(from), this.flatMap(to));
	}

	default <B> Codec<B> dispatch(String key, Function<? super B, ? extends T> typeGetter, Function<? super T, ? extends MapCodec<? extends B>> codecGetter) {
		return new DispatchCodec<>(this, key, typeGetter, codecGetter);
	}

	default <B> Codec<B> dispatch(Function<? super B, ? extends T> typeGetter, Function<? super T, MapCodec<? extends B>> codecGetter) {
		return this.dispatch("type", typeGetter, codecGetter);
	}

	default Codec<T> withAlternative(Codec<? extends T> alternative) {
		return this.either(alternative).xmap(Either::merge, Either::left);
	}

	default Codec<List<T>> listOf() {
		return new ListCodec<>(this);
	}

	default Codec<List<T>> listOrSingle() {
		return this.listOf().withAlternative(this.flatComapMap(
				Collections::singletonList,
				list -> list.size() == 1 ? CodecResult.success(list.get(0)) : CodecResult.error("Not a singleton")
		));
	}

	default Codec<Optional<T>> optional() {
		return new OptionalCodec<>(this);
	}

	default Codec<T> optional(Supplier<T> fallback) {
		return new DefaultedOptionalCodec<>(this, fallback);
	}

	default Codec<T> optional(T fallback) {
		return this.optional(() -> fallback);
	}

	default <B> Codec<Either<T, B>> either(Codec<B> other) {
		return new EitherCodec<>(this, other, false);
	}

	default <B> Codec<Either<T, B>> xor(Codec<B> other) {
		return new EitherCodec<>(this, other, true);
	}

	default MapCodec<T> fieldOf(String name) {
		return new FieldOfCodec<>(this, name);
	}

	// factories

	static <T> Codec<T> of(Encoder<? super T> encoder, Decoder<T> decoder) {
		return new Codec<T>() {
			@Override
			public CodecResult<T> decode(JsonValue json) {
				return decoder.decode(json);
			}

			@Override
			public CodecResult<? extends JsonValue> encode(T value) {
				return encoder.encode(value);
			}
		};
	}

	/**
	 * Create a codec from a function that may throw a {@link JsonException}.
	 */
	static <T> Codec<T> throwing(Function<? super JsonValue, ? extends T> decoder, Function<? super T, ? extends JsonValue> encoder) {
		return new Codec<T>() {
			@Override
			public CodecResult<T> decode(JsonValue json) {
				try {
					return CodecResult.success(decoder.apply(json));
				} catch (JsonException e) {
					return CodecResult.error(e.getMessage());
				}
			}

			@Override
			public CodecResult<? extends JsonValue> encode(T value) {
				return CodecResult.success(encoder.apply(value));
			}
		};
	}

	static <T extends Number> Codec<T> number(Function<Number, T> decoder) {
		return throwing(
				json -> decoder.apply(json.asNumber().strictValue()),
				number -> new JsonNumber(number.doubleValue())
		);
	}

	static <T> Codec<T> byName(Function<? super T, @Nullable String> nameGetter, Function<? super String, ? extends @Nullable T> byName) {
		return new ByNameCodec<>(nameGetter, byName);
	}

	static <T extends Enum<T>> Codec<T> byName(Class<T> clazz, Function<T, String> nameGetter) {
		T[] values = clazz.getEnumConstants();
		Map<String, T> byName = new HashMap<>();
		for (T value : values) {
			byName.put(nameGetter.apply(value), value);
		}
		return byName(nameGetter, byName::get);
	}

	static <T extends Enum<T>> Codec<T> byName(Class<T> clazz) {
		return byName(clazz, Enum::name);
	}

	static <T> Codec<T> unit(T unit) {
		return new UnitCodec<>(unit);
	}

	static <T> Codec<T> codecDispatch(Codec<MapCodec<? extends T>> codec, Function<? super T, ? extends MapCodec<? extends T>> getter) {
		return codec.dispatch(getter, Function.identity());
	}
}
