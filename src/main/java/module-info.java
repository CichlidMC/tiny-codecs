import org.jspecify.annotations.NullMarked;

@NullMarked
open module fish.cichlidmc.tinycodecs {
	requires static transitive org.jetbrains.annotations;
	requires static transitive org.jspecify;
	requires transitive fish.cichlidmc.tinyjson;
	requires transitive fish.cichlidmc.fishflakes;

	exports fish.cichlidmc.tinycodecs.api;
	exports fish.cichlidmc.tinycodecs.api.codec;
	exports fish.cichlidmc.tinycodecs.api.codec.map;
	exports fish.cichlidmc.tinycodecs.api.codec.dual;
}
