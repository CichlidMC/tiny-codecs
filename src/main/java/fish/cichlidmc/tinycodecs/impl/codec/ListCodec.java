package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonArray;

import java.util.ArrayList;
import java.util.List;

public record ListCodec<T>(Codec<T> elementCodec) implements Codec<List<T>> {
	@Override
	public CodecResult<? extends JsonValue> encode(List<T> value) {
		JsonArray array = new JsonArray();
		for (T entry : value) {
			switch (this.elementCodec.encode(entry)) {
				case CodecResult.Error(String message) -> {
					return CodecResult.error("Failed to encode entry: " + message);
				}
				case CodecResult.Success(JsonValue encoded) -> array.add(encoded);
			}
		}

		return CodecResult.success(array);
	}

	@Override
	public CodecResult<List<T>> decode(JsonValue value) {
		if (!(value instanceof JsonArray array)) {
			return CodecResult.error("Not a list: " + value);
		}

		if (array.size() == 0) {
			return CodecResult.success(List.of());
		} else if (array.size() == 1) {
			return this.elementCodec.decode(array.get(0)).map(List::of);
		}

		List<T> decoded = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		for (JsonValue element : array) {
			this.elementCodec.decode(element).ifPresentOrElse(decoded::add, errors::add);
		}

		if (errors.isEmpty()) {
			return CodecResult.success(decoded);
		}

		StringBuilder fullError = new StringBuilder("Failed to decode list: ");
		errors.forEach(error -> fullError.append(error).append("; "));
		return CodecResult.error(fullError.toString().trim());
	}
}
