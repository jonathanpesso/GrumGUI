/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication2;


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
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.crypto.SecretKey;

/**
 *
 * @author jonathan
 */
public class FXMLDocumentController implements Initializable {
    
    //@FXML private ScrollBar matchSlider;
    @FXML private ProgressBar upprogress;
    @FXML private ProgressBar downprogress;
    @FXML private CheckBox stemmer;
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
    //final List<StringPair> searchlist = FXCollections.observableArrayList<>("fileId", "filename");
    public static LoadingCache<String, Set<StringPair>> cache = CacheBuilder.newBuilder().maximumSize(100)
			.expireAfterAccess(5, TimeUnit.MINUTES).build(new QueryCacheLoader());
    private static List<Set<StringPair>> listSet = new ArrayList<>();
    //private JList<StringPair> list;
    //private DefaultListModel<StringPair> searchResults;
    @FXML private ComboBox<KeyItem> keyFile;
    private List<StringPair> myList = new ArrayList<>();
    private List<KeyItem> keylist = new ArrayList<>();
    public ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
    //public Set<String> stemWords = new HashSet<>();
    private List<File> filelist;
    private static Set<String> lastUpload = new HashSet<>();
    private File[] selectedFiles;
    /*
    @SuppressWarnings("unchecked")
    public MouseAdapter getListClickHandler() {
        return new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JList<StringPair> list = (JList<StringPair>) e.getSource();
                if (e.getClickCount() == 2) {
                    if (search_result.getSelectionModel().getSelectedIndex() != -1) {
                        downloadfromlist();		        		
                    }
                }
            }
        };
    }
    */    
    
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ///////////// WELCOME PANEL HANDLERS ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    @FXML
    private void handleChangeUpload(){
        tab_pane.getSelectionModel().select(uploadpanel);    
    }
    
    @FXML
    private void handleChangeSearch(){
        tab_pane.getSelectionModel().select(searchpanel); 
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////// UPLOAD PANEL HANDLERS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    
    @FXML
    private void handleBrowseBtn(){
        if(AESCTR.secretKey == null) {
            notice("Please generate or choose a key!");
            return;
        }       
        
        filechooser.setTitle("Select the files to upload");
        filelist = filechooser.showOpenMultipleDialog(filepath.getScene().getWindow());
        //selectedFile = 
        StringBuilder sb = new StringBuilder(1024);
        if(filelist != null){
            for (File selected_file : filelist){
                sb.append(selected_file.getAbsolutePath() + " , ");
                writeLog("selected File: " + selected_file.getName());    
                }
            filepath.setText(sb.toString());
            }
            //ClientWindow.selectedFile = file;
            //openFile(file);
            //filepath.setText(selectedFile.getAbsolutePath());
            //listitems.add(filepath.getText());
            //writeLog("Selected File: " + filepath.getText());
        
    }
    
    
    @FXML
    private void handleUploadbtn(){
        AESCTR.secretKey = keyFile.getSelectionModel().getSelectedItem().getKey();
        
        if (filelist != null){
            for(File selected_file : filelist){
                //String filepath = selected_file.getAbsolutePath();
                if(lastUpload.contains(filepath)){
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Are you sure?");
                    alert.setHeaderText(null);
                    alert.setContentText("You just uploaded " + filepath + ", are you sure you want to upload it again?");
                    ButtonType yesbtn = new ButtonType("Yes");
                    ButtonType nobtn = new ButtonType("No");
                    alert.getButtonTypes().setAll(yesbtn, nobtn);
                    Optional<ButtonType> result = alert.showAndWait();
                    if(result.get() == yesbtn)
                        ;
                    if(result.get() == nobtn)
                        return;    
                }
            }
            int y = 0;
            selectedFiles = new File[filelist.size()];
            for(File fil : filelist) {
                if(fil.isAbsolute() && fil.exists()){
                    selectedFiles[y] = fil;
                    y++;
                }
                else{
                    notice("Invalid path to file");
                    return;
                }
            }
        } else {
            notice("Please select a file");
            return;
        }					
        System.out.println("Encrypting file...");
        //for now uses same key to encrypt keywords
        Task<Boolean> task = new Task<Boolean>() {
            @Override protected Boolean call() throws Exception {
                String key = UUID.randomUUID().toString();
                updateProgress(5,100);
                Map<String, ArrayList<StringPair>> map = SSE.EDBSetup(selectedFiles, AESCTR.secretKey, key, stemmer.isSelected());
                updateProgress(20, 100);
                ObjectMapper mapper = new ObjectMapper();
                try{
                    String json = mapper.writeValueAsString(map);
                    updateProgress(50, 100);
                    HttpUtil.HttpPost(json);
                    updateProgress(60, 100);                    
                } catch (JsonProcessingException e1){
                    e1.printStackTrace();
                    return false;
                }
                for (File fi : selectedFiles) {
                    System.out.println("Uploading" + fi.getAbsolutePath());
                    FileUtils.uploadFile(fi, key, AESCTR.secretKey);
                }

                updateProgress(100, 100);
                return true;
            }
            
            
        };
        
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
           @Override
           public void handle(WorkerStateEvent event){
               System.out.println("Upload Succeeded");
               notice("Upload Complete!");
               lastUpload.clear();
               for(File f : selectedFiles){
                   lastUpload.add(f.getAbsolutePath());
               }
           }
        });
        
