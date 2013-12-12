/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DropboxGrader;

import java.io.File;


/**
 *
 * @author 141lyonsm
 */
public class TextFile extends File{
    private String text;
    public TextFile(File f){
        super(f.getPath());
        
        read();
    }
    private void read(){
        text=DbxSession.readFromFile(this);
    }
    public String getText(){
        return text;
    }
}
