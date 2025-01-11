package util;

@FunctionalInterface
public interface ExitInterceptor
{
	boolean intercept(int status);
}
