package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.test.types.Animal;
import fish.cichlidmc.tinycodecs.test.types.Shape;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
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

	@Test
	public void codecDispatch() {
		JsonObject json = new JsonObject()
				.put("type", "cat")
				.put("sound", "meow");

		Animal animal = Animal.CODEC.decode(json).getOrThrow();
		assertInstanceOf(Animal.Cat.class, animal);
		assertEquals("meow", animal.sound());
	}
}
