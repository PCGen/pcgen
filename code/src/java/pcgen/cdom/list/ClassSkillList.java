package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.core.Skill;

public class ClassSkillList extends CDOMListObject<Skill>
{

	public Class<Skill> getListClass()
	{
		return Skill.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
