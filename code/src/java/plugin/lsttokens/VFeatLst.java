package plugin.lsttokens;

import pcgen.core.PObject;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.persistence.lst.utils.FeatParser;

public class VFeatLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(PObject obj, String value, int anInt)
		throws PersistenceLayerException
	{
		obj.addVirtualFeats(FeatParser.parseVirtualFeatList(value));
		return true;
	}

}
