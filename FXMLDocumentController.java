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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class FXMLDocumentController implements Initializable {
    
    @FXML private TabPane tab_pane;
    @FXML private Tab welcomepanel;
    @FXML private Tab uploadpanel;
    @FXML private Tab searchpanel;
    @FXML private Tab settingpanel;
    @FXML private TextField filepath;
    @FXML private Button btnbrowse;
    @FXML FileChooser filechooser = new FileChooser();
    @FXML private Desktop desktop = Desktop.getDesktop();
    
    
    
    @FXML
    private void handleChangeUpload(ActionEvent event){
        tab_pane.getSelectionModel().select(uploadpanel);
    
    
    }
    @FXML
    private void handleChangeSearch(ActionEvent event){
        tab_pane.getSelectionModel().select(searchpanel);
    
    
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
