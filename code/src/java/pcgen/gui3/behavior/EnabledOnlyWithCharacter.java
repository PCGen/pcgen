/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3.behavior;

import java.io.File;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;
import pcgen.gui2.PCGenFrame;

import javafx.scene.Node;

public final class EnabledOnlyWithCharacter implements ReferenceListener<CharacterFacade>
{
    private final ReferenceListener<File> fileListener = new FileRefListener();
    private final Node node;

    public EnabledOnlyWithCharacter(Node node, PCGenFrame frame)
    {
        this.node = node;
        ReferenceFacade<CharacterFacade> ref = frame.getSelectedCharacterRef();
        ref.addReferenceListener(this);
        checkEnabled(ref.get());
    }

    @Override
    public void referenceChanged(ReferenceEvent<CharacterFacade> e)
    {
        CharacterFacade oldRef = e.getOldReference();
        if (oldRef != null)
        {
            oldRef.getFileRef().removeReferenceListener(fileListener);
        }
        checkEnabled(e.getNewReference());
    }

    private void checkEnabled(CharacterFacade character)
    {
        if (character != null)
        {
            ReferenceFacade<File> file = character.getFileRef();
            file.addReferenceListener(fileListener);
            node.setDisable(file.get() == null);
        } else
        {
            node.setDisable(true);
        }
    }

    private class FileRefListener implements ReferenceListener<File>
    {

        @Override
        public void referenceChanged(ReferenceEvent<File> e)
        {
            node.setDisable(e.getNewReference() == null);
        }

    }
}
