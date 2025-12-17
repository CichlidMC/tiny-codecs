package fish.cichlidmc.tinycodecs.test.types;

import fish.cichlidmc.fishflakes.api.Result;
import fish.cichlidmc.tinycodecs.api.codec.Codec;

import java.util.Arrays;

public record Vec3(int x, int y, int z) {
	public static final Codec<Vec3> INT_ARRAY_CODEC = Codec.INT.listOf().comapFlatMap(
			list -> list.size() != 3 ? Result.error("Wrong size") : Result.success(new Vec3(list.get(0), list.get(1), list.get(2))),
			vec -> Arrays.asList(vec.x, vec.y, vec.z)
	);
	public static final Codec<Vec3> STRING_CODEC = Codec.STRING.comapFlatMap(
			string -> {
				String[] split = string.split(",");
				if (split.length != 3) {
					return Result.error("Wrong size");
				}

				int[] ints = new int[3];
				for (int i = 0; i < split.length; i++) {
					try {
						ints[i] = Integer.parseInt(split[i]);
					} catch (NumberFormatException e) {
						return Result.error("not an int");
					}
				}

				return Result.success(new Vec3(ints[0], ints[1], ints[2]));
			},
			vec -> vec.x + "," + vec.y + ',' + vec.z
	);
	public static final Codec<Vec3> STRING_OR_INT_ARRAY_CODEC = STRING_CODEC.withAlternative(INT_ARRAY_CODEC);
	public static final Codec<Vec3> ZERO_UNIT_CODEC = Codec.unit(new Vec3(0, 0, 0));
}
