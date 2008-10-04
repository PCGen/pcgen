package pcgen.persistence.lst.utils;

import pcgen.persistence.lst.CampaignSourceEntry;

public class DeferredLine
{

	public final CampaignSourceEntry source;
	public final String lstLine;

	public DeferredLine(CampaignSourceEntry source, String line)
	{
		this.source = source;
		this.lstLine = line;
	}
}
