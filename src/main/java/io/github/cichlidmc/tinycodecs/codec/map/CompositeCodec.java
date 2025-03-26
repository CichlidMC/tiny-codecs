package io.github.cichlidmc.tinycodecs.codec.map;

import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinycodecs.map.MapCodec;
import io.github.cichlidmc.tinycodecs.util.Functions.F1;
import io.github.cichlidmc.tinycodecs.util.Functions.F2;
import io.github.cichlidmc.tinycodecs.util.Functions.F3;
import io.github.cichlidmc.tinycodecs.util.Functions.F4;
import io.github.cichlidmc.tinycodecs.util.Functions.F5;
import io.github.cichlidmc.tinycodecs.util.Functions.F6;
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

	public static <T, A, B> MapCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
										   MapCodec<B> bCodec, F1<? super T, ? extends B> bGetter,
										   F2<? super A, ? super B, ? extends T> factory) {
		return create(json -> {
			A a = decodeField(json, aCodec);
			B b = decodeField(json, bCodec);
			return factory.apply(a, b);
		}, (json, value) -> {
			encodeField(json, aCodec, aGetter, value);
			encodeField(json, bCodec, bGetter, value);
		});
	}

	public static <T, A, B, C> MapCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
											  MapCodec<B> bCodec, F1<? super T, ? extends B> bGetter,
											  MapCodec<C> cCodec, F1<? super T, ? extends C> cGetter,
											  F3<? super A, ? super B, ? super C, ? extends T> factory) {
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

	public static <T, A, B, C, D> MapCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
												 MapCodec<B> bCodec, F1<? super T, ? extends B> bGetter,
												 MapCodec<C> cCodec, F1<? super T, ? extends C> cGetter,
												 MapCodec<D> dCodec, F1<? super T, ? extends D> dGetter,
												 F4<? super A, ? super B, ? super C, ? super D, ? extends T> factory) {
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

	public static <T, A, B, C, D, E> MapCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
													MapCodec<B> bCodec, F1<? super T, ? extends B> bGetter,
													MapCodec<C> cCodec, F1<? super T, ? extends C> cGetter,
													MapCodec<D> dCodec, F1<? super T, ? extends D> dGetter,
													MapCodec<E> eCodec, F1<? super T, ? extends E> eGetter,
													F5<? super A, ? super B, ? super C, ? super D, ? super E, ? extends T> factory) {
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

	public static <T, A, B, C, D, E, F> MapCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
													   MapCodec<B> bCodec, F1<? super T, ? extends B> bGetter,
													   MapCodec<C> cCodec, F1<? super T, ? extends C> cGetter,
													   MapCodec<D> dCodec, F1<? super T, ? extends D> dGetter,
													   MapCodec<E> eCodec, F1<? super T, ? extends E> eGetter,
													   MapCodec<F> fCodec, F1<? super T, ? extends F> fGetter,
													   F6<? super A, ? super B, ? super C, ? super D, ? super E, ? super F, ? extends T> factory) {
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

	private static <T> MapCodec<T> create(Function<? super JsonObject, ? extends T> decoder, BiConsumer<? super JsonObject, ? super T> encoder) {
		return new MapCodec<T>() {
			@Override
			public CodecResult<T> decode(JsonObject json) {
				try {
					return CodecResult.success(decoder.apply(json));
				} catch (JsonException e) {
					return CodecResult.error(e.getMessage());
				}
			}

			@Override
			public CodecResult<JsonObject> encode(JsonObject json, T value) {
				encoder.accept(json, value);
				return CodecResult.success(json);
			}
		};
	}

	private static <T, F> void encodeField(JsonObject json, MapCodec<? super F> codec, F1<T, F> getter, T owner) {
		CodecResult<JsonObject> result = codec.encode(json, getter.apply(owner));
		if (result.isError()) {
			throw new JsonException("Failed to encode field: " + result.asError().message);
		}
	}

	private static <T, F> F decodeField(JsonObject json, MapCodec<F> codec) {
		return codec.decode(json).getOrThrow(
				message -> new JsonException("Failed to decode field: " + message)
		);
	}
}
