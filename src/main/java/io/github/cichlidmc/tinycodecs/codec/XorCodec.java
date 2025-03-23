package io.github.cichlidmc.tinycodecs.codec;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.DecodeResult;
import io.github.cichlidmc.tinycodecs.util.Either;
import io.github.cichlidmc.tinyjson.value.JsonValue;

public final class XorCodec<L, R> implements Codec<Either<L, R>> {
	private final Codec<L> left;
	private final Codec<R> right;

	public XorCodec(Codec<L> left, Codec<R> right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public DecodeResult<Either<L, R>> decode(JsonValue value) {
		DecodeResult<L> leftResult = this.left.decode(value);
		DecodeResult<R> rightResult = this.right.decode(value);

		if (leftResult.isSuccess() && rightResult.isSuccess()) {
			return DecodeResult.error("Both formats were successful");
		} else if (leftResult.isSuccess()) {
			return leftResult.map(Either::left);
		} else if (rightResult.isSuccess()) {
			return rightResult.map(Either::right);
		}

		// both failed, merge errors
		String firstMessage = leftResult.asError().message;
		String secondMessage = rightResult.asError().message;
		return DecodeResult.error("Both formats failed to decode: " + firstMessage + "; " + secondMessage);
	}

	@Override
	public JsonValue encode(Either<L, R> value) {
		return Either.merge(value.map(this.left::encode, this.right::encode));
	}
}
