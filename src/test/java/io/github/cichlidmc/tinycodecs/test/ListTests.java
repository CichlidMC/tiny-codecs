package io.github.cichlidmc.tinycodecs.test;

import io.github.cichlidmc.tinycodecs.test.types.TestRecord;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonArray;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListTests {
	@Test
	public void emptyList() {
		List<TestRecord> list = Collections.emptyList();
		JsonValue encoded = TestRecord.LIST_CODEC.encode(list);

		assertEquals(new JsonArray(), encoded);

		JsonArray array = new JsonArray();
		List<TestRecord> decoded = TestRecord.LIST_CODEC.decode(array).getOrThrow();
		assertEquals(Collections.emptyList(), decoded);
	}

	@Test
	public void single() {
		List<TestRecord> expected = new ArrayList<>();
		expected.add(new TestRecord(15, "gerald", false));

		JsonArray array = JsonArray.of(
				new JsonObject()
						.put("i", 15)
						.put("s", "gerald")
						.put("gerald", false)
		);

		List<TestRecord> decoded = TestRecord.LIST_CODEC.decode(array).getOrThrow();
		assertEquals(expected, decoded);
		assertEquals(array, TestRecord.LIST_CODEC.encode(decoded));
	}

	@Test
	public void multi() {
		List<TestRecord> expected = new ArrayList<>();
		expected.add(new TestRecord(15, "gerald", true));
		expected.add(new TestRecord(-7, "h!", false));

		JsonArray array = JsonArray.of(
				new JsonObject()
						.put("i", 15)
						.put("s", "gerald"),
				new JsonObject()
						.put("i", -7)
						.put("s", "h!")
						.put("gerald", false)
		);

		List<TestRecord> decoded = TestRecord.LIST_CODEC.decode(array).getOrThrow();
		assertEquals(expected, decoded);
		assertEquals(array, TestRecord.LIST_CODEC.encode(decoded));
	}
}
