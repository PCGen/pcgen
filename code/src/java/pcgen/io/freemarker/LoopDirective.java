/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 *
 */
package pcgen.io.freemarker;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/**
 * Implements a custom Freemarker macro to loop a certain number of times. Unlike 
 * the inbuilt loop directive it can loop zero times. 
 * 
 * <p>Parameters</p>
 * <ul>
 * <li><b>from</b> (optional) - The starting value, defaults to 0.</li>
 * <li><b>to</b> - The ending value (inclusive). If this is less than from then 
 * the contents will not be output.
 * <li><b>step</b> (optional) - The amount to increment b each loop, defaults to 1.</li>
 * </ul>
 * 
 * <p>In addition up to two loopvars may be specified. The first will be populated 
 *  with the current index value of the loop and the second will be a boolean 
 *  indicating if there are more iterations of the loop to go.</p>
 * 
 * <p>Nested content is output once for each loop</p>
 * 
 * See http://freemarker.org/docs/pgui_datamodel_directive.html#autoid_37
 * 
 */
public class LoopDirective implements TemplateDirectiveModel
{
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
		throws TemplateException, IOException
	{
		// Check if no parameters were given:
		int fromVal = 0;
		Integer toVal = null;
		int step = 1;

		for (Object entryObj : params.entrySet())
		{
			Map.Entry entry = (Entry) entryObj;
			String paramName = (String) entry.getKey();
			TemplateModel paramValue = (TemplateModel) entry.getValue();

			switch (paramName)
			{
				case "from" -> {
					if (!(paramValue instanceof TemplateNumberModel))
					{
						throw new TemplateModelException("The \"" + paramName + "\" parameter " + "must be a number.");
					}
					fromVal = ((TemplateNumberModel) paramValue).getAsNumber().intValue();
				}
				case "to" -> {
					if (!(paramValue instanceof TemplateNumberModel))
					{
						throw new TemplateModelException("The \"" + paramName + "\" parameter " + "must be a number.");
					}
					toVal = ((TemplateNumberModel) paramValue).getAsNumber().intValue();
				}
				case "step" -> {
					if (!(paramValue instanceof TemplateNumberModel))
					{
						throw new TemplateModelException("The \"" + paramName + "\" parameter " + "must be a number.");
					}
					step = ((TemplateNumberModel) paramValue).getAsNumber().intValue();
					if (step == 0)
					{
						throw new TemplateModelException("The \"" + paramName + "\" parameter must not be 0.");
					}
				}
				default -> {
				}
				//Case not caught, should this cause an error?
			}
		}

		if (toVal == null)
		{
			throw new TemplateModelException("The \"to\" parameter must be provided.");
		}
		if (body == null)
		{
			throw new TemplateModelException("This directive must have content.");
		}

		if (step > 0)
		{
			for (int i = fromVal; i <= toVal; i += step)
			{

				// Set the loop variable, if there is one:
				if (loopVars.length > 0)
				{
					loopVars[0] = new SimpleNumber(i);
				}
				if (loopVars.length > 1)
				{
					loopVars[1] = i + step <= toVal ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
				}

				// Executes the nested body (same as <#nested> in FTL). In this
				// case we don't provide a special writer as the parameter:
				body.render(env.getOut());
			}
		}
		else
		{
			for (int i = fromVal; i >= toVal; i += step)
			{

				// Set the loop variable, if there is one:
				if (loopVars.length > 0)
				{
					loopVars[0] = new SimpleNumber(i);
				}
				if (loopVars.length > 1)
				{
					loopVars[1] = i + step >= toVal ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
				}

				// Executes the nested body (same as <#nested> in FTL). In this
				// case we don't provide a special writer as the parameter:
				body.render(env.getOut());
			}
		}

	}

}
