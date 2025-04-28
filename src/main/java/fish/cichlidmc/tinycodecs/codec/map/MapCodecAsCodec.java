package fish.cichlidmc.tinycodecs.codec.map;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.map.MapCodec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;

public class MapCodecAsCodec<T> implements Codec<T> {
	private final MapCodec<T> mapCodec;

	public MapCodecAsCodec(MapCodec<T> mapCodec) {
		this.mapCodec = mapCodec;
	}

	@Override
	public CodecResult<T> decode(JsonValue value) {
		if (value instanceof JsonObject) {
			return this.mapCodec.decode(value.asObject());
		} else {
			return CodecResult.error("Not an object");
		}
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		JsonObject json = new JsonObject();
		return this.mapCodec.encode(json, value);
	}
}
