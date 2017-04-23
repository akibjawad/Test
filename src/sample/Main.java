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

import static javafx.geometry.Pos.*;

public class Main extends Application {

    private final Node rootIcon= new ImageView(new Image(getClass().getResourceAsStream("This_PC.png")));
    private File[] paths;
    private File[] files;
    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private TableView<file> table=new TableView<>();
    public ObservableList<file> data= FXCollections.observableArrayList();
    private File CurrDir=fsv.getParentDirectory(File.listRoots()[0]);
    public TilePane tp = new TilePane();


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("File Explorer");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.autosize();
        //hbox.setPadding(new Insets(10,0,0,10));

        Label CD=new Label("Current Directory:");
        Text val=new Text();
        val.setText((fsv.getParentDirectory(File.listRoots()[0])).toString());
        System.out.println(fsv.getParentDirectory(File.listRoots()[0]));
        table.setEditable(true);
        HBox hb2=new HBox();
        Button viewChoose=new Button("Tile View");
        viewChoose.setAlignment(TOP_RIGHT);
        viewChoose.setId("tv");
        hb2.getChildren().addAll(CD,val,viewChoose);
        GridPane gp=new GridPane();
        gp.getChildren().add(table);


        tp.setPrefTileHeight(50);
        tp.setPrefTileWidth(50);

        VBox vBox=new VBox();
        vBox.getChildren().addAll(hb2,gp);



        TableColumn<file,File> fc =new TableColumn<>("file");
        fc.setMaxWidth(5);
        fc.setCellValueFactory(new PropertyValueFactory<file,File>("f"));

        TableColumn<file,String> FileName =new TableColumn<>("FileName");
        FileName.setMinWidth(200);
        FileName.setCellValueFactory(new PropertyValueFactory<file,String>("filename"));

        TableColumn<file,Icon> Icon = new TableColumn<>("Icon");
        Icon.setMinWidth(200);
        Icon.setCellValueFactory(new PropertyValueFactory<file,Icon>("icon"));
        /*
        TableColumn<file,String> Icon =new TableColumn<>("Icon");
        Icon.setMinWidth(200);
        Icon.setCellValueFactory(new PropertyValueFactory<file,String>("icon"));
        */
        TableColumn<file,Long> Size = new TableColumn<>("Size");
        Size.setMinWidth(200);
        Size.setCellValueFactory(new PropertyValueFactory<file,Long>("size"));

        TableColumn<file,Long> ModifyDate = new TableColumn<file,Long>("Modyfy Date");
        ModifyDate.setMinWidth(200);
        ModifyDate.setCellValueFactory(new PropertyValueFactory<file,Long>("dateModified"));

        table.getColumns().addAll(FileName,Icon,Size,ModifyDate);


        //hbox.getChildren().add(table);

