package pcgen.rules.persistence.token;

public interface CompatibilityToken
{
	public int compatibilityLevel();

	public int compatibilitySubLevel();

	public int compatibilityPriority();

}
