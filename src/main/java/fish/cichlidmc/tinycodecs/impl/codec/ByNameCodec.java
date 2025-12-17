package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public final class ByNameCodec<T> implements Codec<T> {
	private final Function<? super T, @Nullable String> nameGetter;
	private final Function<? super String, ? extends @Nullable T> byName;

	public ByNameCodec(Function<? super T, @Nullable String> nameGetter, Function<? super String, ? extends @Nullable T> byName) {
		this.nameGetter = nameGetter;
		this.byName = byName;
	}

	@Override
	public Result<? extends JsonValue> encode(T value) {
		String name = this.nameGetter.apply(value);
		return name != null ? Result.success(new JsonString(name)) : Result.error("Unknown name for entry " + value);
	}

	@Override
	public Result<T> decode(JsonValue value) {
		if (!(value instanceof JsonString string)) {
			return Result.error("Name is not a String");
		}

		String name = string.value();
		T named = this.byName.apply(name);

		return named != null ? Result.success(named) : Result.error("No entry named " + name);
	}
}
