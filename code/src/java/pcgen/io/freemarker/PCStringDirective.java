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
import java.util.List;
import java.util.Map;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Implements a custom Freemarker macro to allow exporting of a string value
 * from the character. It evaluates a PCGen export token for the current character
 * and returns the value as a string. e.g. {@literal <@pcstring tag="PLAYERNAME"/>} or
 * ${pcstring('PLAYERNAME')}
 */
public class PCStringDirective implements TemplateDirectiveModel, TemplateMethodModelEx, CharacterExportAction
{
    private PlayerCharacter pc;
    private ExportHandler eh;

    /**
     * Create a new instance of PCStringDirective
     *
     * @param pc The character being exported.
     * @param eh The managing export handler.
     */
    public PCStringDirective(PlayerCharacter pc, ExportHandler eh)
    {
        this.pc = pc;
        this.eh = eh;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws IOException, TemplateModelException
    {
        // Check if no parameters were given:
        if (params.size() != 1 || params.get("tag") == null)
        {
            throw new TemplateModelException("This directive requires a single tag parameter.");
        }
        if (loopVars.length != 0)
        {
            throw new TemplateModelException("This directive doesn't allow loop variables.");
        }
        if (body != null)
        {
            throw new TemplateModelException("This directive cannot take a body.");
        }

        String tag = params.get("tag").toString();
        String value = getExportVariable(tag, pc, eh);

        if (tag.equals(value))
        {
            throw new TemplateModelException("Invalid export tag '" + tag + "'.");
        }

        env.getOut().append(value);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object exec(List arg0) throws TemplateModelException
    {
        if (arg0.size() != 1)
        {
            throw new TemplateModelException("Wrong arguments. tag required");
        }

        String tag = arg0.get(0).toString();
        String value = getExportVariable(tag, pc, eh);

        if (tag.equals(value))
        {
            throw new TemplateModelException("Invalid export tag '" + tag + "'.");
        }
        return value;
    }
}
