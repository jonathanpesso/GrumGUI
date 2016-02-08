package ClientWindow;

import Utils.FileUtils;
import Utils.HttpUtil;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crypto.AESCTR;
import crypto.SSE;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import javafx.scene.control.ComboBox;
import javax.crypto.SecretKey;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class FXMLDocumentController implements Initializable {
    
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
    //not used for now
    private void openFile(File file) {
        try{
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
            FXMLDocumentController.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
        
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
    
    @FXML //lets a user search keywords in uploaded files
    private void handleSearchbtn(){
        if (AESCTR.secretKey == null) {
            notice("Please generate or choose a key");
            return;
        }
	// Split query into keywords
	String[] keywords = search_input.getText().trim().toLowerCase().split("[^\\w']+");
        System.out.println("keywords seperated");
	listSet.clear();
        for (String keyword : keywords) {
            if (keyword.isEmpty()) continue;
            try {
                listSet.add(cache.get(keyword));
            } catch (ExecutionException ex) {
            // Some error? Do nothing for now
            ex.printStackTrace();
            }
        }
        System.out.println("matchhandler called");
        Set<StringPair> results = intersect(listSet, keywords.length);
        System.out.println("populate Results called");
        populateResults(results);
        
        
    }
    @FXML // Add results to gui, and set selected
          // searchResults.clear();
    private void populateResults(Set<StringPair> results) {
        search_result.getItems().clear();
        System.out.println("list cleared");
        if (results.isEmpty()) {
            myList.add(new StringPair("", "No results..."));
            System.out.println("no results");
        } else {
            for (StringPair result : results) {
                myList.add(result);
            }
        ObservableList<StringPair> myObs = FXCollections.observableList(myList);
        search_result.setItems(myObs);
        }
    }
    
    @FXML //tells download button to use downloadfromlist()
    private void handleDownloadbtn(){
        System.out.println("downloadfromlist called");
        downloadfromlist();
        
    }
    
    @FXML //
    private void downloadfromlist(){
        //sets a filechooser so user can select where to download selected file
        if (search_result.getSelectionModel().getSelectedIndex() >= 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a file...");
           
            // file chooser to save file
            selectedFile = filechooser.showSaveDialog(filepath.getScene().getWindow());
            System.out.println("file chosen");
            String path = selectedFile.getAbsolutePath();
            System.out.println("downloading..");
            FileUtils.downloadFile(path, search_result.getSelectionModel().getSelectedItem().getFileId(), AESCTR.secretKey);
            System.out.println("file downloaded");
            notice("Downloaded to " + path);
        } else {
        // produce an error message
            System.out.println("No file selected");
            notice("No File Selected");
        }
        
    }
    // writes strings to GUI
    private void writeLog(String info){
        //uploadlist.append("info");
        listitems.add(info);
        uploadlist.setItems(listitems);
        
        
    }
    private void notice(String note){
        Alert warn = new Alert(AlertType.INFORMATION);
        warn.setTitle("Notice!");
        warn.setHeaderText(null);
        warn.setContentText(note);
        warn.showAndWait();
               
    }
    
    private static Set<StringPair> intersect(List<Set<StringPair>> sets, int min) {
        if (sets.size() < 1) {
            return Collections.emptySet();
        } else if(sets.size() <= min) {
            return intersect(sets);
        }
        // Adds each result to multiset and counts
        Multiset<StringPair> bag = HashMultiset.create();
        for (Set<StringPair> set : sets) {
            for (StringPair str : set) {
                bag.add(str);
            }
        }
        // Only keep results with a count greater than min
        Set<StringPair> newSet = new HashSet<>();
        for (Entry<StringPair> e : bag.entrySet()) {
            if (e.getCount() >= min) {
                newSet.add(e.getElement());
            }
        }
        return newSet;
    }
    
    private static Set<StringPair> intersect(List<Set<StringPair>> sets) {
        if (sets.size() < 1) {
            return Collections.emptySet();
        }
        // Sort sets by size (ascending)
        Collections.sort(sets, new Comparator<Set<StringPair>>() {
            @Override
            public int compare(Set<StringPair> o1, Set<StringPair> o2) {
                return Integer.compare(o1.size(), o2.size());
            }
        });
		
        Set<StringPair> newSet = new HashSet<>(sets.get(0));
        for (Set<StringPair> set : sets) {
            if (newSet.size() < 1) break;
            if (set == newSet) continue;
			
            Iterator<StringPair> it = newSet.iterator();
            while (it.hasNext()) {
                if (!set.contains(it.next())) {
                    it.remove();
                }
            }
        }
		
        return newSet;
    }
    
    

/*@FXML

	private void selectKeyHandler() {
		
			public void actionPerformed(ActionEvent e) {
				Object o = e.getSource();
				if (!(o instanceof JComboBox)) {
					throw new IllegalStateException("selectKeyHandler attached to invalid control");
				}
				
				JComboBox<KeyItem> box = (JComboBox<KeyItem>) o;
				AESCTR.secretKey = box.getItemAt(box.getSelectedIndex()).getKey();
				ClientWindow.writeLog("Successfully loaded key: " + box.getSelectedItem());
			}
}*/
	
	@FXML
	public void handleRemoveKey(JComboBox<KeyItem> keyFile) {

				Object[] options = { "OK", "CANCEL" };
				if (JOptionPane.showOptionDialog(null, "Are you sure you want to delete the key: " + keyFile.getSelectedItem() + 
						"?\nThis CANNOT be undone.", "WARNING: Delete Key?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, 
						null, options, options[1]) == JOptionPane.OK_OPTION) {
					String keyName = keyFile.getSelectedItem().toString();
					File file = new File("keys/" + keyName);
					if (file.delete()) {
						writeLog("Successfully deleted key: " + keyName);
						keyFile.removeItemAt(keyFile.getSelectedIndex());
					} else {
						writeLog("Unable to delete file");
					}
				}
			}
		
	
	@FXML
	public void handleNewKey(JComboBox<KeyItem> keyFile) {
		
				String keyName = JOptionPane.showInputDialog("Please enter a name for your key");
				if (keyName != null && !keyName.isEmpty()) {
			        SecretKey newKey = AESCTR.generateKey();
			        
			        // Serialize (out)
			        try {
			        	File file = new File("keys/" + keyName);
						ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
						out.writeObject(newKey);
						out.close();
						
						// Set key in AES
						AESCTR.secretKey = newKey;
						
						KeyItem keyItem = new KeyItem(newKey, file.getName());
						keyFile.addItem(keyItem);
						keyFile.setSelectedItem(keyItem);
						writeLog("Loaded new key: " + file.getName());
			        } catch (IOException ex) {
						ex.printStackTrace();
					
				}
			}
		};

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
    
    }    }