package pcgen.util;

public class Predicates {
    public interface BooleanPredicate<T> {
        boolean test(final T t);
    }
}
