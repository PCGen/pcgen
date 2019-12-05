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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pcgen.core.PlayerCharacter;
import pcgen.core.character.EquipSet;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Implements a custom Freemarker macro to loop over the characte's equipment
 * sets
 *
 * <p>Parameters: None</p>
 *
 * <p>Nested content is output once for each loop</p>
 */
public class EquipSetLoopDirective implements TemplateDirectiveModel
{
    private PlayerCharacter pc;

    /**
     * Create a new instance of EquipSetLoopDirective
     *
     * @param pc The character being exported.
     */
    public EquipSetLoopDirective(PlayerCharacter pc)
    {
        this.pc = pc;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException
    {
        if (body == null)
        {
            throw new TemplateModelException("This directive must have content.");
        }

        List<EquipSet> eqSetList = new ArrayList<>();
        EquipSet currSet = null;
        String currIdPath = pc.getCalcEquipSetId();
        for (EquipSet es : pc.getDisplay().getEquipSet())
        {
            if (es.getParentIdPath().equals("0"))
            {
                eqSetList.add(es);
                if (es.getIdPath().equals(currIdPath))
                {
                    currSet = es;
                }
            }
        }

        for (EquipSet equipSet : eqSetList)
        {
            pc.setCalcEquipSetId(equipSet.getIdPath());
            pc.setCalcEquipmentList(equipSet.getUseTempMods());

            // Executes the nested body (same as <#nested> in FTL). In this
            // case we don't provide a special writer as the parameter:
            body.render(env.getOut());
        }

        if (currSet != null)
        {
            pc.setCalcEquipSetId(currSet.getIdPath());
            pc.setCalcEquipmentList(currSet.getUseTempMods());
        }
    }

}
