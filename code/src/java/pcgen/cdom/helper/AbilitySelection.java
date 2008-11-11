package pcgen.cdom.helper;

import java.util.StringTokenizer;

import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.Ability.Nature;

public class AbilitySelection
{

	private final Ability ability;

	private final Ability.Nature nature;

	private final String selection;

	public AbilitySelection(Ability a, Nature n)
	{
		if (a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:YES Ability must have choices");
		}
		ability = a;
		nature = n;
		selection = null;
	}

	public AbilitySelection(Ability a, Nature n, String s)
	{
		if (s != null && !a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:NO Ability must not have choices");
		}
		if (s == null && a.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			throw new IllegalArgumentException(
					"AbilitySelection with MULT:YES Ability must have choices");
		}
		ability = a;
		nature = n;
		selection = s;
	}

	public String getAbilityKey()
	{
		return ability.getKeyName();
	}

	public Category<Ability> getAbilityCategory()
	{
		return ability.getCDOMCategory();
	}

	public String getFullAbilityKey()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ability.getKeyName());
		if (selection != null && selection.length() > 0)
		{
			sb.append('(');
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	public boolean containsAssociation(String a)
	{
		return a == null ? selection == null : a.equals(selection);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ability.getDisplayName());
		if (selection != null && selection.length() > 0)
		{
			sb.append('(');
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	public String getSelection()
	{
		return selection;
	}

	public String getPersistentFormat()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("CATEGORY=");
		sb.append(ability.getCategory());
		sb.append('|');
		sb.append("NATURE=");
		sb.append(nature);
		sb.append('|');
		sb.append(ability.getDisplayName());
		if (selection != null)
		{
			sb.append('|');
			sb.append(selection);
		}
		return sb.toString();
	}

	public static AbilitySelection getAbilitySelectionFromPersistentFormat(
			String s)
	{
		// TODO needs LOTS of error checking
		StringTokenizer st = new StringTokenizer(s, Constants.PIPE);
		String cat = st.nextToken().substring(9);
		AbilityCategory ac = SettingsHandler.getGame().getAbilityCategory(cat);
		String natString = st.nextToken().substring(7);
		Nature nat = Ability.Nature.valueOf(natString);
		String ab = st.nextToken();
		String sel = null;
		if (st.hasMoreTokens())
		{
			sel = st.nextToken();
		}
		Ability a = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
				Ability.class, ac, ab);
		return new AbilitySelection(a, nat, sel);

	}

	public Ability.Nature getNature()
	{
		return nature;
	}

	public Ability getAbility()
	{
		return ability;
	}
}
