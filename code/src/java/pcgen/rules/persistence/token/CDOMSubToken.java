package pcgen.rules.persistence.token;


public interface CDOMSubToken<T> extends CDOMToken<T>
{
	public String getParentToken();
}
