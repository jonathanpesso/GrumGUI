/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ClientWindow;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * FXML Controller class
 *
 * @author Yoni
 */
public class WelcomeTabController implements Initializable {
    private TabPane tab_pane;
    private Tab uploadpanel;
    private Tab searchpanel;
    private Tab settingpanel;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

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
    
}
