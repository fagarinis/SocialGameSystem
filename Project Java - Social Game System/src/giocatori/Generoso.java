package giocatori;
import java.awt.Color;

import interfacciaGraficaSGS.GraphicsSGS;
import socialGameSystem.Direzione;
import socialGameSystem.Giocatore;

public class Generoso extends Giocatore {
	protected final static float COLORE = 85/255f;//Color.GREEN;

	// parametri della strategia
	private final static float BASE_GIVE = 6; // quando benessere puo' dare alle relazioni ogni messaggio
	private final static float BASE_TAKE = 1; // quando benessere puo' togliere dalle relazioni ogni messaggio


	public Generoso() {
		super(TipoGiocatore.generoso);
	}

	public Generoso(Direzione direzione, int riga, int colonna) {
		super(TipoGiocatore.generoso, direzione, riga, colonna);
	}
	
	public float colorHue() {
		return COLORE;
	}
	
	@Override
	public float givePower() {
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
			//return givePower() * this.giveModifierOfRelationship(k);
	}






	




}
