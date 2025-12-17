package fish.cichlidmc.tinycodecs.api.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.Functions.F1;
import fish.cichlidmc.tinycodecs.api.Functions.F2;
import fish.cichlidmc.tinycodecs.api.Functions.F3;
import fish.cichlidmc.tinycodecs.api.Functions.F4;
import fish.cichlidmc.tinycodecs.api.Functions.F5;
import fish.cichlidmc.tinycodecs.api.Functions.F6;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.JsonException;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Factory methods for creating codecs by combining others into a single object, quarantined to their own class.
 */
@SuppressWarnings("DuplicatedCode")
public final class CompositeCodec {
	private CompositeCodec() {}
	
	public static <T, A, B> DualCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
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

	public static <T, A, B, C> DualCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
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

	public static <T, A, B, C, D> DualCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
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

	public static <T, A, B, C, D, E> DualCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
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

	public static <T, A, B, C, D, E, F> DualCodec<T> of(MapCodec<A> aCodec, F1<? super T, ? extends A> aGetter,
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

	private static <T> DualCodec<T> create(Function<? super JsonObject, ? extends T> decoder, BiConsumer<? super JsonObject, ? super T> encoder) {
		MapCodec<T> mapCodec = MapCodec.of(
				(json, value) -> {
					encoder.accept(json, value);
					return Optional.empty();
				}, json -> {
					try {
						return Result.success(decoder.apply(json));
					} catch (JsonException e) {
						return Result.error(e.getMessage());
					}
				}
		);

		return new DualCodec<>(mapCodec.asCodec(), mapCodec);
	}

	private static <T, F> void encodeField(JsonObject json, MapCodec<? super F> codec, F1<T, F> getter, T owner) {
		codec.encode(json, getter.apply(owner)).ifPresent(error -> {
			throw new JsonException("Failed to encode field: " + error);
		});
	}

	private static <F> F decodeField(JsonObject json, MapCodec<F> codec) {
		switch (codec.decode(json)) {
			case Result.Success(F value) -> { return value; }
			case Result.Error(String message) -> throw new JsonException("Failed to decode field: " + message);
		}
	}
}
