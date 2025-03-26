package io.github.cichlidmc.tinycodecs.test.types;

import io.github.cichlidmc.tinycodecs.Codec;
import io.github.cichlidmc.tinycodecs.CodecResult;

import java.util.Arrays;

public final class Vec3 {
	public static final Codec<Vec3> INT_ARRAY_CODEC = Codec.INT.listOf().comapFlatMap(
			list -> list.size() != 3 ? CodecResult.error("Wrong size") : CodecResult.success(new Vec3(list.get(0), list.get(1), list.get(2))),
			vec -> Arrays.asList(vec.x, vec.y, vec.z)
	);
	public static final Codec<Vec3> STRING_CODEC = Codec.STRING.comapFlatMap(
			string -> {
				String[] split = string.split(",");
				if (split.length != 3) {
					return CodecResult.error("Wrong size");
				}

				int[] ints = new int[3];
				for (int i = 0; i < split.length; i++) {
					try {
						ints[i] = Integer.parseInt(split[i]);
					} catch (NumberFormatException e) {
						return CodecResult.error("not an int");
					}
				}

				return CodecResult.success(new Vec3(ints[0], ints[1], ints[2]));
			},
			vec -> vec.x + "," + vec.y + ',' + vec.z
	);
	public static final Codec<Vec3> STRING_OR_INT_ARRAY_CODEC = STRING_CODEC.withAlternative(INT_ARRAY_CODEC);
	public static final Codec<Vec3> ZERO_UNIT_CODEC = Codec.unit(new Vec3(0, 0, 0));

	public final int x;
	public final int y;
	public final int z;

	public Vec3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
