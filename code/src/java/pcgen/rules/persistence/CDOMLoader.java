package pcgen.rules.persistence;

import java.net.URI;
import java.util.Collection;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.rules.context.LoadContext;

public interface CDOMLoader<T>
{

	public void parseLine(LoadContext context, T obj, String val, URI source)
			throws PersistenceLayerException;

	public void loadLstFiles(LoadContext context,
			Collection<CampaignSourceEntry> sources);

	public void unloadLstFiles(LoadContext lc,
			Collection<CampaignSourceEntry> languageFiles);

}
