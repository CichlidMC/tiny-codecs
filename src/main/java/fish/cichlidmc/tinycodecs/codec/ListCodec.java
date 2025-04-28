package fish.cichlidmc.tinycodecs.codec;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListCodec<T> implements Codec<List<T>> {
	private final Codec<T> elementCodec;

	public ListCodec(Codec<T> elementCodec) {
		this.elementCodec = elementCodec;
	}

	@Override
	public CodecResult<List<T>> decode(JsonValue value) {
		if (!(value instanceof JsonArray)) {
			return CodecResult.error("Not a list: " + value);
		}

		JsonArray array = value.asArray();
		if (array.size() == 0) {
			return CodecResult.success(Collections.emptyList());
		} else if (array.size() == 1) {
			return this.elementCodec.decode(array.get(0)).map(Collections::singletonList);
		}

		List<T> decoded = new ArrayList<>();
		List<String> errors = new ArrayList<>();

		for (JsonValue element : array) {
			this.elementCodec.decode(element).ifPresentOrElse(
					decoded::add, errors::add
			);
		}

		if (errors.isEmpty()) {
			return CodecResult.success(decoded);
		}

		StringBuilder fullError = new StringBuilder("Failed to decode list: ");
		errors.forEach(error -> fullError.append(error).append("; "));
		return CodecResult.error(fullError.toString().trim());
	}

	@Override
	public CodecResult<? extends JsonValue> encode(List<T> value) {
		JsonArray array = new JsonArray();
		for (T entry : value) {
			CodecResult<? extends JsonValue> result = this.elementCodec.encode(entry);
			if (result.isError()) {
				return CodecResult.error("Failed to encode entry: " + result.asError().message);
			}

			array.add(result.getOrThrow());
		}

		return CodecResult.success(array);
	}
}
