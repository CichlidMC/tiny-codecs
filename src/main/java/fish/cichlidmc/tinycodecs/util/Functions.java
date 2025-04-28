package fish.cichlidmc.tinycodecs.util;

public class Functions {
	public interface F1<A, T> { T apply(A a); }
	public interface F2<A, B, T> { T apply(A a, B b); }
	public interface F3<A, B, C, T> { T apply(A a, B b, C c); }
	public interface F4<A, B, C, D, T> { T apply(A a, B b, C c, D d); }
	public interface F5<A, B, C, D, E, T> { T apply(A a, B b, C c, D d, E e); }
	public interface F6<A, B, C, D, E, F, T> { T apply(A a, B b, C c, D d, E e, F f); }
}
