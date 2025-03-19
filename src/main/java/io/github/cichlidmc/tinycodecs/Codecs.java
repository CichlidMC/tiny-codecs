package io.github.cichlidmc.tinycodecs;

import io.github.cichlidmc.tinycodecs.codec.AlternativeCodec;
import io.github.cichlidmc.tinycodecs.codec.ByNameCodec;
import io.github.cichlidmc.tinycodecs.codec.map.CompositeCodec;
import io.github.cichlidmc.tinycodecs.codec.DispatchCodec;
import io.github.cichlidmc.tinycodecs.codec.map.FieldOfCodec;
import io.github.cichlidmc.tinycodecs.codec.ListCodec;
import io.github.cichlidmc.tinycodecs.codec.map.MapCodecAsCodec;
import io.github.cichlidmc.tinycodecs.codec.ThrowingCodec;
import io.github.cichlidmc.tinycodecs.codec.optional.DefaultedOptionalCodec;
import io.github.cichlidmc.tinycodecs.codec.optional.OptionalCodec;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.primitive.JsonBool;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNumber;
import io.github.cichlidmc.tinyjson.value.primitive.JsonString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Common codecs, factories, and transforms.
 * @see CompositeCodec
 */
public interface Codecs {
	// common codecs

	Codec<Boolean> BOOL = simpleThrowing(json -> json.asBoolean().value(), JsonBool::new);
	Codec<String> STRING = simpleThrowing(json -> json.asString().value(), JsonString::new);
	Codec<Byte> BYTE = number(Number::byteValue);
	Codec<Integer> INT = number(Number::intValue);
	Codec<Long> LONG = number(Number::longValue);
	Codec<Float> FLOAT = number(Number::floatValue);
	Codec<Double> DOUBLE = number(Number::doubleValue);

	// factories

	static <T> Codec<T> simpleThrowing(Function<JsonValue, T> decoder, Function<T, JsonValue> encoder) {
		return new ThrowingCodec<T>() {
			@Override
			public T decodeUnsafe(JsonValue value) {
				return decoder.apply(value);
			}

			@Override
			public JsonValue encode(T value) {
				return encoder.apply(value);
			}
		};
	}

	static <T> Codec<T> simple(Function<JsonValue, DecodeResult<T>> decoder, Function<T, JsonValue> encoder) {
		return new Codec<T>() {
			@Override
			public DecodeResult<T> decode(JsonValue value) {
				return decoder.apply(value);
			}

			@Override
			public JsonValue encode(T value) {
				return encoder.apply(value);
			}
		};
	}

	static <T extends Number> Codec<T> number(Function<Number, T> decoder) {
		return simpleThrowing(
				json -> decoder.apply(json.asNumber().strictValue()),
				number -> new JsonNumber(number.doubleValue())
		);
	}

	static <T> Codec<T> byName(Function<T, String> nameGetter, Function<String, T> byName) {
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

	// transforms for existing codecs

	static <T> Codec<T> validate(Codec<T> codec, Predicate<T> validator) {
		return mapResult(codec, result -> result.filter(validator, "Validation failed"));
	}

	static <T> Codec<T> map(Codec<T> codec, UnaryOperator<T> function) {
		return simple(json -> codec.decode(json).map(function), codec::encode);
	}

	static <T> Codec<T> mapResult(Codec<T> codec, UnaryOperator<DecodeResult<T>> function) {
		return simple(json -> function.apply(codec.decode(json)), codec::encode);
	}

	static <T> Codec<T> flatMap(Codec<T> codec, Function<T, DecodeResult<T>> function) {
		return mapResult(codec, result -> result.flatMap(function));
	}

	static <A, B> Codec<B> xmap(Codec<A> codec, Function<A, B> aToB, Function<B, A> bToA) {
		return simple(json -> codec.decode(json).map(aToB), b -> codec.encode(bToA.apply(b)));
	}

	static <A, B> Codec<B> flatXmap(Codec<A> codec, Function<A, DecodeResult<B>> aToB, Function<B, A> bToA) {
		return simple(json -> codec.decode(json).flatMap(aToB), b -> codec.encode(bToA.apply(b)));
	}

	static <A, B> Codec<B> dispatch(Codec<A> codec, String key,
									Function<? super B, ? extends A> typeGetter,
									Function<? super A, MapCodec<? extends B>> codecGetter) {
		return new DispatchCodec<>(codec, key, typeGetter, codecGetter);
	}

	static <A, B> Codec<B> dispatch(Codec<A> codec, Function<? super B, ? extends A> typeGetter,
									Function<? super A, MapCodec<? extends B>> codecGetter) {
		return dispatch(codec, "type", typeGetter, codecGetter);
	}

	static <T> Codec<T> withAlternative(Codec<T> first, Codec<T> second) {
		return new AlternativeCodec<>(first, second);
	}

	static <T> Codec<List<T>> listOf(Codec<T> elementCodec) {
		return new ListCodec<>(elementCodec);
	}

	static <T> Codec<List<T>> listOrSingle(Codec<T> elementCodec) {
		Codec<List<T>> singleton = elementCodec.xmap(Collections::singletonList, list -> list.get(0));
		return withAlternative(elementCodec.listOf(), singleton);
	}

	static <T> Codec<Optional<T>> optional(Codec<T> codec) {
		return new OptionalCodec<>(codec);
	}

	static <T> Codec<T> optional(Codec<T> codec, Supplier<T> fallback) {
		return new DefaultedOptionalCodec<>(codec, fallback);
	}

	static <T> Codec<T> optional(Codec<T> codec, T fallback) {
		Supplier<T> supplier = () -> fallback;
		return optional(codec, supplier);
	}

	static <T> MapCodec<T> fieldOf(Codec<T> codec, String name) {
		return new FieldOfCodec<>(codec, name);
	}

	static <T> Codec<T> asCodec(MapCodec<T> mapCodec) {
		return new MapCodecAsCodec<>(mapCodec);
	}
}
