package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Globals;
import pcgen.core.Skill;

public class MasterSkillFacet
{

	private HashMapToList<ClassSkillList, Skill> hml;

	private void initialize()
	{
		hml = new HashMapToList<ClassSkillList, Skill>();
		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			Collection objects = masterLists.getObjects(ref);
			for (Object cl : ref.getContainedObjects())
			{
				if (cl instanceof ClassSkillList)
				{
					hml.addAllToListFor((ClassSkillList) cl, objects);
				}
			}
		}
	}

	public boolean hasMasterSkill(ClassSkillList csl, Skill sk)
	{
		if (hml == null)
		{
			initialize();
		}
		return hml.containsInList(csl, sk);
	}
}
