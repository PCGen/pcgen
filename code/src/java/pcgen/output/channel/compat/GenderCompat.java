package pcgen.output.channel.compat;

import java.util.Optional;

import pcgen.base.util.BasicIndirect;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.enumeration.Gender;
import pcgen.rules.context.LoadContext;

/**
 * GenderCompat contains utility methods for communication of the PCs Gender through a
 * channel.
 */
public final class GenderCompat
{

    private GenderCompat()
    {
        //Do not instantiate utility class
    }

    private static final FormatManager<Gender> GENDER_MANAGER = new GenderManager();

    /**
     * Returns an array of the available Gender objects.
     *
     * @return An array of the available Gender objects
     */
    public static Gender[] getAvailableGenders()
    {
        return Gender.values();
    }

    /**
     * Returns an Indirect containing the Gender of the specified name.
     *
     * @param context The LoadContext in which the Gender should be resolved
     * @param name    The name of the Gender
     * @return An Indirect containing the Gender of the specified name
     */
    public static Indirect<Gender> getGenderReference(LoadContext context, String name)
    {
        return GENDER_MANAGER.convertIndirect(name);
    }

    /**
     * A (temporary) FormatManager for Gender to help with compatibility.
     */
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
        public Optional<FormatManager<?>> getComponentManager()
        {
            return Optional.empty();
        }

    }
}
