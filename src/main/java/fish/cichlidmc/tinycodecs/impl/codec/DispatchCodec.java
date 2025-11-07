package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

import java.util.Optional;
import java.util.function.Function;

public final class DispatchCodec<T, TYPE> implements Codec<T> {
	private final String key;
	private final Codec<TYPE> typeCodec;
	private final Function<? super T, ? extends TYPE> typeGetter;
	private final Function<? super TYPE, ? extends MapCodec<? extends T>> codecGetter;

	public DispatchCodec(Codec<TYPE> codec, String key,
						 Function<? super T, ? extends TYPE> typeGetter,
						 Function<? super TYPE, ? extends MapCodec<? extends T>> codecGetter) {
		this.key = key;
		this.typeCodec = codec;
		this.typeGetter = typeGetter;
		this.codecGetter = codecGetter;
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		TYPE type = this.typeGetter.apply(value);

		return switch (this.typeCodec.encode(type)) {
			case CodecResult.Error(String message) -> CodecResult.error("Failed to encode type: " + message);
			case CodecResult.Success(JsonValue encoded) -> {
				MapCodec<T> codec = this.getCodec(type);

				JsonObject json = new JsonObject();
				Optional<String> error = codec.encode(json, value);
				if (error.isPresent()) {
					yield CodecResult.error("Failed to encode value: " + error.get());
				}

				json.put(this.key, encoded);
				yield CodecResult.success(json);
			}
		};
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		if (!(value instanceof JsonObject object)) {
			return CodecResult.error("Cannot get key from non-object");
		}

		JsonValue typeValue = object.getOrJsonNull(this.key);
		return switch (this.typeCodec.decode(typeValue)) {
			case CodecResult.Error(String message) -> CodecResult.error("Could not decode type: " + message);
			case CodecResult.Success(TYPE type) -> {
				MapCodec<? extends T> codec = this.codecGetter.apply(type);
				// load-bearing useless map
				yield codec.decode(object).map(Function.identity());
			}
		};
	}

	@SuppressWarnings("unchecked")
	private MapCodec<T> getCodec(TYPE type) {
		return (MapCodec<T>) this.codecGetter.apply(type);
	}
}
