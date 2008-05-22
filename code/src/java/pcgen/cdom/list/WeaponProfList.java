package pcgen.cdom.list;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.WeaponProf;

public class WeaponProfList extends CDOMListObject<WeaponProf>
{

	public static final CDOMReference<WeaponProfList> STARTING;
	
	static
	{
		WeaponProfList wpl = new WeaponProfList();
		wpl.setName("*Starting");
		STARTING = new CDOMDirectSingleRef<WeaponProfList>(wpl);
	}

	public Class<WeaponProf> getListClass()
	{
		return WeaponProf.class;
	}

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	// No additional Functionality :)

}
