package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.test.types.Vec3;
import fish.cichlidmc.tinycodecs.util.Either;
import fish.cichlidmc.tinyjson.value.JsonValue;
import fish.cichlidmc.tinyjson.value.composite.JsonArray;
import fish.cichlidmc.tinyjson.value.composite.JsonObject;
import fish.cichlidmc.tinyjson.value.primitive.JsonNumber;
import fish.cichlidmc.tinyjson.value.primitive.JsonString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class EitherTests {
	@Test
	public void left() {
		JsonValue input = new JsonString("1,2,3");
		CodecResult<Vec3> result = Vec3.STRING_OR_INT_ARRAY_CODEC.decode(input);
		assertTrue(result.isSuccess(), () -> result.asError().message);
	}

	@Test
	public void right() {
		JsonValue input = JsonArray.of(
				new JsonNumber(1), new JsonNumber(2), new JsonNumber(3)
		);
		CodecResult<Vec3> result = Vec3.STRING_OR_INT_ARRAY_CODEC.decode(input);
		assertTrue(result.isSuccess(), () -> result.asError().message);
	}

	@Test
	public void neither() {
		JsonValue input = new JsonObject();
		CodecResult<Vec3> result = Vec3.STRING_OR_INT_ARRAY_CODEC.decode(input);
		assertTrue(result.isError());
	}

	@Test
	public void bothStrict() {
		Codec<Vec3> codec = Vec3.STRING_CODEC.xor(Vec3.ZERO_UNIT_CODEC).xmap(Either::merge, Either::left);
		JsonValue input = new JsonString("4,5,6");
		CodecResult<Vec3> result = codec.decode(input);
		assertTrue(result.isError());
	}

	@Test
	public void bothLoose() {
		Codec<Vec3> codec = Vec3.STRING_CODEC.either(Vec3.ZERO_UNIT_CODEC).xmap(Either::merge, Either::left);
		JsonValue input = new JsonString("4,5,6");
		CodecResult<Vec3> result = codec.decode(input);
		assertTrue(result.isSuccess());
	}
}
