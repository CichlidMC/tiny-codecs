package io.github.cichlidmc.tinycodecs.test;

import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinycodecs.test.types.Shape;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class DispatchTests {
	@Test
	public void missingType() {
		JsonObject json = new JsonObject()
				.put("radius", 4);

		CodecResult<Shape> result = Shape.CODEC.decode(json);
		assertInstanceOf(CodecResult.Error.class, result);
	}

	@Test
	public void correctType() {
		JsonObject json = new JsonObject()
				.put("radius", 4)
				.put("type", "CIRCLE");

		Shape shape = Shape.CODEC.decode(json).getOrThrow();
		assertEquals(Shape.Type.CIRCLE, shape.getType());
	}

	@Test
	public void encode() {
		JsonObject expected = new JsonObject()
				.put("type", "TRIANGLE")
				.put("base", 1)
				.put("height", 2);

		Shape shape = new Shape.Triangle(1, 2);
		JsonValue actual = Shape.CODEC.encode(shape).getOrThrow();
		assertEquals(expected, actual);
	}
}
