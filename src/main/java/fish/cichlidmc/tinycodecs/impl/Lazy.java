package fish.cichlidmc.tinycodecs.impl;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Holds a supplier that is evaluated lazily and then cached.
 */
public final class Lazy<T> {
	private Supplier<? extends T> factory;
	private T value;

	public Lazy(Supplier<? extends T> factory) {
		this.factory = Objects.requireNonNull(factory);
	}

	public T get() {
		if (this.factory != null) {
			this.value = this.factory.get();
			this.factory = null;
		}

		return this.value;
	}
}
