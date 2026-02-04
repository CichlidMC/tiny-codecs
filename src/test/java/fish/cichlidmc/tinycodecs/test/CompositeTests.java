package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;
import fish.cichlidmc.tinycodecs.test.types.TestRecord;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class CompositeTests {
	@Test
	public void testRecord() {
		TestRecord record = new TestRecord(4, "test", false);
		JsonValue json = TestRecord.CODEC.codec().encode(record).valueOrThrow();

		JsonObject expected = new JsonObject()
				.put("i", 4)
				.put("s", "test")
				.put("gerald", false);

		assertEquals(expected, json);

		TestRecord decoded = TestRecord.CODEC.codec().decode(json).valueOrThrow();
		assertEquals(record, decoded);
	}

	@Test
	public void optionalFieldMissing() {
		TestRecord expected = new TestRecord(1, "h!", true);

		JsonObject json = new JsonObject()
				.put("i", 1)
				.put("s", "h!");

		TestRecord decoded = TestRecord.CODEC.codec().decode(json).valueOrThrow();
		assertEquals(expected, decoded);
	}

	@Test
	public void successfulMap() {
		map(Codec.STRING.comapFlatMap(string -> {
			try {
				return Result.success(Integer.parseInt(string));
			} catch (NumberFormatException e) {
				return Result.error(e.getMessage());
			}
		}, String::valueOf));
	}

	@Test
	public void mapWithNonStringKeys() {
		assertThrows(AssertionFailedError.class, () -> map(Codec.INT));
	}

	private static void map(Codec<Integer> keyCodec) {
		Map<Integer, String> map = Map.of(10, "a", 12, "b", 3, "c");
		JsonObject expected = new JsonObject().put("10", "a").put("12", "b").put("3", "c");

		MapCodec<Map<Integer, String>> codec = MapCodec.map(keyCodec, Codec.STRING);

		JsonObject json = new JsonObject();
		Optional<String> encodeError = codec.encode(json, map);
		encodeError.ifPresent(Assertions::fail);
		assertEquals(expected, json);

		Result<Map<Integer, String>> decodeResult = codec.decode(json);
		if (decodeResult instanceof Result.Error(String message))
			fail(message);

		assertEquals(map, decodeResult.valueOrThrow());
	}
}
