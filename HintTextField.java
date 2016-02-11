//package javafxapplication2;
//
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.scene.control.TextField;
//
////import javax.swing.JTextField;
//
//class HintTextField extends TextField implements FocusListener {
//	private static final long serialVersionUID = 1L;
//	private final String hint;
//	private boolean showingHint;
//
//	public HintTextField(final String hint) {
//		super(hint);
//		this.hint = hint;
//		this.showingHint = true;
//		//super.addFocusListener(this);
//                super.focusedProperty().addListener(new ChangeListener<Boolean>() {
//
//            @Override
//            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//                if (t1) {
//                    System.out.println("Focus In");
//                    focusGained();
//                } else {
//                    System.out.println("Focus Out");
//                    focusLost();    
//                }
//
//            }
//        });
//        }
//
//	@Override
//	public void focusGained() {
//		if (this.getText().isEmpty()) {
//			super.setText("");
//			showingHint = false;
//		}
//	}
//
//	@Override
//	public void focusLost() {
//		if (this.getText().isEmpty()) {
//			super.setText(hint);
//			showingHint = true;
//		}
//	}
//
//	@Override
//	private final String getText() {
//		return showingHint ? "" : super.getText();
//	}
//	
//	public void setShowingHint(boolean flag) {
//		showingHint = flag;
//	}
//}
