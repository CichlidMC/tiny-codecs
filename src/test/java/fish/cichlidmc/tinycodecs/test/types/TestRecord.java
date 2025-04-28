package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.codec.map.CompositeCodec;

import java.util.List;
import java.util.Objects;

public class TestRecord {
	public static final Codec<TestRecord> CODEC = CompositeCodec.of(
			Codec.INT.fieldOf("i"), test -> test.i,
			Codec.STRING.fieldOf("s"), test -> test.s,
			Codec.BOOL.optional(true).fieldOf("gerald"), test -> test.gerald,
			TestRecord::new
	).asCodec();

	public static final Codec<List<TestRecord>> LIST_CODEC = CODEC.listOf();

	public final int i;
	public final String s;
	public final boolean gerald;

	public TestRecord(int i, String s, boolean gerald) {
		this.i = i;
		this.s = s;
		this.gerald = gerald;
	}

	@Override
	public String toString() {
		return this.i + " " + this.s + " " + this.gerald;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.i, this.s, this.gerald);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != TestRecord.class)
			return false;

		TestRecord that = (TestRecord) obj;

		return this.i == that.i && this.s.equals(that.s) && this.gerald == that.gerald;
	}
}
