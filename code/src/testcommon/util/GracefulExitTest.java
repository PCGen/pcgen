package util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pcgen.util.ExitFunction;
import pcgen.util.ExitInterceptor;
import pcgen.util.GracefulExit;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GracefulExitTest
{
	public static final ExitFunction EXIT_FUNCTION_WITH_EXCEPTION = status -> {
		throw new RuntimeException("Test exception");
	};

	public static final ExitInterceptor EMPTY_EXIT_INTERCEPTOR = status -> true;

	@BeforeEach
	void setUp()
	{
		GracefulExit.registerExitFunction(status -> { /* do nothing */ });
		GracefulExit.clearExitInterceptors();
	}

	@Test
	void testDefaultExit()
	{
		GracefulExit.registerExitFunction(status -> {
			assertEquals(23, status, "Status code should be 23");
		});
		GracefulExit.exit(23);
	}

	@Test
	void testExitWithThrownException()
	{
		GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
		assertThrows(RuntimeException.class, () -> GracefulExit.exit(0), "Should throw an exception");
	}

	@Test
	void testNoExitWithExceptionThrown()
	{
		GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
		GracefulExit.addExitInterceptor(status -> false);
		assertDoesNotThrow(() -> GracefulExit.exit(0),
				"Should not throw an exception because the exit was intercepted");
	}

	@Test
	void testUniqueInterceptor()
	{
		ExitInterceptor customInterceptor = status -> false;
		GracefulExit.addExitInterceptor(customInterceptor);

		assertThrows(IllegalArgumentException.class, () -> {
			GracefulExit.addExitInterceptor(customInterceptor);
		}, "Should throw an exception because the interceptor is already registered");
	}

	@Test
	void testAddHookDuringExit()
	{
		GracefulExit.registerExitFunction(status -> {
		});

		GracefulExit.addExitInterceptor(status -> {
			assertThrows(IllegalStateException.class, () -> {
				GracefulExit.addExitInterceptor(EMPTY_EXIT_INTERCEPTOR);
			}, "Should throw an exception because the interceptor is added during exit");
			return true;
		});
		GracefulExit.exit(0);
	}

	@Test
	void testAddExitDuringExit()
	{
		GracefulExit.registerExitFunction(status -> {
			assertThrows(IllegalStateException.class, () -> {
				GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
			}, "Should throw an exception because the exit function is added during exit");
		});
		GracefulExit.exit(0);
	}

	@Test
	void testClearHooksDuringExit()
	{
		GracefulExit.registerExitFunction(status -> {
			assertThrows(IllegalStateException.class, GracefulExit::clearExitInterceptors,
					"Should throw an exception because the interceptors are cleared during exit");
		});
		GracefulExit.exit(0);
	}

	@Test
	void testExitHookSequence()
	{
		AtomicInteger count = new AtomicInteger(0);

		GracefulExit.addExitInterceptor(status -> {
			assertEquals(0, count.get(), "The first interceptor should be called first");
			count.incrementAndGet();
			return true;
		});
		GracefulExit.addExitInterceptor(status -> {
			assertEquals(1, count.get(), "The second interceptor should be called next");
			count.incrementAndGet();
			return true;
		});
		GracefulExit.addExitInterceptor(status -> {
			assertEquals(2, count.get(), "The last interceptor should be called next");
			count.incrementAndGet();
			return true;
		});
		GracefulExit.exit(0);
		assertEquals(3, count.get(), "All interceptors should have been called");
	}
}
