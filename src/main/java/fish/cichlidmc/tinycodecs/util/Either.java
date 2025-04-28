package fish.cichlidmc.tinycodecs.util;

import java.util.NoSuchElementException;
import java.util.function.Function;

public interface Either<L, R> {

	default boolean isLeft() {
		return false;
	}

	default L left() {
		throw new NoSuchElementException();
	}

	default boolean isRight() {
		return false;
	}

	default R right() {
		throw new NoSuchElementException();
	}

	<L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right);

	static <L, R> Either<L, R> left(L value) {
		return new Left<>(value);
	}

	static <L, R> Either<L, R> right(R value) {
		return new Right<>(value);
	}

	static <T> T merge(Either<? extends T, ? extends T> either) {
		return either.isLeft() ? either.left() : either.right();
	}

	final class Left<L, R> implements Either<L, R> {
		private final L value;

		public Left(L value) {
			this.value = value;
		}

		@Override
		public boolean isLeft() {
			return true;
		}

		@Override
		public L left() {
			return this.value;
		}

		public <L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right) {
			return Either.left(left.apply(this.value));
		}
	}

	final class Right<L, R> implements Either<L, R> {
		private final R value;

		public Right(R value) {
			this.value = value;
		}

		@Override
		public boolean isRight() {
			return true;
		}

		@Override
		public R right() {
			return this.value;
		}

		public <L2, R2> Either<L2, R2> map(Function<? super L, ? extends L2> left, Function<? super R, ? extends R2> right) {
			return Either.right(right.apply(this.value));
		}
	}
}
