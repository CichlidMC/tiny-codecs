package fish.cichlidmc.tinycodecs.codec.optional;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

import java.util.Optional;

public final class OptionalCodec<T> implements Codec<Optional<T>> {
	private final Codec<T> wrapped;

	public OptionalCodec(Codec<T> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public CodecResult<Optional<T>> decode(JsonValue value) {
		if (value instanceof JsonNull) {
			return CodecResult.success(Optional.empty());
		} else {
			return this.wrapped.decode(value).map(Optional::of);
		}
	}

	@Override
	public CodecResult<? extends JsonValue> encode(Optional<T> value) {
		if (value.isPresent()) {
			return this.wrapped.encode(value.get());
		} else {
			return CodecResult.success(new JsonNull());
		}
	}
}
