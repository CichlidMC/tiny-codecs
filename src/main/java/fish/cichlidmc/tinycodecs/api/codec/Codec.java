package fish.cichlidmc.tinycodecs.api.codec;

import fish.cichlidmc.fishflakes.api.Either;
import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinycodecs.impl.Lazy;
import fish.cichlidmc.tinycodecs.impl.codec.ByNameCodec;
import fish.cichlidmc.tinycodecs.impl.codec.DispatchCodec;
import fish.cichlidmc.tinycodecs.impl.codec.EitherCodec;
import fish.cichlidmc.tinycodecs.impl.codec.ListCodec;
import fish.cichlidmc.tinycodecs.impl.codec.UnitCodec;
import fish.cichlidmc.tinycodecs.impl.codec.map.FieldOfCodec;
import fish.cichlidmc.tinycodecs.impl.codec.optional.DefaultedOptionalCodec;
import fish.cichlidmc.tinycodecs.impl.codec.optional.OptionalCodec;
import fish.cichlidmc.tinyjson.JsonException;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonBool;
import fish.cichlidmc.tinyjson.value.primitive.JsonNumber;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/// A Codec is both an encoder and decoder of some type of object.
/// @see CompositeCodec
/// @see MapCodec
/// @see DualCodec
public interface Codec<T> extends Encoder<T>, Decoder<T> {
	// base implementations
	Codec<Boolean> BOOL = throwing(JsonBool::new, json -> json.asBoolean().value());
	Codec<String> STRING = throwing(JsonString::new, json -> json.asString().value());
	Codec<Byte> BYTE = number(Number::byteValue);
	Codec<Short> SHORT = number(Number::shortValue);
	Codec<Integer> INT = number(Number::intValue);
	Codec<Long> LONG = number(Number::longValue);
	Codec<Float> FLOAT = number(Number::floatValue);
	Codec<Double> DOUBLE = number(Number::doubleValue);

	// transforms

	default Codec<T> validate(Function<? super T, ? extends Result<T>> validator) {
		return this.flatXmap(validator, validator);
	}

	default <B> Codec<B> xmap(Function<? super T, ? extends B> to, Function<? super B, ? extends T> from) {
		return Codec.of(this.comap(from), this.map(to));
	}

	default <B> Codec<B> comapFlatMap(Function<? super T, ? extends Result<? extends B>> to, Function<? super B, ? extends T> from) {
		return Codec.of(this.comap(from), this.flatMap(to));
	}

	default <B> Codec<B> flatComapMap(Function<? super T, ? extends B> to, Function<? super B, ? extends Result<? extends T>> from) {
		return Codec.of(this.flatComap(from), this.map(to));
	}

	default <B> Codec<B> flatXmap(Function<? super T, ? extends Result<? extends B>> to, Function<? super B, ? extends Result<? extends T>> from) {
		return Codec.of(this.flatComap(from), this.flatMap(to));
	}

	default <B> Codec<B> dispatch(String key, Function<? super B, ? extends T> typeGetter, Function<? super T, ? extends MapCodec<? extends B>> codecGetter) {
		return new DispatchCodec<>(this, key, typeGetter, codecGetter);
	}

	default <B> Codec<B> dispatch(Function<? super B, ? extends T> typeGetter, Function<? super T, MapCodec<? extends B>> codecGetter) {
		return this.dispatch("type", typeGetter, codecGetter);
	}

	default Codec<T> withAlternative(Codec<? extends T> alternative) {
		return this.either(alternative).xmap(Either::join, Either::left);
	}

	default Codec<List<T>> listOf() {
		return new ListCodec<>(this);
	}

	default Codec<List<T>> listOrSingle() {
		return this.listOf().withAlternative(this.flatComapMap(
				List::of,
				list -> list.size() == 1 ? Result.success(list.getFirst()) : Result.error("Not a singleton")
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
		return new Codec<>() {
			@Override
			public Result<? extends JsonValue> encode(T value) {
				return encoder.encode(value);
			}

			@Override
			public Result<T> decode(JsonValue json) {
				return decoder.decode(json);
			}
		};
	}

	/// Create a codec from a pair of functions, where the decoder may throw a [JsonException].
	static <T> Codec<T> throwing(Function<? super T, ? extends JsonValue> encoder, Function<? super JsonValue, ? extends T> decoder) {
		return of(
				value -> Result.success(encoder.apply(value)),
				json -> {
					try {
						return Result.success(decoder.apply(json));
					} catch (JsonException e) {
						return Result.error(e.getMessage());
					}
				}
		);
	}

	static <T> Codec<T> lazy(Supplier<? extends Codec<T>> factory) {
		Lazy<Codec<T>> lazy = new Lazy<>(factory);

		return of(
				value -> lazy.get().encode(value),
				json -> lazy.get().decode(json)
		);
	}

	static <T extends Number> Codec<T> number(Function<Number, T> decoder) {
		return throwing(
				number -> new JsonNumber(number.doubleValue()),
				json -> decoder.apply(json.asNumber().strictValue())
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
