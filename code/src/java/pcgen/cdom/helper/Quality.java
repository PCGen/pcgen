package pcgen.cdom.helper;

public class Quality
{

	private final String quality;
	private final String value;

	public Quality(String key, String val)
	{
		quality = key;
		value = val;
	}

	public String getQuality()
	{
		return quality;
	}

	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return quality.hashCode() ^ value.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Quality)
		{
			Quality other = (Quality) o;
			return quality.equals(other.quality) && value.equals(other.value);
		}
		return false;
	}

	@Override
	public String toString()
	{
		return quality + ": " + value;
	}
}
