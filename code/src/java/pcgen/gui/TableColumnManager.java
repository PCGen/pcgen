package pcgen.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableColumnManager implements MouseListener {
	private JPopupMenu tablePopup;
	private List checkBoxList;
	private List columnList;
	private List settingList;
	private int columnOffset = 0;
	private JTable table;
	private JComponent tableButton;
	
	public TableColumnManager(JTable table, JComponent tableButton, List columnList, List settingList, int columnOffset) {
		this.table = table;
		this.tableButton = tableButton;
		this.columnList = columnList;
		this.settingList = settingList;
		this.columnOffset = columnOffset;
		initContents();
	}
	
	private void initContents() {
		tablePopup = new JPopupMenu();
		checkBoxList = new ArrayList();
		
		tablePopup = new javax.swing.JPopupMenu();
		for(int i = 0; i < columnList.size(); i++) {
			String name = (String)columnList.get(i);
			boolean selected = ((Boolean)settingList.get(i)).booleanValue();
			JCheckBoxMenuItem popupCb = new JCheckBoxMenuItem();
			tablePopup.add(popupCb);
			popupCb.setText(name);
			popupCb.setSelected(selected);
			popupCb.addActionListener(new PopupActionListener(popupCb, i + columnOffset));
			checkBoxList.add(popupCb);
		}
		tableButton.addMouseListener(this);
	}

	public void tableDisplay(java.awt.event.MouseEvent evt) {
		tablePopup.show(evt.getComponent(), evt.getX(), evt.getY());
	}
	
	private void TablePopupActionPerformed() {
		TableColumnModel colModel = table.getColumnModel();
		while(colModel.getColumnCount() > 1) {
			TableColumn col = colModel.getColumn(1);
			colModel.removeColumn(col);
		}
		for(int i = 0; i < checkBoxList.size(); i++) {
			JCheckBoxMenuItem cb = (JCheckBoxMenuItem)checkBoxList.get(i);
			if(cb.isSelected()) {
				TableColumn col = new TableColumn(i + columnOffset);
				col.setHeaderValue(cb.getText());
				colModel.addColumn(col);
			}
		}
	}
	
	private class PopupActionListener implements ActionListener {
		JCheckBoxMenuItem popupCb;
		int colNo = 0;
		public PopupActionListener(JCheckBoxMenuItem popupCb, int colNo) {
			this.popupCb = popupCb;
			this.colNo = colNo;
		}

		public void actionPerformed(ActionEvent e) {
			TablePopupActionPerformed();
		}

	}

	public void mouseClicked(MouseEvent e) {
		tableDisplay(e);
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		tableDisplay(e);
	}

	public void mouseReleased(MouseEvent e) {
		tableDisplay(e);
	}

}