        paths=File.listRoots();
        TreeItem<File> rootItem= new TreeItem<File>(fsv.getParentDirectory(paths[0]),rootIcon);
        rootItem.setExpanded(true);
        for(File path:paths)
        {
            TreeItem<File> temp = new TreeItem<File>(path);
            rootItem.getChildren().add(temp);
            FileNode(path,temp);
        }
        TreeView<File> tree = new TreeView<File>(rootItem);
        tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<File>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<File>> observable, TreeItem<File> oldValue, TreeItem<File> newValue) {
                data.clear();
                tp.getChildren().clear();
                //vBox.getChildren().removeAll(tp);
                //vBox.getChildren().removeAll(gp);
                TreeItem<File> selectedItem =(TreeItem<File>) newValue;
                System.out.println("Selecetd Item: "+selectedItem.getValue());
                File file=selectedItem.getValue();
                CurrDir=file;
                val.setText(CurrDir.toString());
                files=file.listFiles();
                if(files!=null)
                {
                    for (File f : files)
                    {
                        //for list view
                        data.add(new file(f, f.getName(), fsv.getSystemIcon(f), f.length(), f.lastModified()));
                        table.setItems(data);

                        //for tile view
                        Label title = new Label(f.getName());
                        ImageView imageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(f)));
                        VBox vb=new VBox();
                        vb.getChildren().addAll(imageview,title);
                        tp.getChildren().addAll(vb);
                    }

                }
                else
                {
                    System.out.println(fsv.getParentDirectory(file).toString()+"is empty diectory");
                }
            }
        });
        /*
        //tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.setCellFactory( tr ->{
            TreeCell<File> cell = new TreeCell<File>()
            {
                public void  updateItem(File item,boolean empty)
                {
                    super.updateItem(item,empty);
                    if(!empty)
                        setItem(item);
                    else
                        setItem(null);
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty())
                {
                    TreeItem<File> item =cell.getTreeItem();
                    System.out.println("fuck");
                    //File file=item.getValue();
                    //File[] files=file.listFiles();
                    //for(File f:files)
                    //{
                    //    FileName.setText(f.getName());
                    //    //Icon.setGraphic();
                    //    //Size.setText(f.length()));
                    //}
                }
            });
           return cell;
        });
        */
        //Group root=new Group();
        //Text baal=new Text("FUCKING BAAL");
        table.setRowFactory( tv -> {
            TableRow<file> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    file f = row.getItem();
                    CurrDir = f.f;
                    System.out.println("Selected file "+f.getFilename());
                    val.setText(f.f.toString());
                    data.clear();
                    files=f.f.listFiles();
                    if(files!=null)
                    {
                        for (File temp : files) {
                            data.add(new file(temp,temp.getName(), fsv.getSystemIcon(temp), temp.length(), temp.lastModified()));
                            table.setItems(data);
                            //tile view
                            Label title = new Label(temp.getName());
                            ImageView imageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(temp)));
                            //TilePane.setAlignment(title, BOTTOM_RIGHT);
                            VBox vb=new VBox();
                            vb.getChildren().addAll(imageview,title);
                            tp.getChildren().addAll(vb);
                        }
                    }
                    else
                    {
                        System.out.println(f.f.getName()+" is empty diectory");
                    }
                }
            });
            return row ;
        });


        File fp =fsv.getParentDirectory(File.listRoots()[0]);
        tileView(fp);
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
        /*
        tp.setOnMouseClicked((MouseEvent event)->{
            java.lang.Object ob = event.getSource();
            if(ob instanceof VBox)
            {
                VBox vTemp =(VBox)ob;
                Label temp= (Label) vTemp.getChildren().get(1);
                CurrDir=new File(CurrDir+"\\"+temp.getText());
                CD.setText(CurrDir.toString());
                files=CurrDir.listFiles();
                if(files!=null)
                {
                    for (File fi : files) {
                        Label title = new Label(fi.getName());
                        ImageView imageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(fi)));
                        //TilePane.setAlignment(title, BOTTOM_RIGHT);
                        VBox vb=new VBox();
                        vb.getChildren().addAll(imageview,title);
                        tp.getChildren().addAll(vb);
                    }
                }
                else
                    System.out.println(CurrDir.getName()+" is empty Directory");

            }
        });
        */


        viewChoose.setOnMouseClicked((MouseEvent event) ->{
            if(viewChoose.getId()=="tv")
            {
                viewChoose.setText("Details");
                viewChoose.setId("dt");
                //gp.getChildren().removeAll(table);
                vBox.getChildren().removeAll(gp);
                vBox.getChildren().add(tp);
            }
            else
            {
                vBox.getChildren().removeAll(tp);
                vBox.getChildren().add(gp);
                //gp.getChildren().add(table);
                viewChoose.setId("tv");
                viewChoose.setText("Tile View");
            }
        });

        hbox.getChildren().add(tree);
        hbox.getChildren().add(vBox);
        hbox.autosize();
        //root.getChildren().add(table);
        primaryStage.setScene(new Scene(hbox, 700, 400));
        primaryStage.show();
    }
    public void FileNode(File path,TreeItem<File> node)
    {
        if(!(path.isDirectory()))
        {
            TreeItem<File> temp = new TreeItem<File>(path);
            node.getChildren().add(temp);
            System.out.println("fuck new file");
            return;
        }
        else
        {
            File[] list=path.listFiles();
            if(list!=null)
            {

                for (File f : list)
                {
                        TreeItem<File> temp = new TreeItem<File>(f);
                        node.getChildren().add(temp);
                        FileNode(f, temp);
                }
                System.out.println("fuck new directory");
            }
            else
            {
                System.out.println("fuck new empty directory");
                TreeItem<File> temp = new TreeItem<File>(path);
                node.getChildren().add(temp);
            }
        }
    }
    public static Image jswingIconToImage(javax.swing.Icon jswingIcon) {
        BufferedImage bufferedImage = new BufferedImage(jswingIcon.getIconWidth(), jswingIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        jswingIcon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
    public void tileView(File currFile)
    {
        tp.getChildren().clear();
        File[] fileList=currFile.listFiles();
        if(fileList!=null)
        {
            for (File nextfi : fileList) {
                Label nexttitle = new Label(nextfi.getName());
                ImageView nextimageview = new ImageView(jswingIconToImage(fsv.getSystemIcon(nextfi)));
                //TilePane.setAlignment(title, BOTTOM_RIGHT);
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
