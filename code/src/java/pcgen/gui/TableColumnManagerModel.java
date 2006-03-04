package pcgen.gui;

import java.util.List;

public interface TableColumnManagerModel {
	public List getMColumnList();
	public boolean isMColumnDisplayed(int col);
	public void setMColumnDisplayed(int col, boolean disp);
	public int getMColumnOffset();
}
