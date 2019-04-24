package socialGameSystem;

public enum TipoRelazione {

	// una freccia/trattino indichera' verso dove il giocatore e' girato

	// implementazione con 9 relazioni dove 0 è la posizione del giocatore ed e' girato
	// verso l'alto

	// 1 2 3
	// 4 0 5
	// 6 7 8
	
									//moltiplicatori dei parametri del giocatore quando comunica con quella relazione
	//  Come lo vede il giocatore 	<GiveModifier>	<TakeModifier>
	// 0: Sé stesso 				: 0 			: 0
	// 1: Superiore					: 1 			: 0
	// 2: Infatuazione 				: 2				: 0
	// 3: Superiore					: 1				: 0
	// 4: Amicizia					: 1.5			: 1.5
	// 5: Amicizia 					: 1.5			: 1.5
	// 6: Dipendente				: 0				: 1
	// 7: Inimicizia 				: 0				: 2
	// 8: Dipendente				: 0				: 1
	
	self, superior1, love, superior2, friend1, friend2, subordinate1, opponent, subordinate2
}
