package plugin.lsttokens.pcclass;

import java.util.List;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCClass;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLstToken;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.persistence.lst.utils.FeatParser;
import pcgen.util.Logging;

/**
 * Class deals with VFEAT Token
 */
public class VfeatToken implements PCClassLstToken
{

	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(PCClass pcclass, String value, int level)
	{
		List<QualifiedObject<String>> vfeatList =
				FeatParser.parseVirtualFeatListToQualObj(value);
		for (final QualifiedObject<String> ability : vfeatList)
		{
			String preLevelString = "";
			try
			{
				PreParserFactory factory = PreParserFactory.getInstance();
				preLevelString =
						"PRECLASS:1," + pcclass.getKeyName() + "=" + level;	//$NON-NLS-1$ //$NON-NLS-2$
				Prerequisite r = factory.parse(preLevelString);
				ability.addPrerequisite(r);
			}
			catch (PersistenceLayerException notUsed)
			{
				Logging.errorPrint("Failed to create level prereq for VFEAT "
					+ value + ". Prereq was " + preLevelString + ".", notUsed);
				return false;
			}
			pcclass.addAbility(AbilityCategory.FEAT, Ability.Nature.VIRTUAL,
				ability);
		}
		pcclass.addVirtualFeats(level, FeatParser.parseVirtualFeatList(value));
		return true;
	}
}
