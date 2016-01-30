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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

/**
 *
 * @author jonathan
 */
public class FXMLDocumentController implements Initializable {
    
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
    public File file;
    final ObservableList<String> listitems = FXCollections.observableArrayList("");
    
    @FXML
    private void handleChangeUpload(){
        tab_pane.getSelectionModel().select(uploadpanel);    
    }
    
    @FXML
    private void handleChangeSearch(){
        tab_pane.getSelectionModel().select(searchpanel); 
    }
    @FXML
    private void handleBrowseBtn(){
        /*if(AESCTR.secretKey = null) {
            notice("Please generate or choose a key!");
            return;
        }*/       
        
        filechooser.setTitle("Select a file to upload");
        file = filechooser.showOpenDialog(filepath.getScene().getWindow());
        
        if(file != null){
            //ClientWindow.selectedFile = file;
            //openFile(file);
            filepath.setText(file.getAbsolutePath());
            //listitems.add(filepath.getText());
            writeLog("Selected File: " + filepath.getText());
        }
    }
    
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
    @FXML
    private void handleUploadbtn(){
        if (filepath.getText() != ""){
            File fileFromType = new File(filepath.getText());
                if(fileFromType.isAbsolute() && fileFromType.exists()){
                    //ClientWindow.selectedFile = fileFromType;
                } else {
                    notice("Invalid path to file");
                    return;
                }
        } else {
            notice("Please select a file");
            return;
        }
						
        writeLog("Encrypting file...");
        //for now uses same key to encrypt keywords
        //String key = UUID.randomUUID().toString();
        //Map<String, StringPair> map = SSE.EDBSetup(ClientWindow.selectedFile, AESCTR.secretKey, key);
        //ObjectMapper mapper = new ObjectMapper();
        /*try {
            String json = mapper.writeValueAsString(map);
            System.out.println(json);
            writeLog("Indexing file...");
            HttpUtil.HttpPost(json);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
            writeLog("Upload failed!");
            return;
        }*/
        writeLog("Uploading file...");
        //FileUtils.uploadFile(ClientWindow.selectedFile, key, AESCTR.secretKey);
        //ClientWindow.writeLog("Upload successful!");
        writeLog("Upload Successful!");
			
        
        
        
    }
    
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
               
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
