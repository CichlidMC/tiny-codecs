package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;

public interface Animal {
	Codec<MapCodec<? extends Animal>> TYPE_CODEC = Codec.byName(
			codec -> codec == Dog.CODEC ? "dog" : "cat",
			name -> name.equals("dog") ? Dog.CODEC : Cat.CODEC
	);
	Codec<Animal> CODEC = Codec.codecDispatch(TYPE_CODEC, Animal::codec);

	String sound();

	MapCodec<? extends Animal> codec();

	record Dog(String sound) implements Animal {
		public static final MapCodec<Dog> CODEC = Codec.STRING.xmap(Dog::new, Dog::sound).fieldOf("sound");

		@Override
		public MapCodec<? extends Animal> codec() {
			return CODEC;
		}
	}

	record Cat(String sound) implements Animal {
		public static final MapCodec<Cat> CODEC = Codec.STRING.xmap(Cat::new, Cat::sound).fieldOf("sound");

		@Override
		public MapCodec<? extends Animal> codec() {
			return CODEC;
		}
	}
}
