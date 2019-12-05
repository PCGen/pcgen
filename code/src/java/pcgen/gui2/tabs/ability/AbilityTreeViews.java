/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.gui2.tabs.ability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.gui2.util.treeview.TreeView;
import pcgen.gui2.util.treeview.TreeViewPath;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

public final class AbilityTreeViews
{

    private AbilityTreeViews()
    {
    }

    public static List<TreeView<AbilityFacade>> createTreeViewList(CharacterFacade character)
    {
        List<TreeView<AbilityFacade>> list = new ArrayList<>();
        list.add(new NameTreeView());
        list.add(new TypeTreeView());
        list.add(new PreReqTreeView(character.getDataSet()));
        list.add(new SourceTreeView());
        return list;
    }

    private static class NameTreeView implements TreeView<AbilityFacade>
    {

        @Override
        public String getViewName()
        {
            return LanguageBundle.getString("in_nameLabel"); //$NON-NLS-1$
        }

        @Override
        public List<TreeViewPath<AbilityFacade>> getPaths(AbilityFacade pobj)
        {
            return Collections.singletonList(new TreeViewPath<>(pobj));
        }

    }

    private static class TypeTreeView implements TreeView<AbilityFacade>
    {

        @Override
        public String getViewName()
        {
            return LanguageBundle.getString("in_typeName"); //$NON-NLS-1$
        }

        @Override
        public List<TreeViewPath<AbilityFacade>> getPaths(AbilityFacade pobj)
        {
            List<TreeViewPath<AbilityFacade>> list = new ArrayList<>();
            List<String> types = pobj.getTypes();
            if (types.isEmpty())
            {
                list.add((new TreeViewPath<>(pobj)));
            } else
            {
                for (String type : types)
                {
                    list.add(new TreeViewPath<>(pobj, type));
                }
            }
            return list;
        }

    }

    private static class SourceTreeView implements TreeView<AbilityFacade>
    {

        @Override
        public String getViewName()
        {
            return LanguageBundle.getString("in_sourceName"); //$NON-NLS-1$
        }

        @Override
        public List<TreeViewPath<AbilityFacade>> getPaths(AbilityFacade pobj)
        {
            return Collections.singletonList(new TreeViewPath<>(pobj, pobj.getSourceForNodeDisplay()));
        }

    }

    private static class PreReqTreeView implements TreeView<AbilityFacade>
    {

        private final DataSetFacade dataset;

        public PreReqTreeView(DataSetFacade dataset)
        {
            this.dataset = dataset;
        }

        @Override
        public String getViewName()
        {
            return LanguageBundle.getString("in_preReqTree"); //$NON-NLS-1$
        }

        @Override
        public List<TreeViewPath<AbilityFacade>> getPaths(AbilityFacade pobj)
        {
            List<List<AbilityFacade>> abilityPaths = new ArrayList<>();
            addPaths(abilityPaths, dataset.getPrereqAbilities(pobj), new ArrayList<>());
            if (abilityPaths.isEmpty())
            {
                return Collections.singletonList(new TreeViewPath<>(pobj));
            }

            List<TreeViewPath<AbilityFacade>> paths = new ArrayList<>();
            for (List<AbilityFacade> path : abilityPaths)
            {
                Collections.reverse(path);
                paths.add(new TreeViewPath<AbilityFacade>(path.toArray(), pobj));
            }
            return paths;
        }

        private void addPaths(List<List<AbilityFacade>> abilityPaths, List<AbilityFacade> preAbilities,
                ArrayList<AbilityFacade> path)
        {
            if (path.size() > 20)
            {

                Logging.errorPrint("Found probable ability prereq cycle [" + StringUtils.join(path, ",")
                        + "] with prereqs [" + StringUtils.join(preAbilities, ",") + "]. Skipping.");
                return;
            }
            for (AbilityFacade preAbility : preAbilities)
            {
                @SuppressWarnings("unchecked")
                ArrayList<AbilityFacade> pathclone = (ArrayList<AbilityFacade>) path.clone();
                pathclone.add(preAbility);
                List<AbilityFacade> preAbilities2 = dataset.getPrereqAbilities(preAbility);
                // Don't include self references in the path
                preAbilities2.remove(preAbility);
                preAbilities2.removeAll(pathclone);
                if (preAbilities2.isEmpty())
                {
                    abilityPaths.add(pathclone);
                } else
                {
                    addPaths(abilityPaths, preAbilities2, pathclone);
                }
            }
        }

    }

}
