package pcgen.gui.converter;

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;

public interface Loader
{
	public void process(StringBuilder result, int line, String lineString,
			CampaignSourceEntry source) throws PersistenceLayerException;

}
