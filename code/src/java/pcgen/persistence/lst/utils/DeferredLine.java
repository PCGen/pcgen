package pcgen.persistence.lst.utils;

import pcgen.persistence.lst.SourceEntry;

public class DeferredLine
{

	public final SourceEntry source;
	public final String lstLine;

	public DeferredLine(SourceEntry source, String line)
	{
		this.source = source;
		this.lstLine = line;
	}
}
