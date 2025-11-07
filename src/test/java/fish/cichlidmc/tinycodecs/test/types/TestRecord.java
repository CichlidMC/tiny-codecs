package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;

import java.util.List;

public record TestRecord(int i, String s, boolean gerald) {
	public static final DualCodec<TestRecord> CODEC = CompositeCodec.of(
			Codec.INT.fieldOf("i"), test -> test.i,
			Codec.STRING.fieldOf("s"), test -> test.s,
			Codec.BOOL.optional(true).fieldOf("gerald"), test -> test.gerald,
			TestRecord::new
	);

	public static final Codec<List<TestRecord>> LIST_CODEC = CODEC.codec().listOf();
}
