package fish.cichlidmc.tinycodecs.codec;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ByNameCodec<T> implements Codec<T> {
	private final Function<? super T, @Nullable String> nameGetter;
	private final Function<? super String, ? extends @Nullable T> byName;

	public ByNameCodec(Function<? super T, @Nullable String> nameGetter, Function<? super String, ? extends @Nullable T> byName) {
		this.nameGetter = nameGetter;
		this.byName = byName;
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		if (!(value instanceof JsonString)) {
			return CodecResult.error("Name is not a String");
		}

		String name = value.asString().value();
		T named = this.byName.apply(name);

		return named != null ? CodecResult.success(named) : CodecResult.error("No entry named " + name);
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		String name = this.nameGetter.apply(value);
		return name != null ? CodecResult.success(new JsonString(name)) : CodecResult.error("Unknown name for entry " + value);
	}
}
