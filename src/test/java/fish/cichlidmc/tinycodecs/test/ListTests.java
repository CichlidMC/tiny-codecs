package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.tinycodecs.test.types.TestRecord;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonArray;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListTests {
	@Test
	public void emptyList() {
		List<TestRecord> list = List.of();
		JsonValue encoded = TestRecord.LIST_CODEC.encode(list).getOrThrow();

		assertEquals(new JsonArray(), encoded);

		JsonArray array = new JsonArray();
		List<TestRecord> decoded = TestRecord.LIST_CODEC.decode(array).getOrThrow();
		assertEquals(List.of(), decoded);
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
		assertEquals(array, TestRecord.LIST_CODEC.encode(decoded).getOrThrow());
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
		assertEquals(array, TestRecord.LIST_CODEC.encode(decoded).getOrThrow());
	}
}
