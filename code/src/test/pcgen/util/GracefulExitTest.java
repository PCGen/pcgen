package pcgen.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GracefulExitTest
{
	public static final ExitFunction EXIT_FUNCTION_WITH_EXCEPTION = status -> {
		throw new RuntimeException("Test exception");
	};

	@BeforeEach
	void setUp()
	{
		GracefulExit.resetForTests();
		GracefulExit.registerExitFunction(status -> { /* do nothing */ });
	}

	@Test
	void testDefaultExit()
	{
		GracefulExit.registerExitFunction(status -> assertEquals(23, status, "Status code should be 23"));
		GracefulExit.exit(23);
	}

	@Test
	void testExitWithThrownException()
	{
		GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
		assertThrows(RuntimeException.class, () -> GracefulExit.exit(0), "Should throw an exception");
	}

	@Test
	void testNoExitWithVetoingInterceptor()
	{
		GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
		GracefulExit.addExitInterceptor(status -> false);
		assertDoesNotThrow(() -> GracefulExit.exit(0),
				"Should not throw because the exit was vetoed");
	}

	@Test
	void testThrowingInterceptorVetoesExit()
	{
		GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION);
		GracefulExit.addExitInterceptor(status -> {
			throw new IllegalStateException("boom");
		});
		assertDoesNotThrow(() -> GracefulExit.exit(0),
				"A throwing interceptor must veto rather than propagate");
	}

	@Test
	void testAllInterceptorsRunEvenWhenOneVetoes()
	{
		AtomicInteger count = new AtomicInteger();
		GracefulExit.addExitInterceptor(status -> {
			count.incrementAndGet();
			return false;
		});
		GracefulExit.addExitInterceptor(status -> {
			count.incrementAndGet();
			return true;
		});
		GracefulExit.addExitInterceptor(status -> {
			count.incrementAndGet();
			return true;
		});
		GracefulExit.exit(0);
		assertEquals(3, count.get(), "All interceptors must run even after a veto");
	}

	@Test
	void testRegistrationCloseRemovesInterceptor()
	{
		AtomicInteger count = new AtomicInteger();
		GracefulExit.Registration reg = GracefulExit.addExitInterceptor(status -> {
			count.incrementAndGet();
			return true;
		});

		GracefulExit.exit(0);
		assertEquals(1, count.get(), "Interceptor should fire once while registered");

		reg.close();
		GracefulExit.exit(0);
		assertEquals(1, count.get(), "Interceptor must not fire after Registration.close()");
	}

	@Test
	void testRegistrationCloseIsIdempotent()
	{
		GracefulExit.Registration reg = GracefulExit.addExitInterceptor(status -> true);
		assertDoesNotThrow(reg::close);
		assertDoesNotThrow(reg::close, "Closing a Registration twice must be a no-op");
	}

	@Test
	void testAddHookDuringExit()
	{
		GracefulExit.addExitInterceptor(status -> {
			assertThrows(IllegalStateException.class,
					() -> GracefulExit.addExitInterceptor(s -> true),
					"Adding an interceptor during exit must fail");
			return true;
		});
		GracefulExit.exit(0);
	}

	@Test
	void testRegisterExitFunctionDuringExit()
	{
		GracefulExit.registerExitFunction(status -> assertThrows(IllegalStateException.class,
				() -> GracefulExit.registerExitFunction(EXIT_FUNCTION_WITH_EXCEPTION),
				"Registering an exit function during exit must fail"));
		GracefulExit.exit(0);
	}

	@Test
	void testClearInterceptorsDuringExit()
	{
		GracefulExit.registerExitFunction(status -> assertThrows(IllegalStateException.class,
				GracefulExit::clearExitInterceptors,
				"Clearing interceptors during exit must fail"));
		GracefulExit.exit(0);
	}

	@Test
	void testInterceptorsRunInRegistrationOrder()
	{
		AtomicInteger count = new AtomicInteger();

		GracefulExit.addExitInterceptor(status -> {
			assertEquals(0, count.getAndIncrement(), "First interceptor should run first");
			return true;
		});
		GracefulExit.addExitInterceptor(status -> {
			assertEquals(1, count.getAndIncrement(), "Second interceptor should run next");
			return true;
		});
		GracefulExit.addExitInterceptor(status -> {
			assertEquals(2, count.getAndIncrement(), "Third interceptor should run last");
			return true;
		});
		GracefulExit.exit(0);
		assertEquals(3, count.get(), "All interceptors should have run");
	}
}
