package socialGameSystem;
import java.awt.EventQueue;
import interfacciaGraficaSGS.GraphicsSGS;

/**
 * Il Social Game System implementa una scacchiera relazionale in cui diversi tipi di giocatori
 * interagiscono fra loro scambiandosi benessere in base alla loro strategia e posizione relativa.<br><br>
 * 
 * Sara' possibile notare come, partendo da una generazione casuale, si andranno a formare dei cluster
 * di giocatori di diverso tipo che si aggregano andando a formare dei pattern. Il colore del giocatore
 * denota la sua strategia, e la luminosita' del colore denota il suo benessere. 
 * <br><br>
 * Quando un giocatore raggiunge il massimo del benessere provera' a replicarsi nelle celle vicine che hanno 
 * un numero di vicini pari alla variabile {@link Parameters#NEIGHBOURS_TO_REPRODUCE} 
 * <br> il massimo di replicazioni di successo per un giocatore e' pari a: {@link Parameters#NUMBER_OF_REPRODUCE}<br><br>
 * Il giocatore morira' se il suo benessere si esaurisce 
 * oppure se il numero di vicini non e' compreso tra {@link Parameters#UNDERPOPULATION} e {@link Parameters#OVERPOPULATION} inclusi
 * 
 * @version 1.4
 * @since 7-07-2019
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
