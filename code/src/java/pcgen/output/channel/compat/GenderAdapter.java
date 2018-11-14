package pcgen.output.channel.compat;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.enumeration.Gender;
import pcgen.rules.context.LoadContext;

public class GenderAdapter
{
	private static final FormatManager<Gender> GENDER_MANAGER = new GenderManager();

	public static Gender[] getAvailableGenders()
	{
		return Gender.values();
	}

	public static Indirect<Gender> getGenderReference(LoadContext context, String name)
	{
		return GENDER_MANAGER.convertIndirect(name);
	}
	
	private static class GenderManager implements FormatManager<Gender>
	{

		@Override
		public Gender convert(String inputStr)
		{
			return Gender.getGenderByName(inputStr);
		}

		@Override
		public Indirect<Gender> convertIndirect(String inputStr)
		{
			return new BasicIndirect<>(GENDER_MANAGER, convert(inputStr));
		}

		@Override
		public boolean isDirect()
		{
			return true;
		}

		@Override
		public String unconvert(Gender gender)
		{
			return gender.name();
		}

		@Override
		public Class<Gender> getManagedClass()
		{
			return Gender.class;
		}

		@Override
		public String getIdentifierType()
		{
			return "GENDER";
		}

		@Override
		public FormatManager<?> getComponentManager()
		{
			return null;
		}
		
	}
}
