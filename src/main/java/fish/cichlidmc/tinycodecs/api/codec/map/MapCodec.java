package fish.cichlidmc.tinycodecs.api.codec.map;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.impl.Lazy;
import fish.cichlidmc.tinycodecs.impl.codec.map.MapCodecAsCodec;
import fish.cichlidmc.tinycodecs.impl.codec.map.UnitMapCodec;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Similar to a {@link Codec}, but operates specifically on maps ({@link JsonObject}s).
 */
public interface MapCodec<T> extends MapEncoder<T>, MapDecoder<T> {
	/**
	 * Create a {@link Codec} that will deserialize a {@link JsonObject} using this MapCodec.
	 */
	default Codec<T> asCodec() {
		return new MapCodecAsCodec<>(this);
	}

	// transforms

	default MapCodec<T> validate(Function<? super T, ? extends CodecResult<T>> validator) {
		return this.flatXmap(validator, validator);
	}

	default <B> MapCodec<B> xmap(Function<? super T, ? extends B> to, Function<? super B, ? extends T> from) {
		return of(this.comap(from), this.map(to));
	}

	default <B> MapCodec<B> comapFlatMap(Function<? super T, ? extends CodecResult<? extends B>> to, Function<? super B, ? extends T> from) {
		return of(this.comap(from), this.flatMap(to));
	}

	default <B> MapCodec<B> flatComapMap(Function<? super T, ? extends B> to, Function<? super B, ? extends CodecResult<? extends T>> from) {
		return of(this.flatComap(from), this.map(to));
	}

	default <B> MapCodec<B> flatXmap(Function<? super T, ? extends CodecResult<? extends B>> to, Function<? super B, ? extends CodecResult<? extends T>> from) {
		return of(this.flatComap(from), this.flatMap(to));
	}

	// factories

	static <T> MapCodec<T> of(MapEncoder<? super T> encoder, MapDecoder<T> decoder) {
		return new MapCodec<>() {
			@Override
			public Optional<String> encode(JsonObject json, T value) {
				return encoder.encode(json, value);
			}

			@Override
			public CodecResult<T> decode(JsonObject json) {
				return decoder.decode(json);
			}
		};
	}

	static <T> MapCodec<T> unit(T unit) {
		return new UnitMapCodec<>(unit);
	}

	static <T> MapCodec<T> lazy(Supplier<? extends MapCodec<T>> factory) {
		Lazy<MapCodec<T>> lazy = new Lazy<>(factory);

		return of(
				(json, value) -> lazy.get().encode(json, value),
				json -> lazy.get().decode(json)
		);
	}
}
