package pcgen.util;

/**
 * An interface that can be used to intercept the exit of the JVM.
 */
public interface ExitInterceptor
{
	/**
	 * Intercepts the exit of the JVM.
	 *
	 * @param status The status code to exit with
	 * @return true if the exit should proceed, false if the exit should be canceled
	 */
	 boolean intercept(int status);
}
