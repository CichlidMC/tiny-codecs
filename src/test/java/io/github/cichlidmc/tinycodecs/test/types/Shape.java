package io.github.cichlidmc.tinycodecs.test.types;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.codec.map.CompositeCodec;
import io.github.cichlidmc.tinycodecs.map.MapCodec;

public interface Shape {
	Codec<Shape> CODEC = Type.CODEC.dispatch(Shape::getType, type -> type.codec);

	Type getType();

	class Circle implements Shape {
		public static final Codec<Circle> CODEC = Codec.FLOAT.xmap(Circle::new, circle -> circle.radius);
		public static final MapCodec<Circle> MAP_CODEC = CODEC.fieldOf("radius");

		public final float radius;


		public Circle(float radius) {
			this.radius = radius;
		}

		@Override
		public Type getType() {
			return Type.CIRCLE;
		}
	}

	class Square implements Shape {
		public static final Codec<Square> CODEC = Codec.FLOAT.xmap(Square::new, square -> square.length);
		public static final MapCodec<Square> MAP_CODEC = CODEC.fieldOf("length");

		public final float length;

		public Square(float length) {
			this.length = length;
		}

		@Override
		public Type getType() {
			return Type.SQUARE;
		}
	}

	class Triangle implements Shape {
		public static final MapCodec<Triangle> CODEC = CompositeCodec.of(
				Codec.FLOAT.fieldOf("base"), triangle -> triangle.base,
				Codec.FLOAT.fieldOf("height"), triangle -> triangle.height,
				Triangle::new
		);

		public final float base;
		public final float height;

		public Triangle(float base, float height) {
			this.base = base;
			this.height = height;
		}

		@Override
		public Type getType() {
			return Type.TRIANGLE;
		}
	}

	enum Type {
		CIRCLE(Circle.MAP_CODEC), SQUARE(Square.MAP_CODEC), TRIANGLE(Triangle.CODEC);

		public static final Codec<Type> CODEC = Codec.byName(Type.class);

		public final MapCodec<? extends Shape> codec;

		Type(MapCodec<? extends Shape> codec) {
			this.codec = codec;
		}
	}
}
