package socialGameSystem;
import java.awt.Color;
import java.util.Random;

public abstract class Giocatore {
	// Parametri statici per tutti i giocatori
	private final static float MIN_WEALTH = 0;	 								// Quando il giocatore muore
	private final static float MAX_WEALTH = 100; 								// Quando il giocatore si replica
	private final static float BASE_WEALTH = (MAX_WEALTH-MIN_WEALTH) / 2; 		// Benessere alla creazione del giocatore
	protected final static int RELATIONSHIP_SIZE = 9; 							// Numero di relazioni massime
	
	private final static float[] GIVE_MODIFIER = {0, 1, 2, 1, (float) 1.5, (float) 1.5, 0, 0, 0 };
	private final static float[] TAKE_MODIFIER = {0, 0, 0, 0, (float) 1.5, (float) 1.5, 1, 2, 1	};

	// Parametri del singolo giocatore
	private TipoGiocatore strategy; 					// Generoso, Mediatore, Egoista
	private float wealth; 								// Valore del benessere compreso tra MIN_WEALTH e MAX_WEALTH
	private boolean death = false;						// Il giocatore e' vivo alla creazione
	
	protected Giocatore[] relationships = new Giocatore[RELATIONSHIP_SIZE]; // I giocatori con cui si è in relazione
	protected float[] lastMessages = new float[RELATIONSHIP_SIZE]; 			// Ultimi messaggi ricevuti
	
	// Parametri del singolo giocatore per l'interfaccia grafica (opzionale)
	private Color colore;
	private Direzione direzione;						// Direzione sulla Board: up, right, down, left
	protected int x, y; 								// Coordinate sulla Board


	// Costruttore
	protected Giocatore(TipoGiocatore _strategy) { 		
		this.strategy = _strategy;
		this.wealth = BASE_WEALTH;
		this.relationships[0] = this; // ogni giocatore è sempre in relazione a se' stesso in posizione 0
		this.direzione = Direzione.values()[new Random().nextInt(Direzione.values().length)]; // direzione random
	}

	public Direzione direction() {
		return this.direzione;
	}
	
	// Returna la tipologia di strategia del giocatore
	public TipoGiocatore strategy() {
		return this.strategy;
	};

	// Controlla se il giocatore e' vivo
	public boolean isAlive() {
		return !this.death;
	}

	// Il giocatore muore e viene rimosso dalle relazioni dei suoi conoscenti
	private void die() {
		this.death = true;
		this.wealth = MIN_WEALTH;
		for (int i = 0; i < relationships.length - 1; i++) {
			this.relationships[i].removePlayerFromRelationships(this); 
		}
	}

	// Rimuove x dalle relazioni del giocatore
	private void removePlayerFromRelationships(Giocatore x) {	
		for (int i = 0; i < relationships.length - 1; i++) {
			if (this.relationships[i] == x) {
				this.relationships[i] = null;
			};
		}

	}

	// Opzionale: resetta il benessere dopo la replicazione
	private void resetWealth() {
		this.wealth = BASE_WEALTH;
	}

	// Aggiunge o sottrae benessere, facendolo morire o replicare a una determinata soglia
	public void addWealth(int _wealth) {
		this.wealth += _wealth;
		if (this.wealth <= MIN_WEALTH) { // il giocatore muore
			this.die();
		}
		if (this.wealth >= MAX_WEALTH) { // replica il giocatore
			this.reproduce();
			this.resetWealth();
		}

	}

	// Crea una copia del giocatore, inserendola nella relational board senza relazioni a caso o a una certa distanza dal genitore
	private void reproduce() {
	};

	// Returna l'ultimo messaggio ricevuto dalla relazione in posizione k
	public float lastMessageFrom(int k) {
		if (relationships[k] == null)
			return 0;
		else
			return lastMessages[k];
	}

	public TipoRelazione typeOfRelationshipWith(int k) {
		return TipoRelazione.values()[k];
	}
	
	//Returna il modificatore Give del giocatore per la relazione K
	public float giveModifierOfRelationship(int k) {
		return GIVE_MODIFIER[k];
		}
	
	//Returna il modificatore Take del giocatore per la relazione K
	public float takeModifierOfRelationship(int k) {
		return TAKE_MODIFIER[k];
	}
	
	// Returna il benessere del giocatore
	public float benessere() {
		return this.wealth;
	}

	
	//Metodi da implementare nelle sottoclassi che variano a seconda del tipo di giocatore:
	public abstract float givePower(); 		// quanto benessere può potenzialmente dare alle sue relazioni
	public abstract float takePower(); 		// quanto benessere può potenzialmente togliere alle sue relazioni
	public abstract void talk(int k);		//invia un messaggio alla relazione k con i modificatori descritti nei MODIFIERS
	public abstract void receiveMessages(); //ricevi messaggi da tutte le relazioni
	public abstract Color colore();			//colore del giocatore (interfaccia grafica)
	public abstract void setPosition(int _x, int _y); //setta la posizione sulla Board
	
	// Controlla se si è in relazione con almeno un egoista (escluso se' stesso)
	public boolean hasRelationshipWithEgoista() {
		for (int i = 1; i < relationships.length - 1; i++) {
			if (this.relationships[i] != null && this.relationships[i].strategy() == TipoGiocatore.egoista) {
				return true;
			}
		}
		return false;
	}
	
}
