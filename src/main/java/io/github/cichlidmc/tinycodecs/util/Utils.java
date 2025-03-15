package io.github.cichlidmc.tinycodecs.util;

import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNull;

public class Utils {
	public static JsonValue getOrJsonNull(JsonObject object, String key) {
		return object.getOptional(key).orElseGet(JsonNull::new);
	}
}
