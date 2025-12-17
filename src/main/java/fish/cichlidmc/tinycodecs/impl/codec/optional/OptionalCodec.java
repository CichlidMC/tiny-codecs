package fish.cichlidmc.tinycodecs.impl.codec.optional;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.Optional;

public record OptionalCodec<T>(Codec<T> wrapped) implements Codec<Optional<T>> {
	@Override
	public Result<? extends JsonValue> encode(Optional<T> value) {
		if (value.isPresent()) {
			return this.wrapped.encode(value.get());
		} else {
			return Result.success(new JsonNull());
		}
	}

	@Override
	public Result<Optional<T>> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return Result.success(Optional.empty());
		} else {
			return this.wrapped.decode(value).map(Optional::of);
		}
	}
}
