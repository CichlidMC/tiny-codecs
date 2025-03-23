package io.github.cichlidmc.tinycodecs.test;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.CodecResult;
import io.github.cichlidmc.tinycodecs.test.types.Vec3;
import io.github.cichlidmc.tinycodecs.util.Either;
import io.github.cichlidmc.tinyjson.value.JsonValue;
import io.github.cichlidmc.tinyjson.value.composite.JsonArray;
import io.github.cichlidmc.tinyjson.value.composite.JsonObject;
import io.github.cichlidmc.tinyjson.value.primitive.JsonNumber;
import io.github.cichlidmc.tinyjson.value.primitive.JsonString;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public final class XorTests {
	@Test
	public void left() {
		JsonValue input = new JsonString("1,2,3");
		CodecResult<Vec3> result = Vec3.STRING_OR_INT_ARRAY_CODEC.decode(input);
		assertTrue(result.isSuccess());
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
	public void both() {
		Codec<Vec3> codec = Vec3.STRING_CODEC.xor(Vec3.ZERO_UNIT_CODEC).xmap(Either::merge, Either::left);
		JsonValue input = new JsonString("4,5,6");
		CodecResult<Vec3> result = codec.decode(input);
		assertTrue(result.isError());
	}
}
