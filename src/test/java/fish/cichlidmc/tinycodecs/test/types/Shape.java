package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.tinycodecs.api.codec.Codec;
import fish.cichlidmc.tinycodecs.api.codec.CompositeCodec;
import fish.cichlidmc.tinycodecs.api.codec.dual.DualCodec;
import fish.cichlidmc.tinycodecs.api.codec.map.MapCodec;

public interface Shape {
	Codec<Shape> CODEC = Type.CODEC.dispatch(Shape::getType, type -> type.codec);

	Type getType();

	record Circle(float radius) implements Shape {
		public static final Codec<Circle> CODEC = Codec.FLOAT.xmap(Circle::new, circle -> circle.radius);
		public static final MapCodec<Circle> MAP_CODEC = CODEC.fieldOf("radius");

		@Override
		public Type getType() {
			return Type.CIRCLE;
		}
	}

	record Square(float length) implements Shape {
		public static final Codec<Square> CODEC = Codec.FLOAT.xmap(Square::new, square -> square.length);
		public static final MapCodec<Square> MAP_CODEC = CODEC.fieldOf("length");

		@Override
		public Type getType() {
			return Type.SQUARE;
		}
	}

	record Triangle(float base, float height) implements Shape {
		public static final DualCodec<Triangle> CODEC = CompositeCodec.of(
				Codec.FLOAT.fieldOf("base"), triangle -> triangle.base,
				Codec.FLOAT.fieldOf("height"), triangle -> triangle.height,
				Triangle::new
		);

		@Override
		public Type getType() {
			return Type.TRIANGLE;
		}
	}

	enum Type {
		CIRCLE(Circle.MAP_CODEC), SQUARE(Square.MAP_CODEC), TRIANGLE(Triangle.CODEC.mapCodec());

		public static final Codec<Type> CODEC = Codec.byName(Type.class);

		public final MapCodec<? extends Shape> codec;

		Type(MapCodec<? extends Shape> codec) {
			this.codec = codec;
		}
	}
}