        task.setOnFailed(new EventHandler<WorkerStateEvent>(){
           @Override
           public void handle(WorkerStateEvent event){
               System.out.println("Upload Failed");
               throw new UnsupportedOperationException("Failed.");
           }           
        });
        
        task.setOnCancelled(new EventHandler<WorkerStateEvent>(){
           @Override
           public void handle(WorkerStateEvent event) {
               System.out.println("Upload Cancelled!");
               throw new CancellationException("Cancelled.");
           }
        });
        
        upprogress.progressProperty().bind(task.progressProperty());
        //new Thread(task).start();
        Thread up = new Thread(task);
        up.setDaemon(true);
        up.start();
//        String key = UUID.randomUUID().toString();
//        Map<String, StringPair> map = SSE.EDBSetup(selectedFile, AESCTR.secretKey, key, stemmer.isSelected());
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            String json = mapper.writeValueAsString(map);
//            System.out.println(json);
//            System.out.println("Indexing file...");
//            HttpUtil.HttpPost(json);
//        } catch (JsonProcessingException e1) {
//            e1.printStackTrace();
//            writeLog("Upload failed!");
//            System.out.println("Upload failed!");
//            return;
//        }
        
        //.out.println("Uploading file...");
        //FileUtils.uploadFile(selectedFile, key, AESCTR.secretKey);

    }
    
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ////////////////////// SEARCH PANEL HANDLERS ////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    
    @FXML
    public void enterbtn(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER)
            handleSearchbtn();
        
    }
    
    @FXML
    private void handleSearchbtn(){
        AESCTR.secretKey = keyFile.getSelectionModel().getSelectedItem().getKey();
        //search_result.getItems().clear();
        if (AESCTR.secretKey == null) {
            notice("Please generate or choose a key");
            return;
        }
       
        Task<Set<String>> task = new Task<Set<String>>() {
          @Override
          protected Set<String> call() throws Exception {
              String[] keywords = search_input.getText().trim().toLowerCase().split("[^\\w']+");
              Set<String> stemWords = new HashSet<>();
              for(String word : keywords) {
                  if(Stopper.isStop(word)) continue;
                  if(stemmer.isSelected()) {
                      stemWords.add(Stemmer.getStem(word));
                  } else {
                      stemWords.add(word);
                  }
                  
              }
          System.out.println("Searching: " + stemWords);
          listSet.clear();
          for(String keyword : stemWords) {
              if(keyword.isEmpty()) continue;
              try {
                  listSet.add(cache.get(keyword));
              } catch (ExecutionException ex) {
                  ex.printStackTrace();
              }
              
          }
          
          return stemWords;
          }
            
        };
        
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
            @Override
            public void handle(WorkerStateEvent event) {
                System.out.println("calling intersect");
                Set<StringPair> results = intersect(listSet, listSet.size());
                System.out.println("populate results called");
                populateResults(results);
                

        
            }
        
        });
        
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
           @Override
           public void handle(WorkerStateEvent event) {
               System.out.println("query seperation failed");
               throw new UnsupportedOperationException("Failed.");
           }
        });
        
        task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
           @Override
           public void handle(WorkerStateEvent event) {
               System.out.println("operation cancelled");
               throw new CancellationException("Cancelled.");
           }
        });

        Thread search = new Thread(task);
        search.setDaemon(true);
        search.start();
	// Split query into keywords
