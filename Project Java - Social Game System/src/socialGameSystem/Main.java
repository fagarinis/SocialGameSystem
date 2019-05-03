package socialGameSystem;

import java.awt.EventQueue;
import interfacciaGraficaSGS.GraphicsSGS;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * Launch the application.
		 */
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphicsSGS window = new GraphicsSGS();
					window.frmSocialGameSystem.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		
	}

}
