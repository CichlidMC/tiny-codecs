package fish.cichlidmc.tinycodecs.util;

import java.util.function.Supplier;

public final class Lazy<T> {
	private Supplier<? extends T> factory;
	private T value;

	public Lazy(Supplier<? extends T> factory) {
		this.factory = factory;
	}

	public T get() {
		if (this.factory != null) {
			this.value = this.factory.get();
			this.factory = null;
		}

		return this.value;
	}
}
