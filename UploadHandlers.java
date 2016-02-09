/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientWindow;
import Utils.FileUtils;
import Utils.HttpUtil;
import Utils.StringPair;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crypto.AESCTR;
import crypto.SSE;
import javafx.scene.control.ComboBox;
/**
 *
 * @author Yoni
 */
public class UploadHandlers {
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
    
     private void notice(String note){
        Alert warn = new Alert(AlertType.INFORMATION);
        warn.setTitle("Notice!");
        warn.setHeaderText(null);
        warn.setContentText(note);
        warn.showAndWait();
     }
    @FXML //Lets User choose a file to upload if he has a key
    private void handleBrowseBtn(){
        if(AESCTR.secretKey == null) {
            notice("Please generate or choose a key!");
            return;
        }       
        
        filechooser.setTitle("Select a file to upload");
        selectedFile = filechooser.showOpenDialog(filepath.getScene().getWindow());
        
        if(selectedFile != null){
            //ClientWindow.selectedFile = file;
            //openFile(file);
            filepath.setText(selectedFile.getAbsolutePath());
            //listitems.add(filepath.getText());
            writeLog("Selected File: " + filepath.getText());
        }
    }
       // writes strings to GUI
    private void writeLog(String info){
        //uploadlist.append("info");
        listitems.add(info);
        uploadlist.setItems(listitems);
        
    }
    @FXML //lets a user upload a file that he/she chose
    private void handleUploadbtn(){
        
        if (!filepath.getText().isEmpty()){
            File fileFromType = new File(filepath.getText());
                if(fileFromType.isAbsolute() && fileFromType.exists()){
                    selectedFile = fileFromType;
                } else {
                    notice("Invalid path to file");
                    return;
                }
        } else {
            notice("Please select a file");
            return;
        }
						
        System.out.println("Encrypting file...");
        //for now uses same key to encrypt keywords
        
        String key = UUID.randomUUID().toString();
        Map<String, StringPair> map = SSE.EDBSetup(selectedFile, AESCTR.secretKey, key);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(map);
            System.out.println(json);
            System.out.println("Indexing file...");
            HttpUtil.HttpPost(json);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
            writeLog("Upload failed!");
            System.out.println("Upload failed!");
            return;
        }
        
        System.out.println("Uploading file...");
        FileUtils.uploadFile(selectedFile, key, AESCTR.secretKey);
        writeLog("Upload successful!");
        System.out.println("Upload successful!");
        notice("Upload Completed!");
    }
       
}
