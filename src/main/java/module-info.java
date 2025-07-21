module fish.cichlidmc.tinycodecs {
	requires org.jetbrains.annotations;
	requires fish.cichlidmc.tinyjson;

	exports fish.cichlidmc.tinycodecs;

	exports fish.cichlidmc.tinycodecs.codec;
	exports fish.cichlidmc.tinycodecs.codec.map;
	exports fish.cichlidmc.tinycodecs.codec.optional;

	exports fish.cichlidmc.tinycodecs.map;
	exports fish.cichlidmc.tinycodecs.util;
}
