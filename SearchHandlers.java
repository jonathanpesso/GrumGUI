/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication2;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;
import javafx.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.security.spec.MGF1ParameterSpec.SHA256;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.crypto.SecretKey;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;


/**
 *
 * @author Yoni
 */
public class SearchHandlers {
	public static ActionListener getSearchHandler(TextField queryField, ListView<String> list, DefaultListModel<String> searchResults) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (AESCTR.secretKey == null) {
					JOptionPane.showMessageDialog(null, "Please generate or choose a key");
					return;
				}
				
				// Split query into keywords
				String[] keywords = queryField.getText().trim().toLowerCase().split("[^\\w']+");
				List<Set<String>> listSet = new ArrayList<>();
				for (String keyword : keywords) {
					if (keyword.isEmpty()) continue;
					SecretKey kE = SHA256.createIndexingKey(AESCTR.secretKey, keyword);
					String encWord = SHA256.createIndexingString(kE, keyword).replace("+", "X"); // remove + signs TEMP FIX TODO
					Set<String> inds = HttpUtil.HttpGet(encWord);
					// Decrypt all inds and add to listSet
					Set<String> decInds = new HashSet<>();
					for (String ind : inds) {
						decInds.add(AESCTR.decrypt(ind, kE));
					}
					listSet.add(decInds);
				}
				
				// Perform set intersections on results
				Set<String> results = intersect(listSet);
				
				// Add results to gui, and set selected
				searchResults.clear();
				if (results.isEmpty()) {
					searchResults.addElement("No results...");
					list.setEnabled(false);
				} else {
					for (String result : results) {
						searchResults.addElement(result);
					}
					list.setSelectedIndex(0);
					list.setEnabled(true);
				}
			}

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
		};
	}
	
	@SuppressWarnings("unchecked")
	public static MouseAdapter getListClickHandler() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JList<String> list = (JList<String>) e.getSource();
		        if (e.getClickCount() == 2) {
		        	if (list.getSelectedIndex() != -1) {
		        		downloadFromList(list);		        		
		        	}
		        }
		    }
		};
	}
	
	public static ActionListener getDownloadHandler(JList<String> list) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downloadFromList(list);
			}

                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
		};
	}
	
	private static void downloadFromList(JList<String> list) {
		if (list.getSelectedIndex() >= 0) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setApproveButtonText("Save");
	        fileChooser.setDialogTitle("Select a file...");
	        
	        // file chooser to save file
	        if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	        	//JOptionPane.showMessageDialog(null, "Downloading file: " + list.getSelectedValue() + "[" + list.getSelectedIndex() + "]");
	        	String path = fileChooser.getSelectedFile().getAbsolutePath();
				FileUtils.downloadFile(path, list.getSelectedValue(), AESCTR.secretKey);
				ClientWindow.writeLog("Downloaded to " + path);
				JOptionPane.showMessageDialog(null, "Downloaded to " + path);
	        }
		} else {
			// maybe produce an error message
			System.out.println("No file selected");
			JOptionPane.showMessageDialog(null, "No file selected");
		}
	}
	
	private static Set<String> intersect(List<Set<String>> sets) {
		if (sets.size() < 1) {
			return null;
		}
		// Sort sets by size (ascending)
		Collections.sort(sets, new Comparator<Set<String>>() {
			@Override
			public int compare(Set<String> o1, Set<String> o2) {
				return Integer.compare(o1.size(), o2.size());
			}
		});
		
		Set<String> newSet = sets.get(0);
		for (Set<String> set : sets) {
			if (newSet.size() < 1) break;
			if (set == newSet) continue;
			
			Iterator<String> it = newSet.iterator();
			while (it.hasNext()) {
				if (!set.contains(it.next())) {
					it.remove();
				}
			}
		}
		
		return newSet;
	}
}
