package socialGameSystem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import giocatori.Generoso;
import giocatori.TipoGiocatore;
import interfacciaGraficaSGS.CellaMatrice;
import interfacciaGraficaSGS.GraphicsSGS;
/**
 * questa classe rappresenta ogni singola istanza di un giocatore sullo scacchiere relazionale e include
 * i metodi che devono essere implementati dalle sottoclassi (che sono tipologie specifiche di giocatore).
 * E' astratta perché tutti i giocatori saranno sempre una sottoclasse di questa classe e dovranno implementare
 * dei metodi per cui differiscono, in particolare la quantita' di benessere che inviano e ricevono, inoltre
 * questa implementazione permette di sperimentare con facilita' cosa succede se si cambia il modo di ricevere/inviare
 * messaggi di un giocatore di strategia x.
 *
 */
public abstract class Giocatore {
	// Contatore degli ID che assicura che ogni giocatore abbia un ID diverso (non usato)
	private static long playerIdCounter = 0;
	
	// Parametri del singolo giocatore
	private TipoGiocatore 	strategy; 															
	private float 			wealth; 															
	private boolean 		death 				= false;						
	private int 			turnsAlive 			= 0;						
	private int 			numberOfReproduce 	= Parameters.NUMBER_OF_REPRODUCE;
	
	//Ultimi messaggi ricevuti (non piu' usato, prima la strategia dei mediatori dipendeva dall'ultimo messaggio ricevuto)
	private float[] lastMessages = new float[Parameters.RELATIONSHIP_SIZE]; 
	
	// Parametri del singolo giocatore per l'interfaccia grafica
	private int 			riga, colonna; 	// Coordinate sulla Board
	private Color 			colore;
	private Direzione 		direzione;		// Direzione sulla Board: up, right, down, left
	private CellaMatrice 	cella;
	private long 			playerId;		// Per usi futuri


	/**
	 * Costruttore che inizializza un giocatore con direzione casuale, prima
	 * che esso sia inserito nella RelationalBoard
	 * 
	 * @param _strategy Il tipo di strategia usata dal giocatore
	 */
	protected Giocatore(TipoGiocatore _strategy) { 		
		this.strategy = _strategy;
		this.wealth = Parameters.BASE_WEALTH;
		this.playerId = playerIdCounter++;
	
	}

	protected Giocatore (TipoGiocatore _strategy, Direzione direzione, int riga, int colonna) {
		this(_strategy);
		this.setDirection(direzione); //deve essere prima di .setPlayer
		this.setPosition(riga, colonna);
		
		// Assegnazione della cella (GUI)
		CellaMatrice labelOfPlayer = CellaMatrice.getCella(riga, colonna);
		this.setCella(labelOfPlayer);	//il giocatore contiene la cella dove risiede
		labelOfPlayer.setPlayer(this); // Il label contiene il giocatore che ospita
		this.updateColore();

	}
	

	public void addTurn() {
		this.turnsAlive += 1;
	}
	/**
	 * tale metodo viene usato solamente se si conferiscono turni di immunita' alla morte alla
	 * nascita del giocatore con il parametro {@link Parameters#TURNS_OF_IMMUNITY}
	 */
	public boolean isImmune() {
		return this.turnsAlive < Parameters.TURNS_OF_IMMUNITY;
	}
	
	public long getId() {
		return this.playerId;
	}
	
	
	public Direzione direction() {
		return this.direzione;
	}
	
	public void setCella(CellaMatrice _cella) {
		this.cella = _cella;
	}
	
	public JLabel getLabel() {
		return this.cella.getLabel();
	}
	

	public int getRiga() {
		return this.riga;
	}
	
	public int getColonna() {
		return this.colonna;
	}
	
