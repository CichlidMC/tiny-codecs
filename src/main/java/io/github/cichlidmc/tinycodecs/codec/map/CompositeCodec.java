package io.github.cichlidmc.tinycodecs.codec.map;

import io.github.cichlidmc.tinycodecs.MapCodec;
import io.github.cichlidmc.tinycodecs.util.Functions.*;
import io.github.cichlidmc.tinyjson.JsonException;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Factory methods for building composite codecs, quarantined to their own class.
 */
@SuppressWarnings("DuplicatedCode")
public final class CompositeCodec {
	private CompositeCodec() {}

	public static <T, A, B> MapCodec<T> of(MapCodec<A> aCodec, F1<T, A> aGetter,
										   MapCodec<B> bCodec, F1<T, B> bGetter,
										   F2<A, B, T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			return factory.apply(a, b);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
		});
	}

	public static <T, A, B, C> MapCodec<T> of(MapCodec<A> aCodec, F1<T, A> aGetter,
											  MapCodec<B> bCodec, F1<T, B> bGetter,
											  MapCodec<C> cCodec, F1<T, C> cGetter,
											  F3<A, B, C, T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			C c = decodeField(json, cCodec);
			return factory.apply(a, b, c);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
			encodeField(json, cCodec, cGetter, value);
		});
	}

	public static <T, A, B, C, D> MapCodec<T> of(MapCodec<A> aCodec, F1<T, A> aGetter,
												 MapCodec<B> bCodec, F1<T, B> bGetter,
												 MapCodec<C> cCodec, F1<T, C> cGetter,
												 MapCodec<D> dCodec, F1<T, D> dGetter,
												 F4<A, B, C, D, T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			C c = decodeField(json, cCodec);
			D d = decodeField(json, dCodec);
			return factory.apply(a, b, c, d);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
			encodeField(json, cCodec, cGetter, value);
			encodeField(json, dCodec, dGetter, value);
		});
	}

	public static <T, A, B, C, D, E> MapCodec<T> of(MapCodec<A> aCodec, F1<T, A> aGetter,
													MapCodec<B> bCodec, F1<T, B> bGetter,
													MapCodec<C> cCodec, F1<T, C> cGetter,
													MapCodec<D> dCodec, F1<T, D> dGetter,
													MapCodec<E> eCodec, F1<T, E> eGetter,
													F5<A, B, C, D, E, T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			C c = decodeField(json, cCodec);
			D d = decodeField(json, dCodec);
			E e = decodeField(json, eCodec);
			return factory.apply(a, b, c, d, e);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
			encodeField(json, cCodec, cGetter, value);
			encodeField(json, dCodec, dGetter, value);
			encodeField(json, eCodec, eGetter, value);
		});
	}

	public static <T, A, B, C, D, E, F> MapCodec<T> of(MapCodec<A> aCodec, F1<T, A> aGetter,
													   MapCodec<B> bCodec, F1<T, B> bGetter,
													   MapCodec<C> cCodec, F1<T, C> cGetter,
													   MapCodec<D> dCodec, F1<T, D> dGetter,
													   MapCodec<E> eCodec, F1<T, E> eGetter,
													   MapCodec<F> fCodec, F1<T, F> fGetter,
													   F6<A, B, C, D, E, F, T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			C c = decodeField(json, cCodec);
			D d = decodeField(json, dCodec);
			E e = decodeField(json, eCodec);
			F f = decodeField(json, fCodec);
			return factory.apply(a, b, c, d, e, f);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
			encodeField(json, cCodec, cGetter, value);
			encodeField(json, dCodec, dGetter, value);
			encodeField(json, eCodec, eGetter, value);
			encodeField(json, fCodec, fGetter, value);
		});
	}

	private static <T> MapCodec<T> create(Function<JsonObject, T> decoder, BiConsumer<JsonObject, T> encoder) {
		return new ThrowingMapCodec<T>() {
			@Override
			public T decodeUnsafe(JsonObject json) throws JsonException {
				return decoder.apply(json);
			}

			@Override
			public void encode(JsonObject json, T value) {
				encoder.accept(json, value);
			}
		};
	}

	private static <T, F> void encodeField(JsonObject json, MapCodec<F> codec, F1<T, F> getter, T owner) {
		codec.encode(json, getter.apply(owner));
	}

	private static <T, F> F decodeField(JsonObject json, MapCodec<F> codec) {
		return codec.decode(json).getOrThrow(
				message -> new JsonException("Failed to decode field: " + message)
		);
	}
}
