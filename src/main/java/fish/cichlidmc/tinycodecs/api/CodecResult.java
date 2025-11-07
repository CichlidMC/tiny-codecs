package fish.cichlidmc.tinycodecs.api;

import org.jetbrains.annotations.Contract;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A result of a codec operation, either a success (has a value) or an error (has a message).
 */
public sealed interface CodecResult<T> {
	/**
	 * @return true if this result represents a success
	 */
	boolean isSuccess();

	/**
	 * @return true if this result represents an error
	 */
	boolean isError();

	/**
	 * Apply a predicate to the contained value.
	 * <ul>
	 *     <li>If this is an Error, nothing happens since no value is present.</li>
	 *     <li>If this is a Success and the value matches the filter, nothing happens.</li>
	 *     <li>If this is a Success and the value does <strong>not</strong> match the filter, an Error is returned with the given error message.</li>
	 * </ul>
	 * @see Optional#filter(Predicate)
	 */
	CodecResult<T> filter(Predicate<? super T> filter, String messageOnFail);

	/**
	 * Apply a function to the contained value, if present.
	 * @see Optional#map(Function)
	 */
	<B> CodecResult<B> map(Function<? super T, ? extends B> function);

	/**
	 * Apply a function to the contained value, if present.
	 * <p>
	 * The result is flattened and returned directly, instead of being double-wrapped.
	 * @see Optional#flatMap(Function)
	 */
	<B> CodecResult<B> flatMap(Function<? super T, ? extends CodecResult<B>> function);

	/**
	 * Invoke the first consumer with the contained value if present.
	 * Otherwise, invoke the second consumer with the error message.
	 * @see Optional#ifPresentOrElse(Consumer, Runnable)
	 */
	void ifPresentOrElse(Consumer<? super T> success, Consumer<? super String> error);

	/**
	 * @return the held value, if present
	 * @throws NoSuchElementException if this result represents an error and therefore holds no value
	 */
	T getOrThrow() throws NoSuchElementException;

	/**
	 * @return a new successful result holding the given value
	 */
	static <T> Success<T> success(T value) {
		return new Success<>(value);
	}

	/**
	 * @return a new error result with the given error message
	 */
	static <T> Error<T> error(String message) {
		return new Error<>(message);
	}

	/**
	 * @return a new result; an error if {@code errorMessage} is present, otherwise a success holding {@code successValue}
	 */
	static <T> CodecResult<T> of(T successValue, Optional<String> errorMessage) {
		if (errorMessage.isPresent()) {
			return error(errorMessage.get());
		} else {
			return success(successValue);
		}
	}

	/**
	 * The result of a successful operation.
	 */
	record Success<T>(T value) implements CodecResult<T> {
		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isError() {
			return false;
		}

		@Override
		public CodecResult<T> filter(Predicate<? super T> filter, String messageOnFail) {
			return filter.test(this.value) ? this : error(messageOnFail);
		}

		@Override
		public <B> Success<B> map(Function<? super T, ? extends B> function) {
			return CodecResult.success(function.apply(this.value));
		}

		public <B> CodecResult<B> flatMap(Function<? super T, ? extends CodecResult<B>> function) {
			return Objects.requireNonNull(function.apply(this.value));
		}

		@Override
		public void ifPresentOrElse(Consumer<? super T> success, Consumer<? super String> error) {
			success.accept(this.value);
		}

		@Override
		public T getOrThrow() throws NoSuchElementException {
			return this.value;
		}
	}

	/**
	 * The result of a failed operation.
	 */
	record Error<T>(String message) implements CodecResult<T> {
		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isError() {
			return true;
		}

		@Override
		@Contract("_,_->this")
		public CodecResult<T> filter(Predicate<? super T> filter, String messageOnFail) {
			return this;
		}

		@Override
		@Contract("_->this")
		public <B> Error<B> map(Function<? super T, ? extends B> function) {
			return this.cast();
		}

		@Override
		@Contract("_->this")
		public <B> Error<B> flatMap(Function<? super T, ? extends CodecResult<B>> function) {
			return this.cast();
		}

		@Override
		public void ifPresentOrElse(Consumer<? super T> success, Consumer<? super String> error) {
			error.accept(this.message);
		}

		@Override
		@Contract("->fail")
		public T getOrThrow() throws NoSuchElementException {
			throw new NoSuchElementException(this.message);
		}

		@SuppressWarnings("unchecked")
		private <B> Error<B> cast() {
			// no value is actually present, this is always safe
			return (Error<B>) this;
		}
	}
}
