/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientWindow;

import Utils.StringPair;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import crypto.AESCTR;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javax.crypto.SecretKey;

public class SettingsHandlers {
    
    @FXML private ListView<StringPair> search_result;
    @FXML private TextField search_input;
    @FXML private ListView<String> uploadlist;
    @FXML private TabPane tab_pane;
    @FXML private Tab welcomepanel;
    @FXML private Tab uploadpanel;
    @FXML private Tab searchpanel;
    @FXML private Tab settingpanel;
    @FXML private TextField filepath;
    @FXML private Button btnbrowse;
    @FXML FileChooser filechooser = new FileChooser();
    @FXML Desktop desktop = Desktop.getDesktop();
    public File selectedFile;
    final ObservableList<String> listitems = FXCollections.observableArrayList("");
    
    public static LoadingCache<String, Set<StringPair>> cache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(5, TimeUnit.MINUTES).build(new QueryCacheLoader());
    
    private static List<Set<StringPair>> listSet = new ArrayList<>();
    private JList<StringPair> list;
    private DefaultListModel<StringPair> searchResults;
    @FXML private ComboBox<KeyItem> keyFile;
    private List<StringPair> myList = new ArrayList<>();
    private List<KeyItem> keylist = new ArrayList<>();

@FXML //Redirects to Upload Tab
    private void handleChangeUpload(){
        tab_pane.getSelectionModel().select(uploadpanel);    
    }
    @FXML //Redirects to Settings Tab
    private void handleChangeSettings(){
        tab_pane.getSelectionModel().select(settingpanel);    
    }
    @FXML //Redirects to Search Tab
    private void handleChangeSearch(){
        tab_pane.getSelectionModel().select(searchpanel); 
    }    
@FXML
    public void handleRemoveKey() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Are you sure you want to delete?");
    alert.setHeaderText("Delete File: " + keyFile.getSelectionModel()  + "?\n THIS CANNOT BE UNDONE!");
    alert.setContentText("Choose your option.");

    Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            String keyName = keyFile.getSelectionModel().getSelectedItem().toString();
            File file = new File("keys/" + keyName);
            if(file.delete()){
                System.out.println("Successfully deleted key:" + keyName);
                keyFile.getSelectionModel().clearSelection(keyFile.getSelectionModel().getSelectedIndex());
            } else{
                System.out.println("Unable to delete file");
            }
        } 
    }

 @FXML
    public void handleNewKey(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Key");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter a name for your key");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            SecretKey newKey = AESCTR.generateKey();
            try{
                File file = new File("keys/" + result.get());
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
                out.writeObject(newKey);
                out.close();

                AESCTR.secretKey = newKey; // Set secretKey
				
                KeyItem keyItem = new KeyItem(newKey, result.get());
                keylist.add(keyItem);
                ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
                keyFile.setItems(keyitemlist);
                keyFile.getSelectionModel().select(new KeyItem(null, result.get()));
                //keyFile.add(keyItem);
                //keyFile.setSelectedItem(keyItem);
            } catch (IOException ex2) {
                System.out.println("Failed to generate a new key");
                ex2.printStackTrace();
                
            }
            
        }
        
    }
}