	public int numeroDiRelazioni() {
		int relazioni = 0;
		for (int i = 1; i < Parameters.RELATIONSHIP_SIZE; i++) if (relationship(i) != null) relazioni+=1;
		return relazioni;
	}
	
	
	public float wealthPercent() {
		return  ((float)(this.wealth) /(Parameters.MAX_WEALTH - Parameters.MIN_WEALTH));
	}
	
	public Color getColore() {
		return this.colore;
	}
	
	
	public void setDirection(Direzione _direzione) {
		this.direzione = _direzione;
		
	}
	
	public TipoGiocatore strategy() {
		return this.strategy;
	};

	
	public boolean isAlive() {
		return !this.death;
	}

	public CellaMatrice getCella() {
		return this.cella;
	}
	
	/**
	 *  Il giocatore muore e lo sfondo della sua cella viene resettato, diminuendo il contatore
	 *  dei giocatori vivi di 1
	 */
	protected void die() {
		this.wealth = Parameters.MIN_WEALTH;
		if (this.isImmune()){
			return;
		}
		this.death = true;
		RelationalBoard.deleteCella(this.riga, this.colonna);
	}

	/**
	 * Replica il giocatore nelle celle bersaglio intorno a se' che hanno un determinato numero di vicini
	 * 
	 * @param numeroVicini Quanti vicini devono avere le celle bersaglio
	 * @param randomStrategySpawn Genera un giocatore di strategia casuale se true
	 */

