package plugin.lsttokens;

import java.util.Arrays;
import java.util.List;

import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PObject;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * Deals with the QUALIFY token for Abilities
 */
public class QualifyToken implements GlobalLstToken
{

	public String getTokenName()
	{
		return "QUALIFY";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!getLegalTypes().contains(obj.getClass())) {
			Logging.errorPrint("Cannot use QUALIFY on a " + obj.getClass());
			return false;
		}
		obj.setQualifyString(value);
		return true;
	}
	
	public List<Class<? extends PObject>> getLegalTypes() {
		return Arrays.asList(Ability.class, Deity.class, Domain.class,
				Equipment.class, PCClass.class, Race.class, Skill.class,
				Spell.class, PCTemplate.class, WeaponProf.class);
	}
}
