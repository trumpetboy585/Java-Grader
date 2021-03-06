/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DropboxGrader.GuiElements.FileBrowser;

import DropboxGrader.Config;
import DropboxGrader.DbxFile;
import DropboxGrader.Gui;
import DropboxGrader.GuiElements.FileBrowser.FileBrowserTable;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.JTableHeader;

/**
 *
 * @author 141lyonsm
 */
public class FileBrowserListener implements ActionListener,MouseListener,RowSorterListener{
    private FileBrowserTable table;
    private Gui gui;
    private long lastClick;
    private int lastRow;
    private final long DOUBLECLICKDELAY=250;
    public FileBrowserListener(Gui gui){
        this.gui=gui;
        lastClick=DOUBLECLICKDELAY*-1;
        lastRow=-1;
    }
    public void setTable(FileBrowserTable t){
        table=t;
    }
    private JPopupMenu createRightClickMenu(int row){
        JPopupMenu m=new JPopupMenu();
        JMenuItem m1=new JMenuItem("Rename");
        JMenuItem m2=new JMenuItem("ReDownload");
        m1.setActionCommand("Rename"+row);
        m1.addActionListener(this);
        m2.setActionCommand("ReDownload"+row);
        m2.addActionListener(this);
        m.add(m1);
        if(gui.getManager().getFile(row).isDownloaded()){
            m.add(m2);
        }
        return m;
    }
    private JPopupMenu createHeaderMenu(int col){
        JPopupMenu m=new JPopupMenu();
        JMenuItem m1=new JMenuItem("Hide");
        if(table.colIsHidden(col))
            m1.setText("UnHide");
        m1.setActionCommand("Hide"+col);
        m1.addActionListener(this);
        m.add(m1);
        return m;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!table.getRowSelectionAllowed()&&!(e.getComponent() instanceof JTableHeader))
            return;
        if(e.getButton()==MouseEvent.BUTTON1){ //handle doubleclick
            if(e.isAltDown()||e.isControlDown()||e.isMetaDown()||e.isShiftDown()||e.getComponent() instanceof JTableHeader){
                return;
            }
            long currentClick=System.currentTimeMillis();
            int selectedRow=table.getSelectedRow();
            if(selectedRow==-1){
                return;
            }
            if(currentClick-lastClick<=DOUBLECLICKDELAY&&lastRow==table.convertRowIndexToModel(selectedRow)){
                gui.gradeRows();
                lastClick=DOUBLECLICKDELAY*-1;
                lastRow=-2;
            }
            else{
                lastClick=currentClick;
                lastRow=table.convertRowIndexToModel(selectedRow);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!table.getRowSelectionAllowed())
            return;
        if(e.getButton()==MouseEvent.BUTTON3){
            int r = table.rowAtPoint(e.getPoint());
            if (r >= 0 && r < table.getRowCount()) {
                table.setRowSelectionInterval(r, r);
            } else {
                table.clearSelection();
            }

            int rowindex = table.convertRowIndexToModel(table.getSelectedRow());
            if (rowindex < 0)
                return;
            if (e.getComponent() instanceof JTable ) {
                JPopupMenu popup = createRightClickMenu(rowindex);
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
            else if(e.getComponent() instanceof JTableHeader){
                JTableHeader header=(JTableHeader)e.getComponent();
                int col=header.columnAtPoint(e.getPoint());
                JPopupMenu popup= createHeaderMenu(col);
                popup.show(e.getComponent(),e.getX(),e.getY());
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!table.getRowSelectionAllowed())
            return;
        if(e.getActionCommand().contains("Rename")){
            table.setRowSelectionAllowed(false);
            int f=Integer.parseInt(e.getActionCommand().replace("Rename", ""));
            DbxFile file=gui.getManager().getFile(f);
            String choice=JOptionPane.showInputDialog("What would you like to name the file?",file.getFileName());
            if(choice!=null){
                file.rename(choice);
                gui.setupFileBrowserGui();
            }
            else{
                table.setRowSelectionAllowed(true);
            }
            table.dataChanged();
        }
        else if(e.getActionCommand().contains("ReDownload")){
            int f=Integer.parseInt(e.getActionCommand().replace("ReDownload", ""));
            DbxFile file=gui.getManager().getFile(f);
            file.forceDownload();
            
            table.dataChanged();
        }
        else if(e.getActionCommand().contains("Hide")){
            int col=Integer.parseInt(e.getActionCommand().replace("Hide", ""));
            if(!table.colIsHidden(col))
                table.hideCol(col);
            else
                table.unhideCol(col);
            
            table.repaint();
            table.revalidate();
        }
    }

    @Override
    public void sorterChanged(RowSorterEvent e) {
        List<? extends SortKey> keys=table.getRowSorter().getSortKeys();
        if(keys.isEmpty()){
            return;
        }
        Config.sortOrder=keys.get(0).getSortOrder().toString();
        Config.sortColumn=keys.get(0).getColumn()+"";
    }
    
}
