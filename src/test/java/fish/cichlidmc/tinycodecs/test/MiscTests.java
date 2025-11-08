package fish.cichlidmc.tinycodecs.test;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import org.junit.jupiter.api.Test;

public final class MiscTests {
	@Test
	public void checkModuleVersion() {
		Codec.class.getModule().getDescriptor().version().orElseThrow();
	}
}
