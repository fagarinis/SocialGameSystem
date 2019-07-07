package interfacciaGraficaSGS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import giocatori.TipoGiocatore;
import socialGameSystem.Direzione;
import socialGameSystem.Giocatore;
import socialGameSystem.Parameters;
import socialGameSystem.RelationalBoard;
/**
 * Questa classe specifica il comportamento di ogni singola cella grafica nella matrice
 *
 */
public class CellaMatrice extends JLabel{
	private static final long serialVersionUID = 1L;
	
	
	private static CellaMatrice[] celle = null; //contiene tutte le celle create
	private static int top = 0; //punta alla prossima posizione vuota dell'array celle
	
	private int index; // e' l'indice di posizionamento della cella all'intero dell'array celle
	private JLabel label;
	private final static Color defaultCellBackground = Color.GRAY;
	private Giocatore giocatore;
	
	private int riga;
	private int colonna;
	
	
	/**
	 * Costruttore
	 */
	CellaMatrice(){
		this.index = top;
		this.riga = top/ GraphicsSGS.colonne;
		this.colonna = top % GraphicsSGS.colonne;
		
		this.label = new JLabel("", SwingConstants.CENTER);
		this.updateFontSize();
		
		if (celle == null) {
			celle = new CellaMatrice[GraphicsSGS.numeroCelle];
		}
		celle[top++] = this;
		
	    label.setBackground(defaultCellBackground);
	    label.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), null));
	    label.setOpaque(true);
	    
	    //aggiorna il tooltip

	    
	    
	    label.addMouseListener(new MouseAdapter() {
	    	
	    	/**
	    	 * Illumina il bordo e aggiorna il tooltip quando il mouse va sopra la cella
	    	 */
			@Override
			public void mouseEntered(MouseEvent e) {
				label.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED, null, Color.LIGHT_GRAY), null));
				if (label.getBackground() != defaultCellBackground)label.setToolTipText("<html>"+
								"Strategy: "+String.valueOf(getGiocatore().strategy()) + 
								"<br>Direction: "+String.valueOf(getGiocatore().direction()) + 
								"<br>Wealth: "+ (int) getGiocatore().benessere()+"/"+ (int)Parameters.MAX_WEALTH +
								"</html>");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				label.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), null));
				label.setToolTipText("");
			}
			
			/**
			 * Click sulla cella
			 */
			@Override
			public void mousePressed(MouseEvent arg0) {
				 // Pressione tasto sinistro -> crea nuovo giocatore
				if (arg0.getButton() == MouseEvent.BUTTON1){			
					//se nella cella NON c'è un giocatore, aggiungilo
					if (label.getBackground() == defaultCellBackground /*&& GraphicsSGS.chckbxAddremoveWealthOn.isSelected()*/) {
					TipoGiocatore tipoPlayer = GraphicsSGS.RadioButtonGeneroso.isSelected()? TipoGiocatore.generoso: GraphicsSGS.RadioButtonMediatore.isSelected()? TipoGiocatore.mediatore: TipoGiocatore.egoista;
					Direzione direzione = GraphicsSGS.radioButtonUp.isSelected()? Direzione.up: GraphicsSGS.radioButtonLeft.isSelected()? Direzione.left: GraphicsSGS.radioButtonRight.isSelected()? Direzione.right: Direzione.down;
					RelationalBoard.addNewPlayer(getRiga(), getColonna(), tipoPlayer,direzione);
					}
				
				}
			}
			
		});
	}
	
	public JLabel getLabel() {
		return this.label;
	}
	
	public int labelPosition() {
		return this.index;
	}
	
	public int getRiga() {
		return this.riga;
	}
	
	public int getColonna() {
		return this.colonna;
	}
	
	public Giocatore getGiocatore() {
		return RelationalBoard.getPlayer(this.riga, this.colonna);
	}

	
	public static CellaMatrice getCella(int riga, int colonna) {
		return celle[riga*GraphicsSGS.colonne + colonna];
	}
	
	public void resetLabelBackground() {
		label.setBackground(defaultCellBackground);
	}
	
	/**
	 * Rende la cella vuota e disponibile per essere nuovamente 
	 * disponibile l'inserimento di un nuovo altro giocatore.
	 */
	public void makeCellEmpty() {
		this.resetLabelBackground();
		this.setPlayer(null);
		this.setDirectionImage(null);	
	}
	
	
	/**
	 * @param _giocatore Attenzione, il metodo puo' anche ricevere null come giocatore per svuotare la cella
	 */
	public void setPlayer(Giocatore _giocatore) {
		this.giocatore = _giocatore;
	}
	
	@Override
	public void paintComponents(Graphics g) {
		if (g!= null) g.drawRect(0, 5, label.getWidth()/2, label.getHeight()/2);
		
	}
	public void setDirectionImage(Direzione direzione) {
		if (direzione == Direzione.up) this.label.setText("^");
		else if (direzione == Direzione.right) this.label.setText(">");
		else if (direzione == Direzione.down) this.label.setText("v");
		else if (direzione == Direzione.left) this.label.setText("<");
		
		else this.label.setText("");
		
		//this.label.setHorizontalAlignment(5);
		//label.getGraphics().drawRect(0, 5, label.getWidth()/2, label.getHeight());
		
	}
	
	private void updateFontSize() {
		Font labelFont = label.getFont();
		String labelText = label.getText();

		int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
		int componentWidth = (GraphicsSGS.frmSocialGameSystem.getHeight()) / GraphicsSGS.righe;


		double widthRatio = (double)componentWidth / (double)stringWidth;

		int newFontSize = (int)(labelFont.getSize() * widthRatio);
		int componentHeight = componentWidth;

		int fontSizeToUse = (int) (Math.min(newFontSize, componentHeight)*0.7);
		
		this.label.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
	}
	
	public static void updateAllFontSize() {
		for(CellaMatrice cella: CellaMatrice.celle) {
			cella.updateFontSize();
		}
	}
	
}
