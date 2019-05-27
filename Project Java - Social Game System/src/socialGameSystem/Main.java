package socialGameSystem;
import java.awt.EventQueue;
import interfacciaGraficaSGS.GraphicsSGS;

/**
 * Il programma Social Game System implementa una scacchiera relazionale in cui diversi tipi di giocatori
 * interagiscono fra loro scambiandosi benessere in base alla loro strategia e posizione relativa.
 * 
 * Sara' possibile notare come, partendo da una generazione casuale, si andranno a formare dei cluster
 * di giocatori di diverso tipo che si aggregano andando a formare dei pattern. Il colore del giocatore
 * denota la sua strategia, e la luminosita' del colore denota il suo benessere. 
 * Quando un giocatore raggiunge il massimo del benessere tentera' di riprodursi 
 * oppure morira' se il suo benessere si esaurisce oppure se il numero di vicini non e' compreso tra 2 e 3 inclusi
 * 
 * @author Federico Agarinis
 * @version 1.2
 * @since 9-05-2019
 */

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
