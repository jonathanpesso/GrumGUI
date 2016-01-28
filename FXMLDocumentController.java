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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button change_to_upload;
    private Button change_to_search;
    private TextField filepath;
    private ListView filelist;
    private Button btnbrowse;
    private Button btnupload;
    private Button btnsearch;
    private Button btndownload;
    private final FileChooser filechooser = new FileChooser();
    private final Desktop desktop = Desktop.getDesktop();
    
    
    
    @FXML
    private void handleChangeUpload(ActionEvent event){
    
    
    
}
    @FXML
    private void handleChangeSearch(ActionEvent event){
    
    
    
}
    @FXML
    private void handleUploadButton(ActionEvent event){
    
    
    
}
       @FXML
    private void handleSearchButton(ActionEvent event){
    
    
    
}
       @FXML
    private void handleDownloadButton(ActionEvent event){
    
    
    
}
    
    @FXML
    private void handleBrowseBtn(ActionEvent event){
        File file = filechooser.showOpenDialog(filepath.getScene().getWindow());
        if(file != null)
            openFile(file);
        
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
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
