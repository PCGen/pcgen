package pcgen.core.analysis;

import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpecialAbility;

public class SpecialAbilityResolution
{
	public static void addSABToList(List<SpecialAbility> saList, PlayerCharacter pc, CDOMObject cdo)
	{
		for (SpecialAbility sa : cdo.getSafeListFor(ListKey.SAB))
		{
			if (pc == null || sa.qualifies(pc))
			{
				final String key = sa.getKeyName();
				final int idx = key.indexOf("%CHOICE");

				if (idx >= 0)
				{
					StringBuilder sb = new StringBuilder();
					sb.append(key.substring(0, idx));

					if (pc.hasAssociations(cdo))
					{
						sb.append(StringUtil.joinToStringBuffer(pc
								.getAssociationList(cdo), ", "));
					}
					else
					{
						sb.append("<undefined>");
					}

					sb.append(key.substring(idx + 7));
					sa = new SpecialAbility(sb.toString(), sa.getSASource(), sa
							.getSADesc());
					saList.add(sa);
				}
				else
				{
					saList.add(sa);
				}
			}
		}
	}

}
