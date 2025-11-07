package fish.cichlidmc.tinycodecs.impl.codec;

import fish.cichlidmc.tinycodecs.api.CodecResult;
import fish.cichlidmc.tinycodecs.api.Either;
import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinyjson.value.JsonValue;

public record EitherCodec<L, R>(Codec<L> left, Codec<R> right, boolean strict) implements Codec<Either<L, R>> {
	@Override
	public CodecResult<? extends JsonValue> encode(Either<L, R> value) {
		return Either.join(value.map(this.left::encode, this.right::encode));
	}

	@Override
	public CodecResult<Either<L, R>> decode(JsonValue value) {
		CodecResult<Either<L, R>> leftResult = this.left.decode(value).map(Either::left);
		if (!this.strict && leftResult.isSuccess())
			return leftResult;

		CodecResult<Either<L, R>> rightResult = this.right.decode(value).map(Either::right);
		if (!this.strict && rightResult.isSuccess())
			return rightResult;

		if (this.strict && leftResult.isSuccess() && rightResult.isSuccess()) {
			return CodecResult.error("Both formats were successful");
		} else if (leftResult.isSuccess()) {
			return leftResult;
		} else if (rightResult.isSuccess()) {
			return rightResult;
		} else {
			// both must be an Error based on previous conditions
			String firstMessage = ((CodecResult.Error<?>) leftResult).message();
			String secondMessage = ((CodecResult.Error<?>) rightResult).message();
			return CodecResult.error("Both formats failed to decode: " + firstMessage + "; " + secondMessage);
		}
	}
}