//	String[] keywords = search_input.getText().trim().toLowerCase().split("[^\\w']+");
//        System.out.println("keywords seperated");
//	listSet.clear();
//        for (String keyword : keywords) {
//            if (keyword.isEmpty()) continue;
//            try {
//                listSet.add(cache.get(keyword));
//            } catch (ExecutionException ex) {
//            // Some error? Do nothing for now
//            ex.printStackTrace();
//            }
//        }
//        //matchSlider.setMax(keywords.length);
//        //matchSlider.setMin(1);
//        //matchSlider.setValue(keywords.length);
//        System.out.println("matchhandler called");
//        Set<StringPair> results = intersect(listSet, keywords.length);
//        System.out.println("populate Results called");
//        //getMatchHandler(list, searchResults);
//        populateResults(results);
        
        
    }
           
        
    @FXML
    private void populateResults(Set<StringPair> results) {
        // Add results to gui, and set selected
        //searchResults.clear();
        search_result.getItems().clear();
        myList.clear();
        System.out.println("cleared search list");
        if (results.isEmpty()) {
            myList.add(new StringPair("", "No results..."));
            //searchResults.addElement(new StringPair("", "No results..."));
            //list.setEnabled(false);
            
            ObservableList<StringPair> myObs = FXCollections.observableList(myList);
            search_result.setItems(myObs);
            System.out.println("no results");
        } else {
            for (StringPair result : results) {
                myList.add(result);
                //searchResults.addElement(result);
            }
            ObservableList<StringPair> myObs = FXCollections.observableList(myList);
            search_result.setItems(myObs);
        //list.setSelectedIndex(0);
        //list.setEnabled(true);
        }
    }
    
    @FXML
    private void handleDownloadbtn(){
        System.out.println("downloadfromlist called");
        downloadfromlist();
        
    }
    
    @FXML
    private void downloadfromlist(){
        if (search_result.getSelectionModel().getSelectedIndex() >= 0) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a file...");
            
            //fileChooser.setButtonText("Save");
            
            // file chooser to save file
            selectedFile = filechooser.showSaveDialog(filepath.getScene().getWindow());
            System.out.println("file chosen");
            //if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                //JOptionPane.showMessageDialog(null, "Downloading file: " + list.getSelectedValue() + "[" + list.getSelectedIndex() + "]");
            String path = selectedFile.getAbsolutePath();
            System.out.println("downloading..");
            Task<Boolean> download = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        FileUtils.downloadFile(path, search_result.getSelectionModel().getSelectedItem().getFileId(), AESCTR.secretKey);
                        updateProgress(100, 100);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                    return true;
                }
                
            };
            
            download.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("Download Complete!");
                    notice("File Downloaded to: " + path);
                } 
            });
            
            download.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("Download failed!");
                    throw new UnsupportedOperationException("Failed.");
                }
            });
            
            download.setOnCancelled(new EventHandler<WorkerStateEvent>() {
               @Override
               public void handle(WorkerStateEvent event) {
                   System.out.println("Download cancelled!");
                   throw new CancellationException("Cancelled.");
               }
            });
//            FileUtils.downloadFile(path, search_result.getSelectionModel().getSelectedItem().getFileId(), AESCTR.secretKey);
//            System.out.println("file downloaded");
//                //ClientWindow.writeLog("Downloaded to " + path);
//            notice("Downloaded to " + path);
        //}
            downprogress.progressProperty().bind(download.progressProperty());
            //new Thread(task).start();
            Thread down = new Thread(download);
            down.setDaemon(true);
            down.start();
        } else {
        // maybe produce an error message
            System.out.println("No file selected");
            notice("No File Selected");
        }
        
    }
    
    private static Set<StringPair> intersect(List<Set<StringPair>> sets, int min) {
        if (sets.size() < 1) {
            System.out.println("EMPTY SET");
            return Collections.emptySet();
        } else if(sets.size() <= min) {
            System.out.println("CALLING OTHER INTERSECT");
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
            System.out.println("EMPTY SET");
            return Collections.emptySet();
        }
        System.out.println("NOT AN EMPTY SET");
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
    
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// SETTINGS PANEL HANDLERS //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    @FXML
    public void keychanged(){
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Are you sure?");
        confirm.setHeaderText(null);
        confirm.setContentText("Confirm you are changing keys");
        Optional<ButtonType> response = confirm.showAndWait();
        if(response.get() == ButtonType.OK){
            AESCTR.secretKey = keyFile.getSelectionModel().getSelectedItem().getKey();
            cache.invalidateAll();
        }
        
        
    }
    
    
    @FXML
    public void handleRemoveKey(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("WARNING: Delete Key?");
        alert.setHeaderText("Are you sure you want to delete the key:" + keyFile.getSelectionModel().getSelectedItem());
        alert.setContentText("This CANNOT be undone. Are you sure?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK){
            String keyName = keyFile.getSelectionModel().getSelectedItem().toString();
            File file = new File("keys/" + keyName);
            if(file.delete()){
                System.out.println("Successfully deleted key:" + keyName);
                keylist.remove(keyFile.getSelectionModel().getSelectedIndex());
                ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
                keyFile.setItems(keyitemlist);
                keyFile.getSelectionModel().select(new KeyItem(null, "defaultkey"));
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
    
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    ////////////// MISC METHODS ///////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////
    
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
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        stemmer.setSelected(true);
        stemmer.setIndeterminate(false);
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
                //ObservableList<KeyItem> keyitemlist = FXCollections.observableList(keylist);
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
                keyitemlist = FXCollections.observableList(keylist);
                keyFile.setItems(keyitemlist);
                keyFile.getSelectionModel().select(new KeyItem(null, "defaultkey"));
                //keyFile.add(keyItem);
                //keyFile.setSelectedItem(keyItem);
            } catch (IOException ex2) {
                System.out.println("Failed to generate a default key");
                ex2.printStackTrace();
            }
        }
    
        search_result.setOnMouseClicked(new EventHandler<MouseEvent>(){
            
            @Override
            public void handle(MouseEvent click){
                if(click.getClickCount() > 1 && search_result.getSelectionModel().getSelectedItem() != null)
                    downloadfromlist();
                
            }
            
        });
        
    }    
    
}
