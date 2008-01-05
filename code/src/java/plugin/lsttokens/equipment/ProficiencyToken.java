package plugin.lsttokens.equipment;

import pcgen.core.Constants;
import pcgen.core.Equipment;
import pcgen.persistence.lst.EquipmentLstToken;
import pcgen.util.Logging;

/**
 * Deals with PROFICIENCY token
 */
public class ProficiencyToken implements EquipmentLstToken
{

	public String getTokenName()
	{
		return "PROFICIENCY";
	}

	public boolean parse(Equipment eq, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.deprecationPrint("Equipment Token PROFICIENCY syntax "
					+ "without a Subtoken is deprecated: " + value);
			Logging.deprecationPrint("Please use: "
					+ "PROFICIENCY:<subtoken>|<prof>");
			eq.setProfName(value);
		}
		else
		{
			String subtoken = value.substring(0, pipeLoc);
			String prof = value.substring(pipeLoc + 1);
			if (prof == null || prof.length() == 0)
			{
				Logging.errorPrint("PROFICIENCY cannot have "
						+ "empty second argument: " + value);
				return false;
			}
			if (prof.indexOf(Constants.PIPE) != -1)
			{
				Logging.errorPrint("PROFICIENCY cannot have two | characters: "
						+ value);
				return false;
			}
			if (subtoken.equals("WEAPON"))
			{
				if (prof.startsWith("TYPE="))
				{
					eq.setArmorProf("Weapon (" + prof.substring(5) + ")");
				}
				else
				{
					eq.setWeaponProf(prof);
				}
			}
			else if (subtoken.equals("ARMOR"))
			{
				if (prof.startsWith("TYPE="))
				{
					eq.setArmorProf("Armor (" + prof.substring(5) + ")");
				}
				else
				{
					eq.setArmorProf(prof);
				}
			}
			else if (subtoken.equals("SHIELD"))
			{
				if (prof.startsWith("TYPE="))
				{
					eq.setShieldProf("Shield (" + prof.substring(5) + ")");
				}
				else
				{
					eq.setShieldProf(prof);
				}
			}
			else
			{
				Logging.errorPrint("Unknown Subtoken for PROFICIENCY: "
						+ subtoken);
				Logging.errorPrint("  Subtoken must be "
						+ "WEAPON, ARMOR or SHIELD");
				return false;
			}
		}
		return true;
	}
}
