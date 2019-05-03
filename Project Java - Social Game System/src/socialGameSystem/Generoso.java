package socialGameSystem;
import java.awt.Color;

import interfacciaGraficaSGS.GraphicsSGS;

public class Generoso extends Giocatore {
	protected final static float COLORE = 85/255f;//Color.GREEN;

	// parametri della strategia
	private final static float BASE_GIVE = 6; // quando benessere puo' dare alle relazioni ogni messaggio
	private final static float BASE_TAKE = 3f; // quando benessere puo' togliere dalle relazioni ogni messaggio


	public Generoso() {
		super(TipoGiocatore.generoso);
	}

	@Override
	public float givePower() {
			return BASE_GIVE;
	}

	@Override
	public float takePower() {
		// TODO Auto-generated method stub
		return BASE_TAKE;
	}

	@Override
	//strategia giocatore generoso: aumenta il benessere alle sue relazioni
	public float talk(int k) {/*
	
	
		if (this.hasRelationshipWithEgoista()) 
			return 0;
		else*/
		
		float benesserePreso = this.takePower() * this.takeModifierOfRelationship(k);
		this.addWealth(benesserePreso);
		float benessereDato = this.givePower() * this.giveModifierOfRelationship(k);
		return benessereDato - benesserePreso;
			//return givePower() * this.giveModifierOfRelationship(k);
	}






	




}
