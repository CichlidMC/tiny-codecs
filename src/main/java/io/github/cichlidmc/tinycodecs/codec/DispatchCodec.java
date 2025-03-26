package io.github.cichlidmc.tinycodecs.codec;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinycodecs.map.MapCodec;
import io.github.cichlidmc.tinycodecs.util.Utils;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;

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
	public CodecResult<T> decode(JsonValue value) {
		if (!(value instanceof JsonObject)) {
			return CodecResult.error("Cannot get key from non-object");
		}

		JsonObject object = value.asObject();

		JsonValue typeValue = Utils.getOrJsonNull(object, this.key);
		CodecResult<TYPE> typeResult = this.typeCodec.decode(typeValue);
		if (typeResult.isError()) {
			return CodecResult.error("Could not decode type: " + typeResult.asError().message);
		}

		TYPE type = typeResult.getOrThrow();
		MapCodec<? extends T> codec = this.codecGetter.apply(type);
		// load-bearing useless map
		return codec.decode(object).map(h -> h);
	}

	@Override
	public CodecResult<? extends JsonValue> encode(T value) {
		TYPE type = this.typeGetter.apply(value);

		CodecResult<? extends JsonValue> typeResult = this.typeCodec.encode(type);
		if (typeResult.isError()) {
			return CodecResult.error("Failed to encode type: " + typeResult.asError().message);
		}

		MapCodec<T> codec = this.getCodec(type);

		JsonObject json = new JsonObject();
		json.put(this.key, typeResult.getOrThrow());
		codec.encode(json, value);
		return CodecResult.success(json);
	}

	@SuppressWarnings("unchecked")
	private MapCodec<T> getCodec(TYPE type) {
		return (MapCodec<T>) this.codecGetter.apply(type);
	}
}
