package socialGameSystem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import interfacciaGraficaSGS.GraphicsSGS;

public abstract class Giocatore {
	// Parametri statici per tutti i giocatori
	protected final static float MIN_WEALTH = 0;	 								// benessere Quando il giocatore muore
	protected final static float MAX_WEALTH = 100; 								// benessere Quando il giocatore si replica
	private final static float BASE_WEALTH = (MAX_WEALTH-MIN_WEALTH) / 2; 		// Benessere alla creazione del giocatore
	protected final static int RELATIONSHIP_SIZE = 9; 							// Numero di relazioni massime

	
	private final static float[] GIVE_MODIFIER = {0, 1, 3, 1, (float) 1.5, (float) 1.5, 0.25f, 0.1f, 0.25f };
	private final static float[] TAKE_MODIFIER = {0, 0.25f, 0.1f, 0.25f, (float) 1.5, (float) 1.5, 1, 2, 1	};
	
	public final static int OVERPOPULATION = 3; 			   	//se si hanno piu relazioni di queste si perde benessere
	public final static int UNDERPOPULATION = 2; 				//se si hanno meno relazioni di queste si perde benessere
	private final static int OVERPOPULATION_HEALTH_LOST = 100; 	//benessere perso per sovrappopolazione/depopolazione
	private static int playerIdCounter = 0;

	public final static boolean RANDOM_REPRODUCE = false; //se true i giocatori si replicheranno a caso sulla mappa
	public final static int TURNS_OF_IMMUNITY = 0; //turni in cui un giocatore e' immune agli effetti dell under/overpopulation
	
	
	// Parametri del singolo giocatore
	private TipoGiocatore strategy; 					// Generoso, Mediatore, Egoista
	protected float wealth; 							// Valore del benessere compreso tra MIN_WEALTH e MAX_WEALTH
	private boolean death = false;						// Il giocatore e' vivo alla creazione
	private int turnsAlive = 0;
	
	//public Giocatore[] relationships = new Giocatore[RELATIONSHIP_SIZE]; // I giocatori con cui si è in relazione
	protected float[] lastMessages = new float[RELATIONSHIP_SIZE]; 			// Ultimi messaggi ricevuti
	
	// Parametri del singolo giocatore per l'interfaccia grafica (opzionale)
	protected Color colore;
	private Direzione direzione;						// Direzione sulla Board: up, right, down, left
	protected int riga, colonna; 						// Coordinate sulla Board
	protected JLabel label;
	protected int playerId;

	// Costruttore
	protected Giocatore(TipoGiocatore _strategy) { 		
		this.strategy = _strategy;
		this.wealth = BASE_WEALTH;
		
		this.direzione = Direzione.values()[new Random().nextInt(Direzione.values().length)]; // direzione random
		this.playerId = playerIdCounter++;
	}

	//aggiunge 1 ai turni in cui il giocatore e' stato vivo
	public void addTurn() {
		this.turnsAlive += 1;
	}
	
	public boolean isImmune() {
		return this.turnsAlive < TURNS_OF_IMMUNITY;
	}
	
	public int getId() {
		return this.playerId;
	}
	
	public Direzione direction() {
		return this.direzione;
	}
	
	public void setLabel(JLabel _label) {
		this.label = _label;
	}
	
	public JLabel getLabel() {
		return this.label;
	}
	

	public int getRiga() {
		return this.riga;
	}
	
	public int getColonna() {
		return this.colonna;
	}
	
	public int numeroDiRelazioni() {
		int relazioni = 0;
		for (int i = 1; i < RELATIONSHIP_SIZE; i++) if (relationship(i) != null) relazioni+=1;
		return relazioni;
	}
	
	
	public float wealthPercent() {
		return  ((float)(this.wealth) /(MAX_WEALTH - MIN_WEALTH));
	}
	
	public Color getColore() {
		return this.colore;
	}
	
