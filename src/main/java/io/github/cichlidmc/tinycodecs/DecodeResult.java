package io.github.cichlidmc.tinycodecs;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A result of a decode attempt, either a success (has a value) or an error (has a message).
 */
public abstract class DecodeResult<T> {
	private DecodeResult() {
	}

	public abstract boolean isSuccess();

	public boolean isError() {
		return !this.isSuccess();
	}

	public DecodeResult.Success<T> asSuccess() {
		throw new UnsupportedOperationException();
	}

	public DecodeResult.Error<T> asError() {
		throw new UnsupportedOperationException();
	}

	public T getOrThrow() {
		return this.getOrThrow(error -> new NoSuchElementException("Failed to decode: " + error));
	}

	public abstract <X extends Throwable> T getOrThrow(Function<String, X> exceptionFactory) throws X;

	public abstract Optional<T> asOptional();

	public abstract DecodeResult<T> filter(Predicate<T> filter, String messageOnFail);

	public abstract <B> DecodeResult<B> map(Function<T, B> function);

	public abstract <B> DecodeResult<B> flatMap(Function<T, DecodeResult<B>> function);

	public abstract void ifPresentOrElse(Consumer<T> success, Consumer<String> error);

	public static <T> DecodeResult<T> success(T value) {
		return new Success<>(value);
	}

	public static <T> DecodeResult<T> error(String message) {
		return new Error<>(message);
	}

	public static class Success<T> extends DecodeResult<T> {
		public final T value;

		public Success(T value) {
			this.value = value;
		}

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public Success<T> asSuccess() {
			return this;
		}

		@Override
		public <X extends Throwable> T getOrThrow(Function<String, X> exceptionFactory) throws X {
			return this.value;
		}

		@Override
		public Optional<T> asOptional() {
			return Optional.of(this.value);
		}

		@Override
		public DecodeResult<T> filter(Predicate<T> filter, String messageOnFail) {
			return filter.test(this.value) ? this : error(messageOnFail);
		}

		@Override
		public <A> DecodeResult<A> map(Function<T, A> function) {
			return success(function.apply(this.value));
		}

		@Override
		public <B> DecodeResult<B> flatMap(Function<T, DecodeResult<B>> function) {
			return function.apply(this.value);
		}

		@Override
		public void ifPresentOrElse(Consumer<T> success, Consumer<String> error) {
			success.accept(this.value);
		}
	}

	public static class Error<T> extends DecodeResult<T> {
		public final String message;

		public Error(String message) {
			this.message = message;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public Error<T> asError() {
			return this;
		}

		@Override
		public <X extends Throwable> T getOrThrow(Function<String, X> exceptionFactory) throws X {
			throw exceptionFactory.apply(this.message);
		}

		@Override
		public Optional<T> asOptional() {
			return Optional.empty();
		}

		@Override
		public DecodeResult<T> filter(Predicate<T> filter, String messageOnFail) {
			return this;
		}

		@Override
		public <A> DecodeResult<A> map(Function<T, A> function) {
			return error(this.message);
		}

		@Override
		public <B> DecodeResult<B> flatMap(Function<T, DecodeResult<B>> function) {
			return error(this.message);
		}

		@Override
		public void ifPresentOrElse(Consumer<T> success, Consumer<String> error) {
			error.accept(this.message);
		}
	}
}
