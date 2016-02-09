/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientWindow;
import Utils.FileUtils;
import Utils.StringPair;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import crypto.AESCTR;
import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javax.swing.DefaultListModel;
import javax.swing.JList;
/**
 *
 * @author Yoni
 */
public class SearchHandlers {
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
        for (Multiset.Entry<StringPair> e : bag.entrySet()) {
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

}
