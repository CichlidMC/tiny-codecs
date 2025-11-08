open module fish.cichlidmc.tinycodecs {
	requires static org.jetbrains.annotations;
	requires fish.cichlidmc.tinyjson;

	exports fish.cichlidmc.tinycodecs.api;
	exports fish.cichlidmc.tinycodecs.api.codec;
	exports fish.cichlidmc.tinycodecs.api.codec.map;
	exports fish.cichlidmc.tinycodecs.api.codec.dual;
}
