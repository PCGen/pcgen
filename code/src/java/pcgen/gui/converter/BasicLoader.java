package pcgen.gui.converter;

import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public class BasicLoader extends AbstractTokenLoader
{

	private final Class<? extends CDOMObject> cdomClass;
	private final ListKey<CampaignSourceEntry> listkey;

	public BasicLoader(LoadContext lc, Class<? extends CDOMObject> cl,
			ListKey<CampaignSourceEntry> lk)
	{
		super(lc);
		cdomClass = cl;
		listkey = lk;
	}

	@Override
	public void process(StringBuilder result, int line, String lineString,
			CampaignSourceEntry cse) throws PersistenceLayerException
	{
		processTokens(cdomClass, result, line, lineString);
	}

	@Override
	protected List<CampaignSourceEntry> getFiles(Campaign c)
	{
		return c.getSafeListFor(listkey);
	}

}
