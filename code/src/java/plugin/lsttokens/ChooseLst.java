/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>, DeferredToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		String key;
		String val;
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (value.startsWith("FEAT="))
		{
			key = "FEAT";
			val = value.substring(5);
		}
		else if (pipeLoc == -1)
		{
			key = value;
			val = null;
		}
		else
		{
			key = value.substring(0, pipeLoc);
			val = value.substring(pipeLoc + 1);
		}
		if (key.startsWith("NUMCHOICES="))
		{
			String maxCount = key.substring(11);
			if (maxCount == null || maxCount.length() == 0)
			{
				Logging.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
						+ value);
				return false;
			}
			Formula f = FormulaFactory.getFormulaFor(maxCount);
			context.obj.put(obj, FormulaKey.NUMCHOICES, f);
			pipeLoc = val.indexOf(Constants.PIPE);
			if (pipeLoc == -1)
			{
				key = val;
				val = null;
			}
			else
			{
				key = val.substring(0, pipeLoc);
				val = val.substring(pipeLoc + 1);
			}
		}

		return context.processSubToken(obj, getTokenName(), key, val);
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String[] str = context.unparse(obj, getTokenName());
		if (str == null)
		{
			return null;
		}
		Formula choices = context.obj.getFormula(obj, FormulaKey.NUMCHOICES);
		String choicesString = choices == null ? null : "NUMCHOICES="
				+ choices.toString() + Constants.PIPE;
		for (int i = 0; i < str.length; i++)
		{
			if (str[i].endsWith(Constants.PIPE))
			{
				str[i] = str[i].substring(0, str[i].length() - 1);
			}
			if (choicesString != null)
			{
				str[i] = choicesString + str[i];
			}
			if (str[i].startsWith("FEAT|"))
			{
				str[i] = "FEAT=" + str[i].substring(5);
			}
		}
		return str;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}

	/*
	 * This makes an editor a bit more difficult, but since CHOOSE is an early
	 * target of 5.17, this probably isn't a big deal.
	 */
	public boolean process(LoadContext context, CDOMObject obj)
	{
		Formula emb = obj.get(FormulaKey.EMBEDDED_SELECT);
		if (emb != null)
		{
			if (!FormulaFactory.ONE.equals(emb))
			{
				obj.put(FormulaKey.SELECT, emb);
			}
		}
		return true;
	}
}
