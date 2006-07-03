package pcgen.core;

public interface KeyedObject
{
	public String getDisplayName();
	public void setName( final String aName );

	public String getKeyName();
	public void setKeyName( final String aKey );
}
