/*
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
package pcgen.system;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import pcgen.facade.util.AbstractListFacade;

import org.apache.commons.lang3.ArrayUtils;

class RecentFileList extends AbstractListFacade<File>
{

    private static final int MAX_RECENT_FILES = 8;
    private final LinkedList<File> fileList = new LinkedList<>();
    private final String contextProp;

    RecentFileList(String contextProp)
    {
        this.contextProp = contextProp;
        String[] recentFiles = PCGenSettings.getInstance().getStringArray(contextProp);
        if (!ArrayUtils.isEmpty(recentFiles))
        {
            URI userdir = new File(ConfigurationSettings.getUserDir()).toURI();
            for (int i = recentFiles.length - 1;i >= 0;i--)
            {
                addRecentFile(new File(userdir.resolve(recentFiles[i])));
            }
        }
    }

    private void updateRecentFileProp()
    {
        URI userdir = new File(ConfigurationSettings.getUserDir()).toURI();

        List<String> uris = new ArrayList<>(fileList.size());
        fileList.stream().map(file -> userdir.relativize(file.toURI()).toString()).forEach(uris::add);
        PCGenSettings.getInstance().setStringArray(contextProp, uris);
    }

    void addRecentFile(File file)
    {
        if ((file == null) || !file.isFile())
        {
            return;
        }
        //Remove the file if it already exists, that way it gets moved to the top
        int index = indexOf(file);
        if (index != -1)
        {
            File oldFile = fileList.remove(index);
            fireElementRemoved(this, oldFile, index);
        }
        //add it to the front
        fileList.addFirst(file);
        fireElementAdded(this, file, 0);
        //then remove any overflowing files
        if (fileList.size() > MAX_RECENT_FILES)
        {
            File oldFile = fileList.removeLast();
            fireElementRemoved(this, oldFile, MAX_RECENT_FILES);
        }
        updateRecentFileProp();
    }

    @Override
    public File getElementAt(int index)
    {
        return fileList.get(index);
    }

    @Override
    public int getSize()
    {
        return fileList.size();
    }

    @Override
    public boolean containsElement(File element)
    {
        return indexOf(element) != -1;
    }

    private int indexOf(File element)
    {
        if (element != null)
        {
            return IntStream.range(0, fileList.size())
                    .filter(i -> fileList.get(i).getAbsolutePath().equals(element.getAbsolutePath())).findFirst()
                    .orElse(-1);
        }
        return -1;
    }

}
