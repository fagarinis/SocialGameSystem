package socialGameSystem;


public class Parameters {
/**
 * e' Necessasrio ancora implementare questa classe nelle altre
 */
	// Parametri statici per tutti i giocatori
	public final static float MIN_WEALTH = 0; // benessere Quando il giocatore muore
	public final static float MAX_WEALTH = 100; // benessere Quando il giocatore si replica
	public final static float BASE_WEALTH = (MAX_WEALTH - MIN_WEALTH) / 2; // Benessere alla creazione del giocatore
	public final static int RELATIONSHIP_SIZE = 9; // Numero di relazioni massime

	
	public final static int OVERPOPULATION = 3; 			   	// piu' relazioni di queste e il giocatore perde benessere
	public final static int UNDERPOPULATION = 2; 				// meno relazioni di queste e il giocatore perde benessere
	public final static int OVERPOPULATION_HEALTH_LOST = 90; 	// benessere perso per sovrappopolazione/depopolazione
	
	
	//Parametri GUI
	public final static int righe = 40; //RIGHE DELLA MATRICE QUADRATA
	
	// Modificatori take/give wealth per tipo di relazione {0, 1, 2, 3, 4, 5, 6, 7}
	// implementazione con 9 relazioni dove 0 è la posizione del giocatore ed e' girato verso l'alto:
	// 1 2 3
	// 4 ^ 5
	// 6 7 8
	private final static float[] GIVE_MODIFIER = { 
			0, 		// 0 (^): il Giocatore stesssso
			1,		// 1: Superiore 
			3, 		// 2: Infatuazione 
			1,	 	// 3: Superiore 
			1.5f, 	// 4: Amicizia 
			1.5f, 	// 5: Amicizia 
			0.25f, 	// 6: Dipendente 
			0.1f, 	// 7: Inimicizia 
			0.25f 	// 8: Dipendente 
	};
	private final static float[] TAKE_MODIFIER = {
			0, 
			0.25f, 	// 1: Superiore 
			0.1f, 	// 2: Infatuazione
			0.25f, 	// 3: Superiore 
			1.5f, 	// 4: Amicizia 
			1.5f, 	// 5: Amicizia 
			1, 		// 6: Dipendente 
			3, 		// 7: Inimicizia 
			1 		// 8: Dipendente 
			};
	
	
	
	public static float giveModifierOfRelationship(int k) {
		return GIVE_MODIFIER[k];
		}
	

	public static float takeModifierOfRelationship(int k) {
		return TAKE_MODIFIER[k];
	}
}
