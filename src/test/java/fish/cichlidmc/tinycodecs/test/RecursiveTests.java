package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.tinycodecs.test.types.Recursive;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class RecursiveTests {
	@Test
	public void single() {
		Recursive single = new Recursive("gerald");
		JsonValue json = Recursive.CODEC.encode(single).valueOrThrow();

		JsonObject expected = new JsonObject().put("name", "gerald");
		assertEquals(expected, json);

		Recursive decoded = Recursive.CODEC.decode(json).valueOrThrow();
		assertEquals(single, decoded);
	}

	@Test
	public void nested() {
		Recursive nested = new Recursive("a", new Recursive("b", new Recursive("c")));
		JsonValue json = Recursive.CODEC.encode(nested).valueOrThrow();

		JsonObject expected = new JsonObject()
				.put("name", "a")
				.put("next", new JsonObject()
						.put("name", "b")
						.put("next", new JsonObject()
								.put("name", "c")
						)
				);

		assertEquals(expected, json);

		Recursive decoded = Recursive.CODEC.decode(json).valueOrThrow();
		assertEquals(nested, decoded);
	}
}
