package sample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.omg.CORBA.Object;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import static javafx.geometry.Pos.*;

public class Main extends Application {
    TileOrDetais mode=TileOrDetais.getInstance();
    private final Node rootIcon= new ImageView(new Image(getClass().getResourceAsStream("This_PC.png")));
    private File[] paths;
    private File[] files;
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private TableView<file> table=new TableView<>();
    public ObservableList<file> data= FXCollections.observableArrayList();
    private File CurrDir=new File(System.getProperty("user.dir"));//fsv.getParentDirectory(File.listRoots()[0]);
    public TilePane tp = new TilePane();
    public HBox outBox  = new HBox();
    public HBox innerhBox=new HBox();
    public VBox outterVBox=new VBox();
    public GridPane gp=new GridPane();
    public Label CD=new Label("Current Directory:");
    public Text val=new Text();
    public Button viewChoose=new Button("Tile View");
    public Button back=new Button("Back");
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("File Explorer");
        outBox.setSpacing(10);
        outBox.autosize();
        //Start tree view
        paths=File.listRoots();
        TreeItem<File> rootItem= new TreeItem<File>(fsv.getParentDirectory(paths[0]),rootIcon);
        /*if(paths!=null)
        {
            for (File f : paths)
            {//for tree view
                TreeItem<File> temp=new TreeItem<>(f);
                rootItem.getChildren().add(temp);
            }
        }*/
        rootItem.setExpanded(true);
        TreeView<File> tree = new TreeView<File>(rootItem);

        //for innerHbox

        val.setText(CurrDir.toString());//(fsv.getParentDirectory(File.listRoots()[0])).toString());

        //System.out.println(fsv.getParentDirectory(File.listRoots()[0]));
        viewChoose.setAlignment(TOP_RIGHT);
        viewChoose.setId("tv");

        innerhBox.getChildren().addAll(CD,val,back,viewChoose);
        innerhBox.autosize();

        TableColumn<file,File> fc =new TableColumn<>("file");
        fc.setMaxWidth(5);
        fc.setCellValueFactory(new PropertyValueFactory<file,File>("f"));

        TableColumn<file,String> FileName =new TableColumn<>("FileName");
        FileName.setMinWidth(200);
        FileName.setCellValueFactory(new PropertyValueFactory<file,String>("filename"));

        TableColumn<file,Icon> Icon = new TableColumn<>("Icon");
        Icon.setMinWidth(200);
        Icon.setCellValueFactory(new PropertyValueFactory<file,Icon>("icon"));

        TableColumn<file,Long> Size = new TableColumn<>("Size");
        Size.setMinWidth(200);
        Size.setCellValueFactory(new PropertyValueFactory<file,Long>("size"));

        TableColumn<file,Date> ModifyDate = new TableColumn<file,Date>("Modify Date");
        ModifyDate.setMinWidth(200);
        ModifyDate.setCellValueFactory(new PropertyValueFactory<file,Date>("dateModified"));

        table.getColumns().addAll(FileName,Icon,Size,ModifyDate);


        table.setEditable(true);
        //default tile and table view
        File fp =fsv.getParentDirectory(File.listRoots()[0]);
        tileView(CurrDir);


