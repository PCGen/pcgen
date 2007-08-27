package plugin.lsttokens;

import java.util.List;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PObject;
import pcgen.core.QualifiedObject;
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
		List<QualifiedObject<String>> vfeatList =
				FeatParser.parseVirtualFeatListToQualObj(value);
		for (final QualifiedObject<String> ability : vfeatList)
		{
			obj.addAbility(AbilityCategory.FEAT, Ability.Nature.VIRTUAL,
				ability);
		}
		return true;
	}

}
