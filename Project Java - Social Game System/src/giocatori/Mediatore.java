package giocatori;

import java.awt.Color;

import socialGameSystem.Direzione;
import socialGameSystem.Giocatore;

public class Mediatore extends Giocatore {

	protected final static float COLORE = 150/255f;
	
	// parametri della strategia
	private final static float BASE_GIVE = 3; // quando benessere puo' dare alle relazioni ogni messaggio
	private final static float BASE_TAKE = 3; // quando benessere puo' togliere dalle relazioni ogni messaggio
	
	public Mediatore() {
		super(TipoGiocatore.mediatore);
	}
	
	public Mediatore(Direzione direzione, int riga, int colonna) {
		super(TipoGiocatore.mediatore, direzione, riga, colonna);
	}
	
	@Override
	public float givePower() {
		// TODO Auto-generated method stub
		return BASE_GIVE ;
	}

	@Override
	public float takePower() {
		// TODO Auto-generated method stub
		return BASE_TAKE ;
	}

	@Override
	public float talk(int k) {
		float benesserePreso = this.takePower() * this.takeModifierOfRelationship(k);
		this.addWealth(benesserePreso);
		
		float benessereDato = this.givePower() * this.giveModifierOfRelationship(k);
		return benessereDato - benesserePreso;
	}

	public float colorHue() {
		return COLORE;
	}



}