        gp.getChildren().add(table);
        if(mode.mode=="Details")
            outterVBox.getChildren().addAll(innerhBox, gp);
        else
            outterVBox.getChildren().addAll(innerhBox, tp);
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<File>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<File>> observable, TreeItem<File> oldValue, TreeItem<File> newValue) {
                data.clear();
                table.getItems().clear();
                tp.getChildren().clear();
                TreeItem<File> selectedItem =(TreeItem<File>) newValue;
                selectedItem.getChildren().clear();
                System.out.println("Selecetd Item: "+selectedItem.getValue());
                File file=selectedItem.getValue();
                //for tile view and table view
                CurrDir=file;
                tileView(CurrDir);
                val.setText(CurrDir.toString());

                files=file.listFiles();
                if(files!=null)
                {
                    for (File f : files)
                    {
                        //for tree view
                        TreeItem<File> temp=new TreeItem<>(f);
                        selectedItem.getChildren().add(temp);
                    }

                }
                else
                {
                    System.out.println(fsv.getParentDirectory(file).toString()+"is empty diectory");
                }
            }
        });
        table.setRowFactory( tv -> {
            TableRow<file> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    file f = row.getItem();
                    CurrDir = f.f;
                    val.setText(CurrDir.toString());
                    table.getItems().clear();
                    data.clear();
                    tileView(CurrDir);
                    System.out.println("Selected file "+f.getFilename());
                    //val.setText(f.f.toString());
                    /*
                    files=f.f.listFiles();
                    if(files!=null)
                    {
                        for (File temp : files)
                        {
                            data.add(new file(temp,temp.getName(), fsv.getSystemIcon(temp), temp.length(), temp.lastModified()));
                            table.setItems(data);
                        }
                    }
                    else
                    {
                        System.out.println(f.f.getName()+" is empty diectory");
                    }*/
                }
            });
            return row ;
        });

        /*
        files=fp.listFiles();
        if(files!=null)
        {
            for (File fi : files) {
                Label title = new Label(fi.getName());
                ImageView imageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(fi)));
                //TilePane.setAlignment(title, BOTTOM_RIGHT);
                VBox vb=new VBox();
                vb.getChildren().addAll(imageview,title);
                tp.getChildren().addAll(vb);
                vb.setOnMouseClicked(event -> {
                    if(event.getClickCount()==2)
                    {
                        CurrDir=fi;

                    }
                });
            }
        }
        else
            System.out.println(fp.getName()+" is empty Directory");
            */
        viewChoose.setOnMouseClicked((MouseEvent event) ->{
            if(mode.mode=="Details")
            {
                mode.mode="Tile View";
                viewChoose.setText("Details");
                viewChoose.setId("dt");
                //mode.mode="Details";
                outterVBox.getChildren().removeAll(gp);
                outterVBox.getChildren().add(tp);
            }
            else
            {
                mode.mode="Details";
                outterVBox.getChildren().removeAll(tp);
                outterVBox.getChildren().add(gp);
                //gp.getChildren().add(table);
                viewChoose.setId("tv");
                viewChoose.setText("Tile View");
            }
        });
        back.setOnMouseClicked((MouseEvent event)->
        {
            if(CurrDir!=fsv.getParentDirectory(File.listRoots()[0])) {

                CurrDir = fsv.getParentDirectory(CurrDir);
                val.setText(CurrDir.toString());
                tileView(CurrDir);
            }
        });
        outBox.getChildren().add(tree);
        outBox.getChildren().add(outterVBox);
        outBox.autosize();
        //root.getChildren().add(table);
        primaryStage.setScene(new Scene(outBox, 1920, 1080));
        primaryStage.show();
    }

    public static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    public void tileView(File currFile)
    {
        data.clear();
        table.getItems().clear();
        tp.getChildren().clear();
        CurrDir=currFile;
        val.setText(CurrDir.toString());
        File[] fileList=currFile.listFiles();
        if(fileList!=null)
        {
            for (File nextfi : fileList)
            {
                //for table view
                data.add(new file(nextfi,nextfi.getName(), fsv.getSystemIcon(nextfi), nextfi.length(),new Date(nextfi.lastModified())));
                table.setItems(data);

                Label nexttitle = new Label(nextfi.getName());
                ImageView nextimageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(nextfi)));
                VBox nextvb=new VBox();
                nextvb.getChildren().addAll(nextimageview,nexttitle);
                tp.getChildren().addAll(nextvb);
                nextvb.setOnMouseClicked(nextevent -> {
                    if(nextevent.getClickCount()==2)
                    {
                        CurrDir=nextfi;
                        tileView(CurrDir);
                    }
                });
            }
        }
        else
        {
            System.out.println(currFile +"is an empty Directory");
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
