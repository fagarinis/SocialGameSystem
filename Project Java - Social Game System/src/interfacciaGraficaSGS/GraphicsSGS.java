package interfacciaGraficaSGS;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import socialGameSystem.Direzione;
import socialGameSystem.Giocatore;
import socialGameSystem.Parameters;
import socialGameSystem.RelationalBoard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.Window.Type;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;
import javax.swing.event.ChangeListener;

import giocatori.TipoGiocatore;

import javax.swing.event.ChangeEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class GraphicsSGS{

	public static JFrame frmSocialGameSystem;
	public static JTextField textField_turn;
	public static JTextField textField_alivePlayers;

	public final static int righe						= Parameters.righe; //RIGHE DELLA MATRICE QUADRATA
	public final static int colonne						= righe;
	public final static int numeroCelle 				= righe * colonne;
	private final static int RANDOM_PLAYERS_GENERATED 	= (int) numeroCelle/5;
	private int latoCella;
	
	public static int 	TURN_SPEED 	= 25; 			//quanto velocemenente avanzano i turni
	public static Timer timer 		= new Timer();
	
	public static JLabel[] celle 	= new JLabel[numeroCelle];
	
	protected JPanel matrix 		= new JPanel();
	protected static Component contenitoreMatrice;
	
	private static boolean 			wasRunning 	= false;
	public 	static RelationalBoard 	board 		= new RelationalBoard(righe, colonne);
	
	private final static Color defaultCellBackground = Color.GRAY;
	
	static final int changedWealthOnClick = 3;
	public 	static 	JButton 	stepButton;
	public 	static 	JButton 	StartStopButton;
	private static 	JTextField 	speedTextField;
	private static	JSlider 	speedSlider;

	
	public static ButtonGroup G; // il gruppo dei bottoni del tipo giocatore (solo uno puo' essere selezionato)
	public static JRadioButton RadioButtonGeneroso;
	public static JRadioButton RadioButtonMediatore;
	public static JRadioButton RadioButtonEgoista;
	
	public static ButtonGroup D; // il gruppo dei bottoni della direzione (solo uno puo' essere selezionato)
	public static JRadioButton radioButtonUp;
	public static JRadioButton radioButtonRight;
	public static JRadioButton radioButtonLeft;
	public static JRadioButton radioButtonDown;
	
	public static int getSpeed() {
		return speedSlider.getMaximum()+1 -speedSlider.getValue();
	}
	
	/**
	 * Create the application.
	 */
	public GraphicsSGS() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmSocialGameSystem = new JFrame();
		frmSocialGameSystem.setMinimumSize(new Dimension(1400, 485));
		
		frmSocialGameSystem.setIconImage(Toolkit.getDefaultToolkit().getImage(GraphicsSGS.class.getResource("/com/sun/java/swing/plaf/motif/icons/DesktopIcon.gif")));
		frmSocialGameSystem.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		frmSocialGameSystem.setTitle("Social Game System");
		frmSocialGameSystem.setBounds(50, 50, 1600, 850);
		frmSocialGameSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	
		frmSocialGameSystem.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel menu = new JPanel();
		frmSocialGameSystem.getContentPane().add(menu, BorderLayout.SOUTH);
		menu.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_1 = new JPanel();
		menu.add(panel_1, BorderLayout.WEST);
		
		StartStopButton = new JButton("Start");
		StartStopButton.setPreferredSize(new Dimension(70, 26));
		StartStopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(StartStopButton.getText() == "Start") {//START BUTTON RUN
					RelationalBoard.startRun();
					
				}
				
				
				else if(StartStopButton.getText() == "Stop") {
					while(RelationalBoard.isRunning()) {
						RelationalBoard.stopRun();
					}
					
					}
				
				
			}
		});
		StartStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				
			}
		});
		panel_1.add(StartStopButton);
		
		stepButton = new JButton("Step");
		stepButton.setToolTipText("avanti di un turno");
		stepButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RelationalBoard.step();
			}
		});
		panel_1.add(stepButton);
		
		JButton btnClear = new JButton("Reset");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RelationalBoard.resetBoard();
			}
		});
		panel_1.add(btnClear);
		
		JLabel lblTurn = new JLabel("Turn:");
		panel_1.add(lblTurn);
		
		textField_turn = new JTextField();
		textField_turn.setEditable(false);
		panel_1.add(textField_turn);
		textField_turn.setColumns(3);
		
		JLabel lblAlivePlayers = new JLabel("Alive Players:");
		panel_1.add(lblAlivePlayers);
		
		textField_alivePlayers = new JTextField();
		textField_alivePlayers.setEditable(false);
		textField_alivePlayers.setText("0");
		textField_alivePlayers.setColumns(3);
		panel_1.add(textField_alivePlayers);
		
		JLabel lblSpeed = new JLabel("Speed:");
		panel_1.add(lblSpeed);
		
		speedTextField = new JTextField();
		speedTextField.setEditable(false);
		panel_1.add(speedTextField);
		speedTextField.setColumns(3);
		
		speedSlider = new JSlider();
		
		speedSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(wasRunning) RelationalBoard.startRun();
				wasRunning = false;
			}
		});
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				
				speedTextField.setText(String.valueOf(speedSlider.getValue()));
				if(RelationalBoard.isRunning()) {
					while(RelationalBoard.isRunning()) RelationalBoard.stopRun();
					wasRunning = true;
				}
				
			}
		});
		speedSlider.setValue(250);
		speedSlider.setMaximum(400);
		speedSlider.setMinimum(1);
		panel_1.add(speedSlider);
		
		JPanel panel_2 = new JPanel();
		menu.add(panel_2, BorderLayout.EAST);
		
		JButton btnGenrandomplayers = new JButton("GenRandomPlayers");
		btnGenrandomplayers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RelationalBoard.addRandomPlayers(RANDOM_PLAYERS_GENERATED);
			}
		});
		panel_2.add(btnGenrandomplayers);
		
		JPanel panel_3 = new JPanel();
		menu.add(panel_3, BorderLayout.CENTER);
		
		
		
		JPanel contenitoreMatrice = new JPanel();
	
		contenitoreMatrice.setBackground(Color.DARK_GRAY);
		frmSocialGameSystem.getContentPane().add(contenitoreMatrice, BorderLayout.CENTER);
		
		contenitoreMatrice.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	
		//crea la griglia
		JPanel matrix = new JPanel();
		matrix.setLayout(new GridLayout(righe, colonne));
		latoCella = frmSocialGameSystem.getHeight();
		matrix.setPreferredSize(new Dimension(latoCella-85, latoCella-85));
		
		contenitoreMatrice.add(matrix);
		
		JPanel panel = new JPanel();
		frmSocialGameSystem.getContentPane().add(panel, BorderLayout.EAST);
		panel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel label_3 = new JLabel("");
		panel.add(label_3);
		
		JLabel label = new JLabel("");
		panel.add(label);
		
		JLabel label_1 = new JLabel("");
		panel.add(label_1);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setToolTipText("");
		toolBar.setName("");
		toolBar.setOrientation(SwingConstants.VERTICAL);
		toolBar.setFloatable(false);
		panel.add(toolBar);
		
		JLabel lblNewPlayer = new JLabel("New Player:");
		toolBar.add(lblNewPlayer);
		lblNewPlayer.setVerticalAlignment(SwingConstants.BOTTOM);
		
		RadioButtonGeneroso = new JRadioButton(TipoGiocatore.generoso.toString());
		RadioButtonGeneroso.setSelected(true);
		RadioButtonGeneroso.setActionCommand(TipoGiocatore.generoso.toString());
		toolBar.add(RadioButtonGeneroso);
		
		RadioButtonMediatore = new JRadioButton(TipoGiocatore.mediatore.toString());
		RadioButtonMediatore.setActionCommand(TipoGiocatore.mediatore.toString());
		toolBar.add(RadioButtonMediatore);
		
		RadioButtonEgoista = new JRadioButton(TipoGiocatore.egoista.toString());
		RadioButtonEgoista.setActionCommand(TipoGiocatore.egoista.toString());
		toolBar.add(RadioButtonEgoista);
		
		ButtonGroup G = new ButtonGroup();
		G.add(RadioButtonGeneroso);
		G.add(RadioButtonMediatore);
		G.add(RadioButtonEgoista);
		
		JToolBar toolBar_1 = new JToolBar();
		toolBar_1.setOrientation(SwingConstants.VERTICAL);
		panel.add(toolBar_1);
		
		JLabel lblDirection = new JLabel("Direction:");
		toolBar_1.add(lblDirection);
		lblDirection.setVerticalAlignment(SwingConstants.BOTTOM);
		
		radioButtonUp = new JRadioButton(Direzione.up.toString());
		radioButtonUp.setSelected(true);
		toolBar_1.add(radioButtonUp);
		
		radioButtonRight = new JRadioButton(Direzione.right.toString());
		toolBar_1.add(radioButtonRight);
		
		radioButtonLeft = new JRadioButton(Direzione.left.toString());
		toolBar_1.add(radioButtonLeft);
		
		radioButtonDown = new JRadioButton(Direzione.down.toString());
		toolBar_1.add(radioButtonDown);
	
		
		ButtonGroup D = new ButtonGroup();
		D.add(radioButtonUp);
		D.add(radioButtonRight);
		D.add(radioButtonLeft);
		D.add(radioButtonDown);
		
		frmSocialGameSystem.addComponentListener(new ComponentAdapter() {
			@Override
			//il contenitore della matrice deve cambiare dimensione on-resize
			public void componentResized(ComponentEvent e) { 
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						latoCella = Math.min(contenitoreMatrice.getHeight(), frmSocialGameSystem.getWidth());
						matrix.setPreferredSize(new Dimension(latoCella-5, latoCella-5));
						CellaMatrice.updateAllFontSize();
					}
					
				}).start();
				
			}
		});
		
				
		//inserisci tutte le celle
			for (int i = 0; i < celle.length; i++) {
				 CellaMatrice cella = new CellaMatrice();
				 matrix.add(cella.getLabel());
				 celle[i] = cella.getLabel();
				}
		
		
		//fine colora matrice
				
		
		
	
	}
	
	
	
	/**
	 * @return La grandezza grafica della singola cella sullo schermo
	 */
	public static int getCellaWidth(){
		return Math.min(contenitoreMatrice.getHeight(), frmSocialGameSystem.getWidth()) / GraphicsSGS.righe;
		
	}
	/**
	 * resetta lo sfondo del quadrato
	 * @param label
	 */
	public static void resetLabelBackground(JLabel label) {
		label.setBackground(defaultCellBackground);
	}
	

	
	//returna il JLabel associato alla cella di coordinate x e y
	public JLabel getCella(int x, int y) {
		return GraphicsSGS.celle[x*colonne + y];
	}
	
	public void colora(int x, int y, Color colore) {
		getCella(x, y).setBackground(colore);
	}
	

	
}
