package fish.cichlidmc.tinycodecs.api;

import java.util.function.Function;

/**
 * Holds one of two values: either an {@link L} or a {@link R}.
 */
public sealed interface Either<L, R> {
	/**
	 * @return true if this is a Left Either
	 */
	boolean isLeft();

	/**
	 * @return true if this is a Right Either
	 */
	boolean isRight();

	/**
	 * Apply a pair of functions to the possible values of this Either.
	 */
	<L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right);

	/**
	 * Unwrap this Either by mapping both possible values to the same type.
	 * @see #join(Either)
	 */
	<T> T join(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right);

	/**
	 * @return a new Either holding the given value on the left.
	 */
	static <L, R> Either<L, R> left(L value) {
		return new Left<>(value);
	}

	/**
	 * @return a new Either holding the given value on the right.
	 */
	static <L, R> Either<L, R> right(R value) {
		return new Right<>(value);
	}

	/**
	 * Unwrap an Either that has the same type on both sides.
	 */
	static <T> T join(Either<? extends T, ? extends T> either) {
		return either.join(Function.identity(), Function.identity());
	}

	record Left<L, R>(L value) implements Either<L, R> {
		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public boolean isRight() {
			return false;
		}

		public <L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right) {
			return Either.left(left.apply(this.value));
		}

		@Override
		public <T> T join(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right) {
			return left.apply(this.value);
		}
	}

	record Right<L, R>(R value) implements Either<L, R> {
		@Override
		public boolean isLeft() {
			return false;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		public <L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right) {
			return Either.right(right.apply(this.value));
		}

		@Override
		public <T> T join(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right) {
			return right.apply(this.value);
		}
	}
}
