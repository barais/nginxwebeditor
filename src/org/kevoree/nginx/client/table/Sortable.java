package org.kevoree.nginx.client.table;

/**
 * Sortable columns in a table
 *
 */
public interface Sortable {
	// Constants defining the current direction of the 
	// sort on a column
	public static int SORT_ASC = 0;
	public static int SORT_DESC = 1;
	
	
	/**	  
	 * Defines what happens when the column is sorted
	 * 
	 * @param columnIndex to be sorted (int)
	 */
	public void sort(int columnIdx);
}
