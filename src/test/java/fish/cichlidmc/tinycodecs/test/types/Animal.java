package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.Codec;
import fish.cichlidmc.tinycodecs.map.MapCodec;

public interface Animal {
	Codec<MapCodec<? extends Animal>> TYPE_CODEC = Codec.byName(
			codec -> codec == Dog.CODEC ? "dog" : "cat",
			name -> name.equals("dog") ? Dog.CODEC : Cat.CODEC
	);
	Codec<Animal> CODEC = Codec.codecDispatch(TYPE_CODEC, Animal::codec);

	String sound();

	MapCodec<? extends Animal> codec();

	final class Dog implements Animal {
		public static final MapCodec<Dog> CODEC = Codec.STRING.xmap(Dog::new, Dog::sound).fieldOf("sound");

		private final String sound;

		public Dog(String sound) {
			this.sound = sound;
		}

		@Override
		public String sound() {
			return this.sound;
		}

		@Override
		public MapCodec<? extends Animal> codec() {
			return CODEC;
		}
	}

	final class Cat implements Animal {
		public static final MapCodec<Cat> CODEC = Codec.STRING.xmap(Cat::new, Cat::sound).fieldOf("sound");

		private final String sound;

		public Cat(String sound) {
			this.sound = sound;
		}

		@Override
		public String sound() {
			return this.sound;
		}

		@Override
		public MapCodec<? extends Animal> codec() {
			return CODEC;
		}
	}
}
