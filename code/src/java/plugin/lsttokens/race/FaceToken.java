package plugin.lsttokens.race;

import java.math.BigDecimal;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.BigDecimalHelper;
import pcgen.util.Logging;

/**
 * Class deals with FACE Token
 */
public class FaceToken implements CDOMPrimaryToken<Race>
{

	public String getTokenName()
	{
		return "FACE";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		return parseFace(context, race, value);
	}

	protected boolean parseFace(LoadContext context, Race race, String value)
	{
		int commaLoc = value.indexOf(Constants.COMMA);
		if (commaLoc != value.lastIndexOf(Constants.COMMA))
		{
			Logging.errorPrint(getTokenName() + " must be of the form: "
					+ getTokenName() + ":<num>[,<num>]");
			return false;
		}
		if (commaLoc > -1)
		{
			if (commaLoc == 0)
			{
				Logging
						.errorPrint(getTokenName()
								+ " should not start with a comma.  Must be of the form: "
								+ getTokenName() + ":<num>[,<num>]");
				return false;
			}
			if (commaLoc == value.length() - 1)
			{
				Logging
						.errorPrint(getTokenName()
								+ " should not end with a comma.  Must be of the form: "
								+ getTokenName() + ":<num>[,<num>]");
				return false;
			}
			try
			{
				String widthString = value.substring(0, commaLoc).trim();
				BigDecimal width = new BigDecimal(widthString);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative width in "
							+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().put(race, ObjectKey.FACE_WIDTH,
						width);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood Double Width in Tag: "
						+ value);
				return false;
			}

			try
			{
				String heightString = value.substring(commaLoc + 1).trim();
				BigDecimal height = new BigDecimal(heightString);
				if (height.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative height in "
							+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().put(race, ObjectKey.FACE_HEIGHT,
						height);
			}
			catch (NumberFormatException ne)
			{
				Logging.errorPrint("Misunderstood Double Height in Tag: "
						+ value);
				return false;
			}
		}
		else
		{
			try
			{
				String widthString = value;
				BigDecimal width = new BigDecimal(widthString);
				if (width.compareTo(BigDecimal.ZERO) < 0)
				{
					Logging.errorPrint("Cannot have negative width in "
							+ getTokenName() + ": " + value);
					return false;
				}
				context.getObjectContext().put(race, ObjectKey.FACE_WIDTH,
						width);
				context.getObjectContext().put(race, ObjectKey.FACE_HEIGHT,
						BigDecimal.ZERO);
			}
			catch (NumberFormatException nfe)
			{
				Logging.errorPrint("Misunderstood Double in Tag: " + value);
				return false;
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		BigDecimal width = context.getObjectContext().getObject(race,
				ObjectKey.FACE_WIDTH);
		BigDecimal height = context.getObjectContext().getObject(race,
				ObjectKey.FACE_HEIGHT);
		if (width == null && height == null)
		{
			return null;
		}
		if (width == null || height == null)
		{
			context.addWriteMessage("Must have both width and height in "
					+ getTokenName() + ": " + width + " " + height);
			return null;
		}
		if (width.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative width in "
					+ getTokenName() + ": " + width);
			return null;
		}
		if (height.compareTo(BigDecimal.ZERO) < 0)
		{
			context.addWriteMessage("Cannot have negative height in "
					+ getTokenName() + ": " + height);
			return null;
		}
		StringBuilder sb = new StringBuilder();
		BigDecimal w = BigDecimalHelper.trimBigDecimal(width);
		sb.append(w);
		if (height.compareTo(BigDecimal.ZERO) != 0)
		{
			BigDecimal h = BigDecimalHelper.trimBigDecimal(height);
			sb.append(',').append(h);
		}
		return new String[] { sb.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

}
