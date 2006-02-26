package plugin.lsttokens.skill;

import pcgen.core.Skill;
import pcgen.persistence.lst.SkillLstToken;

/**
 * Class deals with VISIBLE Token
 */
public class VisibleToken implements SkillLstToken {

	public String getTokenName() {
		return "VISIBLE";
	}

	public boolean parse(Skill skill, String value) {
		final String visType = value.toUpperCase();

		if (visType.startsWith("YES") || visType.startsWith("ALWAYS")) {
			skill.setVisible(Skill.VISIBILITY_DEFAULT);
		}
		else if (visType.startsWith("GUI")) {
			skill.setVisible(Skill.VISIBILITY_DISPLAY_ONLY);
		}
		else if (visType.startsWith("EXPORT") || visType.startsWith("CSHEET")) {
			skill.setVisible(Skill.VISIBILITY_OUTPUT_ONLY);
		}
		else {
			skill.setVisible(Skill.VISIBILITY_DEFAULT);
			return false;
		}

		String[] elements = visType.split("\\|");

		if (elements.length > 1) {
			if (elements[1].equals("READONLY") && !visType.startsWith("EXPORT")) {
				skill.setReadOnly(true);
			}
			else {
				return false;
			}
		}
		return true;
	}
}

