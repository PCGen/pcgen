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
 * Deals with the SERVESAS token for Abilities
 */
public class ServesAsToken implements GlobalLstToken
{

	public String getTokenName()
	{
		return "SERVESAS";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (!getLegalTypes().contains(obj.getClass())) {
			Logging.errorPrint("Cannot use SERVESAS on a " + obj.getClass());
			return false;
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String key = st.hasMoreTokens() ? st.nextToken() : "";
		Class c;
		String category = "";
		int equalLoc = key.indexOf('=');
		if (equalLoc == -1) {
			if ("ABILITY".equals(key)) {
				Logging.errorPrint("Invalid use of ABILITY in SERVESAS "
						+ "(requires ABILITY=<category>): " + key);
				return false;
			}
			c = StringPClassUtil.getClassFor(key);
		} else {
			if (!"ABILITY".equals(key.substring(0, equalLoc))) {
				Logging.errorPrint("Invalid use of = in SERVESAS "
						+ "(only valid for ABILITY): " + key);
				return false;
			}
			c = Ability.class;
			category = key.substring(equalLoc + 1);
		}
		if (c == null) {
			c = Object.class;
			Logging.deprecationPrint(getTokenName() + " expecting a POBJECT Type, found: " + key);
			Logging.deprecationPrint("  5.14 Format is: SERVESASType|Key[|Key] value was: " + value);
			Logging.deprecationPrint("  Valid SERVESASTypes are: " + StringPClassUtil.getValidStrings());
			Logging.deprecationPrint("  SERVESAS without a Type will fail after PCGen 5.14");
		} else {
			key = st.nextToken();
		}
		
		while (true) 
		{
			obj.putServesAs( key, category);
			
			if (!st.hasMoreTokens()) 
			{
				break;
			}			
			
			key = st.nextToken();
		}
		
		return true;
	}
	
	public List<Class<? extends PObject>> getLegalTypes() {
		return Arrays.asList(
			 
			PCClass.class, Ability.class,Skill.class
			//Ability.class, Deity.class, Domain.class,Equipment.class,
			//Race.class, Skill.class,Spell.class, PCTemplate.class, WeaponProf.class
			);
	}
}
