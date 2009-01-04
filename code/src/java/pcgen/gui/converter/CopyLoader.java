package pcgen.gui.converter;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public class CopyLoader extends AbstractLoader
{
	private final ListKey<CampaignSourceEntry> listkey;

	public CopyLoader(LoadContext lc, ListKey<CampaignSourceEntry> lk)
	{
		super(lc);
		listkey = lk;
	}

	@Override
	public void process(StringBuilder result, int line, String lineString,
			CampaignSourceEntry cse) throws PersistenceLayerException
	{
		result.append(lineString);
	}

	@Override
	protected List<CampaignSourceEntry> getFiles(Campaign c)
	{
		return c.getSafeListFor(listkey);
	}

}
