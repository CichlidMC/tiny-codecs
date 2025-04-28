package fish.cichlidmc.tinycodecs.util;

import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonNull;

public class Utils {
	public static JsonValue getOrJsonNull(JsonObject object, String key) {
		return object.getOptional(key).orElseGet(JsonNull::new);
	}
}
