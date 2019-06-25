package socialGameSystem;
import javax.swing.JLabel;
import javax.swing.JPanel;

import giocatori.Egoista;
import giocatori.Generoso;
import giocatori.Mediatore;
import giocatori.TipoGiocatore;
import interfacciaGraficaSGS.CellaMatrice;
import interfacciaGraficaSGS.GraphicsSGS;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Time;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RelationalBoard{
	static int AlivePlayers = 0;
	static int AliveGenerosi = 0;
	static int AliveMediatori = 0;
	static int AliveEgoisti = 0;
	
	static boolean isRunning = false;
	
	static int Turn = 0;
	private static Giocatore[][] matriceGiocatori; //matrice di oggetti giocatori
	private static int righe;
	private static int colonne;
	
	public RelationalBoard(int _righe, int _colonne) {
		matriceGiocatori = new Giocatore[_righe][_colonne];
		righe = _righe;
		colonne = _colonne;
	}
	
	public int livingPlayer() {
		return AlivePlayers;
	}
	
	public static int getTurn() {
		return Turn;
	}
	
	public static boolean isFull() {
		return AlivePlayers == righe*colonne;
	}
	
	public static int righe() {
		return righe;
		
	}
	public static int colonne() {
		return colonne;
	}
	
	/**
	 * @return il label grafico della cella in posizione [riga][colonna]
	 */
	public JLabel getLabel(int riga, int colonna) {
		return GraphicsSGS.celle[riga*GraphicsSGS.colonne + colonna];
	}
	
	public static Giocatore getPlayer(int riga, int colonna) {
		try {
		return matriceGiocatori[riga][colonna];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			
		};
		return null;
	}

	public static int numberOfNeighboursInCell(int riga, int colonna) {
		int popolazioneIntornoCella = 0;
		
		if (getPlayer(riga-1, colonna-1) != null) popolazioneIntornoCella+=1; 
		if (getPlayer(riga-1, colonna) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga-1, colonna+1) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga, colonna-1) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga, colonna+1) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga+1, colonna-1) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga+1, colonna) != null) popolazioneIntornoCella+=1;
		if (getPlayer(riga+1, colonna+1) != null) popolazioneIntornoCella+=1;
		
		
		return popolazioneIntornoCella;
	}
	
	//controlla se la cella e' sovrappopolata
	public static boolean isCrowded(int riga, int colonna) {
		return numberOfNeighboursInCell(riga, colonna) > Parameters.OVERPOPULATION;
	}
	
	
	public static boolean isEmptyCell(int riga, int colonna) {
		try {
			return matriceGiocatori[riga][colonna] == null;
		}catch (ArrayIndexOutOfBoundsException e) {
			return false; //se la coordinata e' fuori dalla board
		}
		
		
	}
	
	/**
	 * Inserisce un nuovo giocatore nella board, settandone il colore.
	 */
	public static void addNewPlayer(int riga, int colonna, TipoGiocatore tipoPlayer, Direzione direzione) {
		Giocatore player = null;
		if (tipoPlayer == TipoGiocatore.egoista) player = new Egoista(direzione, riga, colonna);
		if (tipoPlayer == TipoGiocatore.mediatore) player = new Mediatore(direzione, riga, colonna);
		if (tipoPlayer == TipoGiocatore.generoso) player = new Generoso(direzione, riga, colonna);
		
		matriceGiocatori[riga][colonna] = player;
		addAlivePlayerTextField(1, player);
		
		
	}
	
	public static void addTurnTextField(int n) {
		RelationalBoard.Turn += n;
	    GraphicsSGS.textField_turn.setText(String.valueOf(RelationalBoard.getTurn()));
	}
	
	public static void addAlivePlayerTextField(int n, Giocatore player) {
		AlivePlayers += n;
		GraphicsSGS.textField_alivePlayers.setText(String.valueOf(AlivePlayers));
	}

	public static void resetTurnTextField() {
		RelationalBoard.Turn = 0;
	    GraphicsSGS.textField_turn.setText(String.valueOf(RelationalBoard.getTurn()));
	}
	
	public static void resetAlivePlayersTextField() {
		RelationalBoard.AlivePlayers = 0;
		GraphicsSGS.textField_alivePlayers.setText(String.valueOf(AlivePlayers));
		AliveGenerosi = 0;
		AliveMediatori = 0;
		AliveEgoisti = 0;
	}
	
	//genera fino a n nuovi giocatori random sulla board
	public static void addRandomPlayers(int n) {
		boolean wasRunning = false;
		if(isRunning()) {
			while(isRunning) {
				stopRun();
			}
			wasRunning = true;
		}
		while (n > 0) {
			int riga = new Random().nextInt(righe);
			int colonna = new Random().nextInt(colonne);
			if (isEmptyCell(riga, colonna)) {
			addNewPlayer(
					riga,
					colonna, 
					TipoGiocatore.values()[new Random().nextInt(TipoGiocatore.values().length)], 
					Direzione.values()[new Random().nextInt(Direzione.values().length)]
							);
			}
			n--;
		}
		if(wasRunning) startRun();
		//updateAllPlayerRelationships();
	}
	
	
	public float getAliveGenerosiPercent(){
		return this.AliveGenerosi/this.AlivePlayers * 100;
	}
	
	public float getAliveMediatoriPercent(){
		return this.AliveMediatori/this.AlivePlayers * 100;
	}
	
	public float getAliveEgoistiPercent(){
		return this.AliveEgoisti/this.AlivePlayers * 100;
	}
	
	public static void resetBoard() {
		while(isRunning()) stopRun();
		
		for (Giocatore[] riga: matriceGiocatori)
			for (Giocatore player: riga) {
				if (player != null)player.die();
			}
		
		resetTurnTextField();
		//resetAlivePlayersTextField();
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
	
	public static void startRun() {
		isRunning = true;
		GraphicsSGS.StartStopButton.setText("Stop");
		GraphicsSGS.timer = new Timer();
		GraphicsSGS.stepButton.setEnabled(false);
		//GraphicsSGS.comboBox_tipoGiocatore.setEnabled(false);
		//GraphicsSGS.comboBox_direzioni.setEnabled(false);
		
		GraphicsSGS.timer.schedule( new TimerTask() {
		    public void run() {
		       RelationalBoard.step();
		    }
		 }, 5, GraphicsSGS.getSpeed());
	}
	
	public static void stopRun() {
		GraphicsSGS.timer.cancel();
		isRunning = false;
		GraphicsSGS.stepButton.setEnabled(true);
		GraphicsSGS.StartStopButton.setText("Start");
	}
	
	/**
	 * Resetta l'aspetto grafico e logico della cella
	 */
	public static void deleteCella(int riga, int colonna) {
		Giocatore giocatoreDaEliminare = matriceGiocatori[riga][colonna];
		matriceGiocatori[riga][colonna] = null;
		if (giocatoreDaEliminare != null) {
			giocatoreDaEliminare.getCella().makeCellEmpty();
			RelationalBoard.AlivePlayers -= 1;
			GraphicsSGS.textField_alivePlayers.setText(String.valueOf(RelationalBoard.AlivePlayers));
		}
		
	}
	

	
	
	public static void step() {
		
		for (Giocatore[] riga: matriceGiocatori)
			for (Giocatore player: riga) {
				if (player != null) {
					player.addTurn(); //aggiunge 1 al turno in cui il giocatore e' stato vivo
					player.receiveMessages(); //il benessere si aggiorna dai messaggi che arrivano
					player.wealthFromPopulation(); //il benessere si aggiorna per sovrapopolazione o solitudine
					
				}
			}
		
		addTurnTextField(1);
		
	}
	
	
	public class runStep extends TimerTask {
		public void run() {
	       step(); 
	    }
	}
	
}
