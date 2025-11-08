open module fish.cichlidmc.tinycodecs {
	requires static transitive org.jetbrains.annotations;
	requires transitive fish.cichlidmc.tinyjson;

	exports fish.cichlidmc.tinycodecs.api;
	exports fish.cichlidmc.tinycodecs.api.codec;
	exports fish.cichlidmc.tinycodecs.api.codec.map;
	exports fish.cichlidmc.tinycodecs.api.codec.dual;
}
