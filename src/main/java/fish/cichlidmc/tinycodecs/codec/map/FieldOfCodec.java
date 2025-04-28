package fish.cichlidmc.tinycodecs.codec.map;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.map.MapCodec;
import fish.cichlidmc.tinycodecs.util.Utils;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

public class FieldOfCodec<T> implements MapCodec<T> {
	private final Codec<T> wrapped;
	private final String name;

	public FieldOfCodec(Codec<T> wrapped, String name) {
		this.wrapped = wrapped;
		this.name = name;
	}

	@Override
	public CodecResult<T> decode(JsonObject json) {
		JsonValue value = Utils.getOrJsonNull(json, this.name);
		return this.wrapped.decode(value);
	}

	@Override
	public CodecResult<JsonObject> encode(JsonObject json, T value) {
		CodecResult<? extends JsonValue> result = this.wrapped.encode(value);
		if (result.isError()) {
			return result.asError().cast();
		}

		JsonValue encoded = result.getOrThrow();
		if (!(encoded instanceof JsonNull)) {
			json.put(this.name, encoded);
		}

		return CodecResult.success(json);
	}
}
