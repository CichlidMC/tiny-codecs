package fish.cichlidmc.tinycodecs.impl.codec.optional;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.Optional;

public record OptionalCodec<T>(Codec<T> wrapped) implements Codec<Optional<T>> {
	@Override
	public CodecResult<? extends JsonValue> encode(Optional<T> value) {
		if (value.isPresent()) {
			return this.wrapped.encode(value.get());
		} else {
			return CodecResult.success(new JsonNull());
		}
	}

	@Override
	public CodecResult<Optional<T>> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return CodecResult.success(Optional.empty());
		} else {
			return this.wrapped.decode(value).map(Optional::of);
		}
	}
}
