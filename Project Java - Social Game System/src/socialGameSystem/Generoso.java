package socialGameSystem;
import java.awt.Color;

public class Generoso extends Giocatore {
	private final static Color COLORE = Color.GREEN;

	// parametri della strategia
	private final static float BASE_GIVE = 3; // quando benessere puo' dare alle relazioni ogni messaggio
	private final static float BASE_TAKE = 0; // quando benessere puo' togliere dalle relazioni ogni messaggio


	public Generoso() {
		super(TipoGiocatore.generoso);
	}

	@Override
	public float givePower() {
		if (this.hasRelationshipWithEgoista())
			return 0;
		else
			return BASE_GIVE;
	}

	@Override
	public float takePower() {
		// TODO Auto-generated method stub
		return BASE_TAKE;
	}

	@Override
	public void talk(int k) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessages() {
		// TODO Auto-generated method stub
		
	}
	
	public Color colore() {
		return COLORE;
	}

	@Override
	public void setPosition(int _x, int _y) {
		this.x = _x;
		this.y = _y;
		// TODO Auto-generated method stub
		
	}

}
