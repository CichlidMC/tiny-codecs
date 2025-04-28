package fish.cichlidmc.tinycodecs.codec;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.CodecResult;
import fish.cichlidmc.tinycodecs.util.Either;
import fish.cichlidmc.tinyjson.value.JsonValue;

public final class EitherCodec<L, R> implements Codec<Either<L, R>> {
	private final Codec<L> left;
	private final Codec<R> right;
	private final boolean strict;

	public EitherCodec(Codec<L> left, Codec<R> right, boolean strict) {
		this.left = left;
		this.right = right;
		this.strict = strict;
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
			String firstMessage = leftResult.asError().message;
			String secondMessage = rightResult.asError().message;
			return CodecResult.error("Both formats failed to decode: " + firstMessage + "; " + secondMessage);
		}
	}

	@Override
	public CodecResult<? extends JsonValue> encode(Either<L, R> value) {
		return Either.merge(value.map(this.left::encode, this.right::encode));
	}
}
