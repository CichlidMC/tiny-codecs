package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;

import java.util.Optional;

public record Recursive(String name, Optional<Recursive> next) {
	public static final Codec<Recursive> CODEC = Codec.recursive(self -> CompositeCodec.of(
			Codec.STRING.fieldOf("name"), Recursive::name,
			self.optional().fieldOf("next"), Recursive::next,
			Recursive::new
	).codec());

	public Recursive(String name, Recursive next) {
		this(name, Optional.of(next));
	}

	public Recursive(String name) {
		this(name, Optional.empty());
	}
}
