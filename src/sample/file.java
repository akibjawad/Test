package sample;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.*;

//import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.io.File;

/**
 * Created by Akib Jawad on 19-Apr-17.
 */
public  class file
{
    private SimpleStringProperty filename;
    private SimpleLongProperty size;
    //private SimpleObjectProperty<ic> icon;
    private Icon icon;
    private SimpleLongProperty dateModified;
    public File f;


    public file(File f,String filename, Icon icon, long size, long datemodified)
    {
        this.filename=new SimpleStringProperty(filename);
        this.size=new SimpleLongProperty(size);
        this.icon= icon;
        this.dateModified = new SimpleLongProperty(datemodified);
        this.f=f;

    }
    public String getFilename() {
        return filename.get();
    }

    public long getSize() {
        return size.get();
    }

    public String getIcon() {
        return icon.toString();
    }

    public long getDateModified() {
        return dateModified.get();
    }

    public void setSize(long size) {
        this.size.set(size);
    }

    public void setFilename(String filename) {
        this.filename.set(filename);
    }
    /*
    public void setIcon(String icon) {
        this.icon.set(icon);
    }   */

    public void setDateModified(long dateModified) {
        this.dateModified.set(dateModified);
    }
}