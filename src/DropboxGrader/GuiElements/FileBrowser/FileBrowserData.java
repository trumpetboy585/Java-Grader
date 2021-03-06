/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package DropboxGrader.GuiElements.FileBrowser;

import DropboxGrader.FileManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Matt
 */
public class FileBrowserData extends AbstractTableModel{
    private FileManager manager;
    private HashMap<CellLocation,Color> cellColors;
    private ArrayList<Integer> hiddenCols;
    public FileBrowserData(FileManager f){
        super();
        manager=f;
        
        cellColors=new HashMap();
        hiddenCols=new ArrayList();
    }
    @Override
    public int getRowCount() {
        int count=manager.getNumFiles();
        if(count==0){
            count=1;
        }
        return count;
    }

    @Override
    public int getColumnCount() {
        int count=manager.getNumAttributes();
        
        return count;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(hiddenCols.contains(columnIndex)){
            return "Hiden";
        }
        String value=manager.getFileInfo(rowIndex, columnIndex);
        if(value==null)
            value="No files found.";
        return value;
    }
    @Override
    public String getColumnName(int col){
        return manager.getAttributes()[col];
    }
    public void setColorAt(Color c,CellLocation cell){
        cellColors.put(cell, c);
    }
    public Color getColorAt(CellLocation cell){
        return cellColors.get(cell);
    }
    public FileManager getManager(){
        return manager;
    }
    public void refresh(){
        cellColors.clear();
    }
    public void hideCol(int col){
        if(!hiddenCols.contains(col)){
            hiddenCols.add(col);
        }
    }
    public boolean isHidden(int col){
        return hiddenCols.contains(col);
    }
    public void unhideCol(int col){
        hiddenCols.remove((Integer)col);
    }
}
