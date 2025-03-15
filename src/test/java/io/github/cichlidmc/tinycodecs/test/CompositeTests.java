package io.github.cichlidmc.tinycodecs.test;

import io.github.cichlidmc.tinycodecs.test.types.TestRecord;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompositeTests {
	@Test
	public void testRecord() {
		TestRecord record = new TestRecord(4, "test", false);
		JsonValue json = TestRecord.CODEC.encode(record);

		JsonObject expected = new JsonObject()
				.put("i", 4)
				.put("s", "test")
				.put("gerald", false);

		assertEquals(expected, json);

		TestRecord decoded = TestRecord.CODEC.decode(json).getOrThrow();
		assertEquals(record, decoded);
	}

	@Test
	public void optionalFieldMissing() {
		TestRecord expected = new TestRecord(1, "h!", true);

		JsonObject json = new JsonObject()
				.put("i", 1)
				.put("s", "h!");

		TestRecord decoded = TestRecord.CODEC.decode(json).getOrThrow();
		assertEquals(expected, decoded);
	}

}