	public void setDirection(Direzione _direzione) {
		this.direzione = _direzione;
		label.createImage(label.getWidth()/2, label.getHeight());
		
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
	protected void die() {
		this.wealth = MIN_WEALTH;
		if (this.isImmune()){
			return;
		}
		
		GraphicsSGS.resetLabelBackground(this.getLabel());
		this.death = true;
		RelationalBoard.deleteCella(this.riga, this.colonna);
		
		RelationalBoard.AlivePlayers -= 1;
		GraphicsSGS.textField_alivePlayers.setText(String.valueOf(RelationalBoard.AlivePlayers));
		
		
	}


	// Opzionale: resetta il benessere dopo la replicazione
	protected void resetWealth() {
		this.wealth = BASE_WEALTH;
	}
	
	///errori...
	public int[] takeNextEmptyCord(int _riga, int _colonna) {
		riga = _riga;
		colonna = _colonna;
		while (true) {
			if (RelationalBoard.isEmptyCell(riga, colonna)) break;
			colonna+=1;
			if(colonna == RelationalBoard.colonne()) {colonna = 0; riga += 1;}
			if(riga == RelationalBoard.righe() && colonna == RelationalBoard.colonne()) {colonna = 0; riga = 0;}
		}
		return new int[]{riga, colonna};
		
	}
	
	//errori...
	public int[] genEmptyCoordinate() {
		int[] coordinate = new int[2];
		
		coordinate[0] = new Random().nextInt(RelationalBoard.righe());
		coordinate[1] = new Random().nextInt(RelationalBoard.colonne());
		
		if(!RelationalBoard.isEmptyCell(coordinate[0], coordinate[1])) {
			coordinate = takeNextEmptyCord(coordinate[0], coordinate[1]);
		}
		
		return coordinate;
	}

	//replica il giocatore nelle celle intorno che hanno un certo numero di vicini
	//genera un giocatore di strategia casuale se randomStrategySpawn == true
	public void spawnNear(int numeroVicini, boolean randomStrategySpawn) {
		TipoGiocatore strategia;
		if (randomStrategySpawn) strategia = TipoGiocatore.values()[new Random().nextInt(TipoGiocatore.values().length)];
		else strategia = this.strategy();
		//
		if (RelationalBoard.isEmptyCell(riga-1, colonna-1) && RelationalBoard.numberOfNeighboursInCell(riga-1, colonna-1) == numeroVicini) RelationalBoard.addNewPlayer(riga-1, colonna-1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga-1, colonna) && RelationalBoard.numberOfNeighboursInCell(riga-1, colonna) == numeroVicini)RelationalBoard.addNewPlayer(riga-1, colonna, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga-1, colonna+1) && RelationalBoard.numberOfNeighboursInCell(riga-1, colonna+1) == numeroVicini)RelationalBoard.addNewPlayer(riga-1, colonna+1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga, colonna-1) && RelationalBoard.numberOfNeighboursInCell(riga, colonna-1) == numeroVicini)RelationalBoard.addNewPlayer(riga, colonna-1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga, colonna+1) && RelationalBoard.numberOfNeighboursInCell(riga, colonna+1) == numeroVicini)RelationalBoard.addNewPlayer(riga, colonna+1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga+1, colonna-1) && RelationalBoard.numberOfNeighboursInCell(riga+1, colonna-1) == numeroVicini)RelationalBoard.addNewPlayer(riga+1, colonna-1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga+1, colonna) && RelationalBoard.numberOfNeighboursInCell(riga+1, colonna) == numeroVicini)RelationalBoard.addNewPlayer(riga+1, colonna, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		if (RelationalBoard.isEmptyCell(riga+1, colonna+1) && RelationalBoard.numberOfNeighboursInCell(riga+1, colonna+1) == numeroVicini)RelationalBoard.addNewPlayer(riga+1, colonna+1, strategia, Direzione.values()[new Random().nextInt(Direzione.values().length)]);
	}
	
	// Crea una copia del giocatore, inserendola nella relational board senza relazioni a caso o a una certa distanza dal genitore
	//returna true se il giocatore si e' replicato almeno una volta
	protected boolean reproduce(boolean spawnNear) {
		if(RelationalBoard.isFull()) return false;
		
		// Il giocatore si riproduce nelle celle vicine
		if(spawnNear) {
			int viciniPrima = this.numeroDiRelazioni();
			this.spawnNear(3, false); //replica il giocatori nelle celle intorno a lui che hanno 3 vicini
			
			int viciniDopo = this.numeroDiRelazioni();
			if (viciniDopo > viciniPrima) return true;	
		}

		/*if (RelationalBoard.isEmptyCell(rigaRandom, colonnaRandom) && RelationalBoard.numberOfNeighboursInCell(rigaRandom, colonnaRandom) == 4) //attenzione, potrebbe anche non riprodursi in questo modo!
		{RelationalBoard.addNewPlayer(rigaRandom, colonnaRandom, this.strategy(), Direzione.values()[new Random().nextInt(Direzione.values().length)]);
		return true;}*/
		else {
		// Il giocatore si riproduce in un punto a caso della Board
		int rigaRandom = new Random().nextInt(RelationalBoard.righe());
		int colonnaRandom = new Random().nextInt(RelationalBoard.colonne());
		if (RelationalBoard.isEmptyCell(rigaRandom, colonnaRandom) && !RelationalBoard.isCrowded(rigaRandom, colonnaRandom)) //attenzione, potrebbe anche non riprodursi in questo modo!
			{RelationalBoard.addNewPlayer(rigaRandom, colonnaRandom, this.strategy(), Direzione.values()[new Random().nextInt(Direzione.values().length)]);
			return true;
			}
		}
		
		return false;
	};

	// Returna l'ultimo messaggio ricevuto dalla relazione in posizione k
	public float lastMessageFrom(int k) {
		if (relationship(k) == null)
			return 0;
		else
			return lastMessages[k];
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

	
	public void setPosition(int _riga, int _colonna) {
		this.riga = _riga;
		this.colonna = _colonna;
	}
	
	//Metodi da implementare nelle sottoclassi che variano a seconda del tipo di giocatore:
	public abstract float givePower(); 		// quanto benessere può potenzialmente dare alle sue relazioni
	public abstract float takePower(); 		// quanto benessere può potenzialmente togliere alle sue relazioni
	public abstract float talk(int k);		//invia un messaggio alla relazione k con i modificatori descritti nei MODIFIERS

	
	// Aggiunge o sottrae benessere, facendolo morire o replicare a una determinata soglia
	public void addWealth(float benessereRicevuto) {
		this.wealth += benessereRicevuto;
		if (this.wealth <= MIN_WEALTH) { // il giocatore muore
			
			this.die();
			return;
			
		}
		if (this.wealth >= MAX_WEALTH) { // replica il giocatore
			if (this.reproduce(!RANDOM_REPRODUCE))  //se si replica resetta il benessere
				this.resetWealth();
			else
				this.wealth = MAX_WEALTH; //altrimenti rimane al massimo
		}
		
		this.updateColore();

	}
	
	//TEST diminuisce il benessere se si hanno troppe o troppo poche relazioni
	public void wealthFromPopulation() {
		int r = this.numeroDiRelazioni();
		if (r > OVERPOPULATION) {
			this.addWealth(-OVERPOPULATION_HEALTH_LOST);
			return;
		}
		if (r < UNDERPOPULATION) {
			this.addWealth(-OVERPOPULATION_HEALTH_LOST);
			return;
		}
		
	}
	
	//ricevi messaggi da tutte le relazioni e aggiorna il benessere totale
	public void receiveMessages() {
		float benessereRicevuto = 0;
		for(int i = 1; i < RELATIONSHIP_SIZE; i++) 
			if (relationship(i) != null) {
				float benessere = receiveMessageFromRelationship(i);
				this.lastMessages[i] = benessere;
				benessereRicevuto += benessere;
			}
		
		this.addWealth(benessereRicevuto);
	}

	public Giocatore[] allRelationships() {
		Giocatore[] relazioni = new Giocatore[RELATIONSHIP_SIZE];
		for (int i = 0; i < RELATIONSHIP_SIZE; i++) {
			relazioni[i] = relationship(i);
		}
		return relazioni;
		
	}
	
	
	
	//returna il giocatore in relazione di tipo i
	public Giocatore relationship(int i) {
		if(!(0<=i && i < 9))System.out.println(i);
		if (i == 0) return RelationalBoard.getPlayer(this.riga, this.colonna);
		if (this.direction() == Direzione.up) return this.relationshipGiocatoreUp(i);
		if (this.direction() == Direzione.down) return this.relationshipGiocatoreDown(i);
		if (this.direction() == Direzione.left) return this.relationshipGiocatoreLeft(i);
		if (this.direction() == Direzione.right) return this.relationshipGiocatoreRight(i);
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
		
	
	
	/*
	public int typeOfRelationshipWith(Giocatore player) {
		for (int i = 0; i < RELATIONSHIP_SIZE; i++) {
			if (relationships[i] != null)  relationships[i].updateRelationship();
			if (relationships[i] == player) 
				return i;
		}
		return -1; //returna -1 se non sono in relazione
	}
	*/
	//date due coordinate returna l'indice della relazione con il giocatore di coordinate (_riga, _colonna) dalla vista del giocatore
	public int indiceRelazione(int _riga, int _colonna) {
		int diff_riga = _riga - this.riga;
		int diff_colonna = _colonna - this.colonna;
		//System.out.println(diff_riga + " "+ diff_colonna + "giocatore: "+this+" (" +riga+","+colonna+") confronto con  ("+_riga+","+_colonna+") " );
		if (diff_riga == 0 && diff_colonna == 0) return 0;
		if (this.direction() == Direzione.up) {
			if (diff_riga == -1 && diff_colonna == -1)	return 1; if (diff_riga == -1 && diff_colonna == 0) return 2; 	if (diff_riga == -1 && diff_colonna == +1) return 3;
			if (diff_riga == 0 	&& diff_colonna == -1) 	return 4;														if (diff_riga == 0 	&& diff_colonna == +1) return 5;
			if (diff_riga == 1 	&& diff_colonna == -1) 	return 6; if (diff_riga == 1  && diff_colonna == 0)	return 7; 	if (diff_riga == 1 	&& diff_colonna == 1)  return 8;
		}
		if (this.direction() == Direzione.down) {
			if (diff_riga == -1 && diff_colonna == -1)	return 8; if (diff_riga == -1 && diff_colonna == 0) return 7; 	if (diff_riga == -1 && diff_colonna == +1) return 6;
			if (diff_riga == 0 	&& diff_colonna == -1) 	return 5;														if (diff_riga == 0 	&& diff_colonna == +1) return 4;
			if (diff_riga == 1 	&& diff_colonna == -1) 	return 3; if (diff_riga == 1  && diff_colonna == 0)	return 2; 	if (diff_riga == 1 	&& diff_colonna == 1)  return 1;
		
		}
		if (this.direction() == Direzione.left) {
			if (diff_riga == -1 && diff_colonna == -1)	return 3; if (diff_riga == -1 && diff_colonna == 0) return 5; 	if (diff_riga == -1 && diff_colonna == +1) return 8;
			if (diff_riga == 0 	&& diff_colonna == -1) 	return 2;														if (diff_riga == 0 	&& diff_colonna == +1) return 7;
			if (diff_riga == 1 	&& diff_colonna == -1) 	return 1; if (diff_riga == 1  && diff_colonna == 0)	return 4; 	if (diff_riga == 1 	&& diff_colonna == 1)  return 6;
		}
		if (this.direction() == Direzione.right) {
			if (diff_riga == -1 && diff_colonna == -1)	return 6; if (diff_riga == -1 && diff_colonna == 0) return 4; 	if (diff_riga == -1 && diff_colonna == +1) return 1;
			if (diff_riga == 0 	&& diff_colonna == -1) 	return 7;														if (diff_riga == 0 	&& diff_colonna == +1) return 2;
			if (diff_riga == 1 	&& diff_colonna == -1) 	return 8; if (diff_riga == 1  && diff_colonna == 0)	return 5; 	if (diff_riga == 1 	&& diff_colonna == 1)  return 3;
		}
		
		
		
		return -1;
	}
	
	//returna come l'indice k vede il giocatore
	protected int howIsSeenByRelationship(int k) {
		for(int i = 1; i < RELATIONSHIP_SIZE+1; i++) {
			if(relationship(k).relationship(i) == this) return i;
		}
		return -1; //errore
	}
	
	// Ricevi un singolo messaggio da una relazione specifica
	public float receiveMessageFromRelationship(int i) {
		Giocatore amico = relationship(i);
		if (amico == null) 
			return 0;
		else
			return amico.talk(this.howIsSeenByRelationship(i));
	}
	
	//aggiorna il colore dopo qualsiasi aggiornamento del benessere
	protected void updateColore() {
		float brightness = (float) 0.5*(1+this.wealthPercent()); //la luminosita del colore aumenta con 
		float hueGiocatore = 0;									//il benessere del giocatore
		if (this.strategy() == TipoGiocatore.generoso) hueGiocatore = Generoso.COLORE;
		if (this.strategy() == TipoGiocatore.mediatore) hueGiocatore = Mediatore.COLORE;
		if (this.strategy() == TipoGiocatore.egoista) hueGiocatore = Egoista.COLORE;
		
		this.colore = Color.getHSBColor(hueGiocatore, 0.5f, brightness); 
		this.label.setBackground(this.getColore());	
	}
	
	
	// Controlla se si è in relazione con almeno un egoista (escluso se' stesso)
	public boolean hasRelationshipWithEgoista() {
		for (int i = 1; i < RELATIONSHIP_SIZE; i++) {
			if (this.relationship(i) != null && this.relationship(i).strategy() == TipoGiocatore.egoista) {
				return true;
			}
		}
		return false;
	}
	
	// Controlla se si è in relazione con almeno un generoso (escluso se' stesso)
		public boolean hasRelationshipWithGeneroso() {
			for (int i = 1; i < RELATIONSHIP_SIZE; i++) {
				if (this.relationship(i) != null && this.relationship(i).strategy() == TipoGiocatore.generoso) {
					return true;
				}
			}
			return false;
		}
	
	
	
}
