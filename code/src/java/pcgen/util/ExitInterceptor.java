package pcgen.util;

import java.util.function.IntPredicate;

/**
 * A functional interface that can be used to intercept the exit of the JVM.
 */
@FunctionalInterface
public interface ExitInterceptor extends IntPredicate
{
	/**
	 * Intercepts the exit of the JVM.
	 *
	 * @param status The status code to exit with
	 * @return true if the exit should proceed, false if the exit should be canceled
	 */
	default boolean intercept(int status)
	{
		return test(status);
	}
}