	public void spawnNear(int numeroVicini, boolean randomStrategySpawn) {
		TipoGiocatore strategia;
		
		if (randomStrategySpawn) strategia = TipoGiocatore.values()[new Random().nextInt(TipoGiocatore.values().length)];
		else strategia = this.strategy();
		//
		if (RelationalBoard.isEmptyCell(riga -1	, colonna -1) && RelationalBoard.numberOfNeighboursInCell(riga -1, colonna -1) == numeroVicini) RelationalBoard.addNewPlayer(riga -1, colonna -1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga -1	, colonna	) && RelationalBoard.numberOfNeighboursInCell(riga -1, colonna	 ) == numeroVicini) RelationalBoard.addNewPlayer(riga -1, colonna	, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga -1	, colonna +1) && RelationalBoard.numberOfNeighboursInCell(riga -1, colonna +1) == numeroVicini) RelationalBoard.addNewPlayer(riga -1, colonna +1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga	, colonna -1) && RelationalBoard.numberOfNeighboursInCell(riga	 , colonna -1) == numeroVicini) RelationalBoard.addNewPlayer(riga	, colonna -1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga	, colonna +1) && RelationalBoard.numberOfNeighboursInCell(riga	 , colonna +1) == numeroVicini) RelationalBoard.addNewPlayer(riga	, colonna +1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga +1	, colonna -1) && RelationalBoard.numberOfNeighboursInCell(riga +1, colonna -1) == numeroVicini) RelationalBoard.addNewPlayer(riga +1, colonna -1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga +1	, colonna	) && RelationalBoard.numberOfNeighboursInCell(riga +1, colonna	 ) == numeroVicini) RelationalBoard.addNewPlayer(riga +1, colonna	, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga +1	, colonna +1) && RelationalBoard.numberOfNeighboursInCell(riga +1, colonna +1) == numeroVicini) RelationalBoard.addNewPlayer(riga +1, colonna +1, 	strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
	}
	
	
	protected void resetWealth() {
		this.wealth = Parameters.BASE_WEALTH;
	}
	
	/**
	 *  Crea una copia del giocatore, inserendola nella relational board senza relazioni a caso o a una certa distanza dal genitore
	 *  tale metodo ha due modalita' di replicazione implementate in fase di testing
	 *  
	 * @param spawnNear indica il tipo di replicazione nel gioco che dipende da @link {@link Giocatore#RANDOM_REPRODUCE}
	 * @return true se il giocatore si e' replicato con successo almeno una volta
	 */
	protected boolean reproduce(boolean spawnNear) {
		if(RelationalBoard.isFull()) return false;
		
		// spawnNear = True: Replicazione standard nelle celle vicine
		if(spawnNear) {
			int viciniPrima = this.numeroDiRelazioni();
			this.spawnNear(Parameters.NEIGHBOURS_TO_REPRODUCE, false); //replica il giocatori nelle celle intorno a lui che hanno 3 vicini
			int viciniDopo = this.numeroDiRelazioni();
			if (viciniDopo > viciniPrima) 
				return true;	
			else
				return false;
		}
		// spawnNear = False: Il giocatore si riproduce in un punto a caso della Board
		else {
		int rigaRandom = new Random().nextInt(RelationalBoard.righe());
		int colonnaRandom = new Random().nextInt(RelationalBoard.colonne());
		if (RelationalBoard.isEmptyCell(rigaRandom, colonnaRandom) && !RelationalBoard.isCrowded(rigaRandom, colonnaRandom)){
			RelationalBoard.addNewPlayer(
					rigaRandom, 
					colonnaRandom, 
					this.strategy(), 
					Direzione.values()[new Random().nextInt(Direzione.values().length)]);
			return true;
			}
		}
		
		return false;
	};

	
	public float lastMessageFrom(int k) {
		if (relationship(k) == null) return 0;
		else return lastMessages[k];
	}

	
	public float giveModifierOfRelationship(int k) {
		return Parameters.GIVE_MODIFIER[k];
		}
	

	public float takeModifierOfRelationship(int k) {
		return Parameters.TAKE_MODIFIER[k];
	}
	

	public float benessere() {
		return this.wealth;
	}

	
	public void setPosition(int _riga, int _colonna) {
		this.riga = _riga;
		this.colonna = _colonna;
	}
	
	
	//Metodi da implementare nelle sottoclassi che variano a seconda del tipo di giocatore:
	public abstract float givePower(); 		// quanto benessere può potenzialmente dare alle sue relazioni
	public abstract float takePower(); 		// quanto benessere può potenzialmente togliere alle sue relazioni
	public abstract float talk(int i);		//invia un messaggio alla relazione k con i modificatori descritti nei MODIFIERS
	public abstract float colorHue();
	
	/**
	 * Aggiunge o sottrae benessere, facendolo morire o replicare il giocatore se il benessere
	 * raggiunge una una determinata soglia. Il colore del giocatore viene aggiornato.
	 * 
	 * @param benessereRicevuto
	 */
	public void addWealth(float benessereRicevuto) {
		this.wealth += benessereRicevuto;
		
		if (this.wealth <= Parameters.MIN_WEALTH) {
			this.die();
			return;
		}
		
		if (this.wealth >= Parameters.MAX_WEALTH) { 			// tenta di replicare il giocatore
			if (numberOfReproduce > 0 && this.reproduce(!Parameters.RANDOM_REPRODUCE)) { // se si replica resetta il benessere
				this.resetWealth();
				this.numberOfReproduce--;
			}
				
			else
				this.wealth = Parameters.MAX_WEALTH; 			//altrimenti il benesssere rimane al massimo
		}
		
		this.updateColore(); // Aggiornamento del colore della cella del giocatore nella GUI
		

	}
	
	/**
	 * Diminuisce il benessere del giocatore di {@value #OVERPOPULATION_HEALTH_LOST} se il numero di relazioni non rispetta i parametri
	 * non e' compreso tra {@value #UNDERPOPULATION}  e {@value #OVERPOPULATION} inclusi.
	 */
	public void wealthFromPopulation() {
		int r = this.numeroDiRelazioni();
		if (r > Parameters.OVERPOPULATION) {
			this.addWealth(-Parameters.OVERPOPULATION_HEALTH_LOST);
			return;
		}
		if (r < Parameters.UNDERPOPULATION) {
			this.addWealth(-Parameters.OVERPOPULATION_HEALTH_LOST);
			return;
		}
		
	}
	
	/**
	 * Il giocatore riceve messaggi da tutte le sue relazioni e aggiorna il benessere del giocatore
	 */
	public void receiveMessages() {
		float benessereRicevuto = 0;
		for(int i = 1; i < Parameters.RELATIONSHIP_SIZE; i++) 
			if (relationship(i) != null) {
				float benessere = receiveMessageFromRelationship(i);
				this.lastMessages[i] = benessere;
				benessereRicevuto += benessere;
			}
		
		this.addWealth(benessereRicevuto);
	}

	/**
	 * @return Un array di oggetti Giocatore contenente all'indice i la relazione numero i
	 */
	public Giocatore[] allRelationships() {
		Giocatore[] relazioni = new Giocatore[Parameters.RELATIONSHIP_SIZE];
		for (int i = 0; i < Parameters.RELATIONSHIP_SIZE; i++) {
			relazioni[i] = relationship(i);
		}
		return relazioni;
		
	}
	
	
	/**
	 * In base alle coordinate del giocatore e alla sua direzione, trova il giocatore nella relazione
	 * i, appoggiandosi ad altri metodi privati.
	 * @param i indice relazione 	<br>123 <br>
	 * 					  				405<br>
	 * 					  				678
	 * @return oggetto Giocatore in posizione di relazione i
	 */
	public Giocatore relationship(int i) {
		if (i == 0) 								return RelationalBoard.getPlayer(this.riga, this.colonna);
		if (this.direction() == Direzione.up) 		return this.relationshipGiocatoreUp(i);
		if (this.direction() == Direzione.down) 	return this.relationshipGiocatoreDown(i);
		if (this.direction() == Direzione.left) 	return this.relationshipGiocatoreLeft(i);
		if (this.direction() == Direzione.right) 	return this.relationshipGiocatoreRight(i);
		return null;
				
	}
	
	private Giocatore relationshipGiocatoreUp(int i) {
		
		if(i == 1) return RelationalBoard.getPlayer(this.riga-1, this.colonna-1); 
		if(i == 2) return RelationalBoard.getPlayer(this.riga-1, this.colonna); 
		if(i == 3) return RelationalBoard.getPlayer(this.riga-1, this.colonna+1); 
		
		if(i == 4) return RelationalBoard.getPlayer(this.riga, this.colonna-1); 
		if(i == 5) return RelationalBoard.getPlayer(this.riga, this.colonna+1); 
		
		if(i == 6) return RelationalBoard.getPlayer(this.riga+1, this.colonna-1);
		if(i == 7) return RelationalBoard.getPlayer(this.riga+1, this.colonna);
		if(i == 8) return RelationalBoard.getPlayer(this.riga+1, this.colonna+1);
		return null; 
	}
	
	private Giocatore relationshipGiocatoreDown(int i) {		
		if(i == 1) return RelationalBoard.getPlayer(this.riga+1, this.colonna+1); 
		if(i == 2) return RelationalBoard.getPlayer(this.riga+1, this.colonna); 
		if(i == 3) return RelationalBoard.getPlayer(this.riga+1, this.colonna-1);
		
		if(i == 4) return RelationalBoard.getPlayer(this.riga, this.colonna+1); 
		if(i == 5) return RelationalBoard.getPlayer(this.riga, this.colonna-1); 
		
		if(i == 6) return RelationalBoard.getPlayer(this.riga-1, this.colonna+1);
		if(i == 7) return RelationalBoard.getPlayer(this.riga-1, this.colonna);
		if(i == 8) return RelationalBoard.getPlayer(this.riga-1, this.colonna-1); 
		return null; 
	}
	private Giocatore relationshipGiocatoreLeft(int i) {
		if(i == 1) return RelationalBoard.getPlayer(this.riga+1, this.colonna-1); 
		if(i == 2) return RelationalBoard.getPlayer(this.riga, this.colonna-1); 
		if(i == 3) return RelationalBoard.getPlayer(this.riga-1, this.colonna-1);
		
		if(i == 4) return RelationalBoard.getPlayer(this.riga+1, this.colonna); 
		if(i == 5) return RelationalBoard.getPlayer(this.riga-1, this.colonna); 
		
		if(i == 6) return RelationalBoard.getPlayer(this.riga+1, this.colonna+1);
		if(i == 7) return RelationalBoard.getPlayer(this.riga, this.colonna+1);
		if(i == 8) return RelationalBoard.getPlayer(this.riga-1, this.colonna+1); 
		return null; 
	}		
	private Giocatore relationshipGiocatoreRight(int i) {
		if(i == 1) return RelationalBoard.getPlayer(this.riga-1, this.colonna+1); 
		if(i == 2) return RelationalBoard.getPlayer(this.riga, this.colonna+1); 
		if(i == 3) return RelationalBoard.getPlayer(this.riga+1, this.colonna+1);
		
		if(i == 4) return RelationalBoard.getPlayer(this.riga-1, this.colonna); 
		if(i == 5) return RelationalBoard.getPlayer(this.riga+1, this.colonna); 
		
		if(i == 6) return RelationalBoard.getPlayer(this.riga-1, this.colonna-1);
		if(i == 7) return RelationalBoard.getPlayer(this.riga, this.colonna-1);
		if(i == 8) return RelationalBoard.getPlayer(this.riga+1, this.colonna-1); 
		return null; 
	}
		
	
	
	/**
	 * @param k indice della relazione la quale si vuole sapere come questa vede il giocatore
	 * @return l'indice della relazione del giocatore rispetto alla relazione k
	 */
	protected int howIsSeenByRelationship(int k) {
		for(int i = 1; i < Parameters.RELATIONSHIP_SIZE; i++) {
			if(relationship(k).relationship(i) == this) return i;
		}
		return -1; // errore, non dovrebbe mai arrivare qui.
	}
	
	/**
	 *  Ricevi un singolo messaggio da una relazione specifica
	 * @param i indice della relazione
	 * @return valore di benessere ricevuto dalla comunicazione con la relazione i
	 */
	public float receiveMessageFromRelationship(int i) {
		Giocatore amico = relationship(i);
		if (amico == null) 
			return 0;
		else
			return amico.talk(this.howIsSeenByRelationship(i));
	}
	
	/**
	 * aggiorna la luminosita' del colore del giocatore 
	 * dopo qualsiasi aggiornamento del benessere
	 */
	protected void updateColore() {
		float brightness = (float) 0.5*(1+this.wealthPercent()); //la luminosita del colore aumenta con 
		float hueGiocatore = 0;									//il benessere del giocatore
		hueGiocatore = this.colorHue();
		
		this.colore = Color.getHSBColor(hueGiocatore, 0.5f, brightness); 
		this.getLabel().setBackground(this.getColore());	
		this.getCella().setDirectionImage(direzione);
	}
	


	/**
	 * Usato opzionalmente se si vuole usare per lo scambio di messaggi,
	 * ad esempio se true potrebbe diminuire la quantita' di benessere dato di un giocatore
	 */
	public boolean hasRelationshipWithEgoista() {
		for (int i = 1; i < Parameters.RELATIONSHIP_SIZE; i++) {
			if (this.relationship(i) != null && this.relationship(i).strategy() == TipoGiocatore.egoista) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Usato opzionalmente se si vuole usare per lo scambio di messaggi,
	 * ad esempio se true potrebbe aumentare la quantita' di benessere dato di un giocatore
	 */
		public boolean hasRelationshipWithGeneroso() {
			for (int i = 1; i < Parameters.RELATIONSHIP_SIZE; i++) {
				if (this.relationship(i) != null && this.relationship(i).strategy() == TipoGiocatore.generoso) {
					return true;
				}
			}
			return false;
		}
	
	
	
}
