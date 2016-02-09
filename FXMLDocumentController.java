package ClientWindow;
import Utils.StringPair;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javax.crypto.SecretKey;

 
public class FXMLDocumentController implements Initializable {
    @FXML private ListView<String> uploadlist;
    @FXML private TabPane tab_pane;
    @FXML private Tab uploadpanel;
    @FXML private TextField filepath;
    @FXML private Button btnbrowse;
    FileChooser filechooser = new FileChooser();
    Desktop desktop = Desktop.getDesktop();
    public File selectedFile;
    final ObservableList<String> listitems = FXCollections.observableArrayList("");
    
    public static LoadingCache<String, Set<StringPair>> cache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(5, TimeUnit.MINUTES).build(new QueryCacheLoader());
    
    private static List<Set<StringPair>> listSet = new ArrayList<>();
    private JList<StringPair> list;
    private DefaultListModel<StringPair> searchResults;
    private ComboBox<KeyItem> keyFile;
    private List<StringPair> myList = new ArrayList<>();
    private List<KeyItem> keylist = new ArrayList<>();
    @FXML private Button btnupload;
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
     
        File folder = new File("keys");
        folder.mkdirs();
        File[] files = folder.listFiles();
        boolean hasDefaultKey = false;
        for (File file : files) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                SecretKey kS = (SecretKey) in.readObject(); // Set secretKey
                in.close();
                keylist.add(new KeyItem(kS, file.getName()));
                ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
                keyFile.setItems(keyitemlist);
                //keyFile.addItem(new KeyItem(kS, file.getName()));
                if (file.getName().equals("defaultkey")) {
                    hasDefaultKey = true;
                }
            } catch (IOException | ClassNotFoundException ex) {
            // Not a key file, don't add to list
            }
        }
	
    	// Load default key
        if (hasDefaultKey) {
            keyFile.getSelectionModel().select(new KeyItem(null, "defaultkey"));
            //keyFile.setSelectedItem(new KeyItem(null, "defaultkey"));
            AESCTR.secretKey = keyFile.getSelectionModel().getSelectedItem().getKey();
            System.out.println("Successfully loaded key: defaultkey");
        } else {
            System.out.println("No default key found, generating new one");
            File file = new File("keys/defaultkey");
            SecretKey newKey = AESCTR.generateKey();
        // Serialize (out)
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
                out.writeObject(newKey);
                out.close();

                AESCTR.secretKey = newKey; // Set secretKey
				
                KeyItem keyItem = new KeyItem(newKey, file.getName());
                keylist.add(keyItem);
                ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
                keyFile.setItems(keyitemlist);
                keyFile.getSelectionModel().select(new KeyItem(null, "defaultkey"));
                //keyFile.add(keyItem);
                //keyFile.setSelectedItem(keyItem);
            } catch (IOException ex2) {
                System.out.println("Failed to generate a default key");
                ex2.printStackTrace();
            }
        }
    
    }   
 }