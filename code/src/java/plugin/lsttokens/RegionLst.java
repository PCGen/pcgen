/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcreteTransitionChoice;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.SimpleChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Region;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * @author djones4
 *
 */
public class RegionLst extends AbstractTokenWithSeparator<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, ChoiceActor<Region>
{
	@Override
	public String getTokenName()
	{
		return "REGION";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		CDOMObject pcc, String value)
	{
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String item = tok.nextToken();
		Formula count = FormulaFactory.getFormulaFor(item);
		if (!count.isValid())
		{
			return new ParseResult.Fail("Count in " + getTokenName()
					+ " was not valid: " + count.toString());
		}
		if (count.isStatic())
		{
			if (!tok.hasMoreTokens())
			{
				return new ParseResult.Fail(getTokenName()
						+ " cannot have only a count: " + value);
			}
			item = tok.nextToken();
			if (count.resolve(null, "").intValue() <= 0)
			{
				return new ParseResult.Fail("Count in "
						+ getTokenName() + " must be > 0: " + value);
			}
		}
		else
		{
			count = FormulaFactory.ONE;
		}
		List<Region> regions = new ArrayList<Region>();
		while (true)
		{
			regions.add(Region.getConstant(item));
			if (!tok.hasMoreTokens())
			{
				break;
			}
			item = tok.nextToken();
		}
		SimpleChoiceSet<Region> rcs = new SimpleChoiceSet<Region>(regions);
		ChoiceSet<Region> cs = new ChoiceSet<Region>(getTokenName(), rcs);
		cs.setTitle("Region Selection");
		TransitionChoice<Region> tc = new ConcreteTransitionChoice<Region>(cs, count);
		context.obj.put(pcc, ObjectKey.REGION_CHOICE, tc);
		tc.setRequired(false);
		tc.setChoiceActor(this);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject pcc)
	{
		TransitionChoice<Region> tc = context.getObjectContext().getObject(pcc,
				ObjectKey.REGION_CHOICE);
		if (tc == null)
		{
			// indicates no Token
			return null;
		}
		StringBuilder sb = new StringBuilder();
		Formula count = tc.getCount();
		if (!FormulaFactory.ONE.equals(count))
		{
			sb.append(count);
			sb.append(Constants.PIPE);
		}
		sb.append(tc.getChoices().getLSTformat().replaceAll(Constants.COMMA,
				Constants.PIPE));
		return new String[] { sb.toString() };
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(CDOMObject owner, Region choice, PlayerCharacter pc)
	{
		if (!pc.getRegionString().equalsIgnoreCase(choice.toString()))
		{
			pc.setRegion(choice);
		}
	}

	@Override
	public boolean allow(Region choice, PlayerCharacter pc, boolean allowStack)
	{
		return true;
	}

	@Override
	public List<Region> getCurrentlySelected(CDOMObject owner,
			PlayerCharacter pc)
	{
		return Collections.emptyList();
	}
}
