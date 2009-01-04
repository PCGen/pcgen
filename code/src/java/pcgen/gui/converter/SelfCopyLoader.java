package pcgen.gui.converter;

import java.util.Collections;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public class SelfCopyLoader extends AbstractLoader
{

	public SelfCopyLoader(LoadContext lc)
	{
		super(lc);
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
		return Collections.singletonList(new CampaignSourceEntry(c, c
				.getSourceURI()));
	}

}
