package util;

/**
 * A functional interface that can be used to intercept the exit of the JVM.
 */
@FunctionalInterface
public interface ExitInterceptor
{
	/**
	 * Is executed when the JVM should exit.
	 *
	 * @param status The status code to exit with
	 * @return true if the exit should be allowed, false if it should be intercepted
	 */
	boolean intercept(int status);
}
