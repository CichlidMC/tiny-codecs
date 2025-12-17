package fish.cichlidmc.tinycodecs.api.codec.dual;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinycodecs.impl.Lazy;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Both a {@link Codec} and a {@link MapCodec} for the same type. Allows for easily transforming the two at the same time.
 * @see CompositeCodec
 */
public record DualCodec<T>(Codec<T> codec, MapCodec<T> mapCodec) {
	public DualCodec<T> validate(Function<? super T, ? extends Result<T>> validator) {
		return this.flatXmap(validator, validator);
	}

	public <B> DualCodec<B> xmap(Function<? super T, ? extends B> to, Function<? super B, ? extends T> from) {
		return new DualCodec<>(this.codec.xmap(to, from), this.mapCodec.xmap(to, from));
	}

	public <B> DualCodec<B> comapFlatMap(Function<? super T, ? extends Result<? extends B>> to, Function<? super B, ? extends T> from) {
		return new DualCodec<>(this.codec.comapFlatMap(to, from), this.mapCodec.comapFlatMap(to, from));
	}

	public <B> DualCodec<B> flatComapMap(Function<? super T, ? extends B> to, Function<? super B, ? extends Result<? extends T>> from) {
		return new DualCodec<>(this.codec.flatComapMap(to, from), this.mapCodec.flatComapMap(to, from));
	}

	public <B> DualCodec<B> flatXmap(Function<? super T, ? extends Result<? extends B>> to, Function<? super B, ? extends Result<? extends T>> from) {
		return new DualCodec<>(this.codec.flatXmap(to, from), this.mapCodec.flatXmap(to, from));
	}

	// factories

	public static <T> DualCodec<T> unit(T unit) {
		return new DualCodec<>(Codec.unit(unit), MapCodec.unit(unit));
	}

	public static <T> DualCodec<T> lazy(Supplier<DualCodec<T>> factory) {
		Lazy<DualCodec<T>> lazy = new Lazy<>(factory);
		return new DualCodec<>(Codec.lazy(() -> lazy.get().codec()), MapCodec.lazy(() -> lazy.get().mapCodec()));
	}
}
