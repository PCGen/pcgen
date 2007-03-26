package plugin.lsttokens;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.Ability;
import pcgen.core.Constants;
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
import pcgen.util.StringPClassUtil;

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
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String key = st.hasMoreTokens() ? st.nextToken() : "";
		Class c;
		String category = null;
		int equalLoc = key.indexOf('=');
		if (equalLoc == -1) {
			if ("ABILITY".equals(key)) {
				Logging.errorPrint("Invalid use of ABILITY in QUALIFY "
						+ "(requires ABILITY=<category>): " + key);
				return false;
			}
			c = StringPClassUtil.getClassFor(key);
		} else {
			if (!"ABILITY".equals(key.substring(0, equalLoc))) {
				Logging.errorPrint("Invalid use of = in QUALIFY "
						+ "(only valid for ABILITY): " + key);
				return false;
			}
			c = Ability.class;
			category = key.substring(equalLoc + 1);
		}
		if (c == null) {
			c = Object.class;
			Logging.errorPrint(getTokenName() + " expecting a POBJECT Type, found: " + key);
			Logging.errorPrint("  5.12 Format is: QualifyType|Key[|Key] value was: " + value);
			Logging.errorPrint("  Valid QualifyTypes are: " + StringPClassUtil.getValidStrings());
			Logging.errorPrint("  QUALIFY without a Type will fail after PCGen 5.12");
		} else {
			key = st.nextToken();
		}
		
		while (true) {
			obj.putQualifyString(c, category, key);
			if (!st.hasMoreTokens()) {
				break;
			}
			key = st.nextToken();
		}
		
		return true;
	}
	
	public List<Class<? extends PObject>> getLegalTypes() {
		return Arrays.asList(Ability.class, Deity.class, Domain.class,
				Equipment.class, PCClass.class, Race.class, Skill.class,
				Spell.class, PCTemplate.class, WeaponProf.class);
	}
}
