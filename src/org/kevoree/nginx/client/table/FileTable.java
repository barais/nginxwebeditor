package org.kevoree.nginx.client.table;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * Flex table that can handle double clicks
 */
public class FileTable extends FlexTable {
	
	// FileCell is a way to create a cell
	// cell constructor is protected in HTMLTable.Cell
	// workaround
	class FileCell extends Cell {
		protected FileCell(int rowIndex, int cellIndex) {
			super(rowIndex, cellIndex);
		}
	}
	
	public Cell getCellForEvent(MouseEvent<? extends EventHandler> event) {
        Element td = getEventTargetCell(Event.as(event.getNativeEvent()));
        if (td == null) {
          return null;
        }

        int row = TableRowElement.as(td.getParentElement()).getSectionRowIndex();
        int column = TableCellElement.as(td).getCellIndex();
        return new FileCell(row, column);
    }
}
