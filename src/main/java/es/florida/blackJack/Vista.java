package es.florida.blackJack;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

public class Vista extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	JButton btnCargarCartas;
	JButton btnRegistrarse;
	JButton btnAcceder;
	JButton btnStart;
	JComboBox cmbEsFr;
	JButton btnGuardar;
	JButton btnHallOfFame;
	JButton btnDesconectarse;
	JButton btnCartaNueva;
	JButton btnPlantarse;
	JLabel lblCartaCrupier;
	JLabel lblCartaJugador;
	JLabel lblJugador;
	JLabel lblPuntuacionTotalCrupier;
	JLabel lblPuntuacionTotalJugador;
	JLabel lblHistorialDePuntuacionesCrupier;
	JLabel lblHistorialDePuntuacionesJugador;

	public Vista() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1020, 679);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 185, 0));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnCargarCartas = new JButton("Cargar cartas");
		btnCargarCartas.setBounds(10, 11, 114, 23);
		contentPane.add(btnCargarCartas);
		
		btnRegistrarse = new JButton("Registrarse");
		btnRegistrarse.setBounds(134, 11, 102, 23);
		contentPane.add(btnRegistrarse);
		
		btnAcceder = new JButton("Acceder");
		btnAcceder.setBounds(246, 11, 89, 23);
		contentPane.add(btnAcceder);
		
		btnStart = new JButton("Start");
		btnStart.setBounds(514, 11, 89, 23);
		contentPane.add(btnStart);
		
		cmbEsFr = new JComboBox();
		cmbEsFr.setModel(new DefaultComboBoxModel(new String[] {"ES", "FR"}));
		cmbEsFr.setBounds(458, 11, 46, 22);
		contentPane.add(cmbEsFr);
		
		btnGuardar = new JButton("Guardar");
		btnGuardar.setBounds(613, 11, 89, 23);
		contentPane.add(btnGuardar);
		
		btnHallOfFame = new JButton("Hall Of Fame");
		btnHallOfFame.setBounds(738, 11, 117, 23);
		contentPane.add(btnHallOfFame);
		
		btnDesconectarse = new JButton("Desconectarse");
		btnDesconectarse.setBounds(865, 11, 129, 23);
		contentPane.add(btnDesconectarse);
		
		btnCartaNueva = new JButton("Carta nueva");
		btnCartaNueva.setBounds(738, 606, 117, 23);
		contentPane.add(btnCartaNueva);
		
		btnPlantarse = new JButton("Plantarse");
		btnPlantarse.setBounds(865, 606, 89, 23);
		contentPane.add(btnPlantarse);
		
		JLabel lblPalo = new JLabel("Palo de Carta:");
		lblPalo.setBounds(369, 15, 79, 14);
		contentPane.add(lblPalo);
		
		JLabel lblCrupier = new JLabel("CRUPIER");
		lblCrupier.setBounds(28, 45, 65, 14);
		contentPane.add(lblCrupier);
		
		lblJugador = new JLabel("");
		lblJugador.setBounds(613, 45, 289, 14);
		contentPane.add(lblJugador);
		lblJugador.setText("JUGADOR: ");
		
		lblPuntuacionTotalCrupier = new JLabel("PUNTUACIÓN TOTAL:");
		lblPuntuacionTotalCrupier.setBounds(28, 548, 307, 14);
		contentPane.add(lblPuntuacionTotalCrupier);
		
		lblPuntuacionTotalJugador = new JLabel("PUNTUACIÓN TOTAL:");
		lblPuntuacionTotalJugador.setBounds(613, 548, 306, 14);
		contentPane.add(lblPuntuacionTotalJugador);
		
		lblHistorialDePuntuacionesCrupier = new JLabel("Historial de puntuaciones:");
		lblHistorialDePuntuacionesCrupier.setBounds(28, 575, 307, 14);
		contentPane.add(lblHistorialDePuntuacionesCrupier);
		
		lblHistorialDePuntuacionesJugador = new JLabel("Historial de puntuaciones:");
		lblHistorialDePuntuacionesJugador.setBounds(613, 575, 306, 14);
		contentPane.add(lblHistorialDePuntuacionesJugador);
		
		lblCartaCrupier = new JLabel();
		lblCartaCrupier.setOpaque(true);
		lblCartaCrupier.setBackground(new Color(0, 128, 0));
		lblCartaCrupier.setBounds(28, 67, 307, 470);
		contentPane.add(lblCartaCrupier);
		//ImageIcon imageIcon = new ImageIcon(getClass().getResource("/img/cards_es/bastos_01.jpg"));
		//ImageIcon redimensionadoIcon = new ImageIcon( imageIcon.getImage().getScaledInstance( lblCartaCrupier.getWidth(), lblCartaCrupier.getHeight(), Image.SCALE_SMOOTH ) );
		//lblCartaCrupier.setIcon(redimensionadoIcon);
		
		lblCartaJugador = new JLabel();
		lblCartaJugador.setOpaque(true);
		lblCartaJugador.setBackground(new Color(0, 128, 0));
		lblCartaJugador.setBounds(612, 67, 307, 470);
		contentPane.add(lblCartaJugador);
		//ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/cards_fr/clubs_01.png"));
		//ImageIcon redimensionadoIcon2 = new ImageIcon( originalIcon.getImage().getScaledInstance( lblCartaJugador.getWidth(), lblCartaJugador.getHeight(), Image.SCALE_SMOOTH ) );
		//lblCartaJugador.setIcon(redimensionadoIcon2);
		
		setLocationRelativeTo(null);  // Centrar la ventana principal
		
		setVisible(true);
	}

	public JButton getBtnCargarCartas() {
		return btnCargarCartas;
	}

	public JButton getBtnRegistrarse() {
		return btnRegistrarse;
	}

	public JButton getBtnAcceder() {
		return btnAcceder;
	}

	public JButton getBtnStart() {
		return btnStart;
	}

	public JComboBox<String> getCmbEsFr() {
		return cmbEsFr;
	}

	public JButton getBtnGuardar() {
		return btnGuardar;
	}

	public JButton getBtnHallOfFame() {
		return btnHallOfFame;
	}

	public JButton getBtnDesconectarse() {
		return btnDesconectarse;
	}

	public JButton getBtnCartaNueva() {
		return btnCartaNueva;
	}

	public JButton getBtnPlantarse() {
		return btnPlantarse;
	}
	
	public JLabel getLblCartaCrupier() {
		return lblCartaCrupier;
	}

	public JLabel getLblCartaJugador() {
		return lblCartaJugador;
	}

	public JLabel getLblJugador() {
		return lblJugador;
	}

	public JLabel getLblPuntuacionTotalCrupier() {
		return lblPuntuacionTotalCrupier;
	}

	public JLabel getLblPuntuacionTotalJugador() {
		return lblPuntuacionTotalJugador;
	}

	public JLabel getLblHistorialDePuntuacionesCrupier() {
		return lblHistorialDePuntuacionesCrupier;
	}

	public JLabel getLblHistorialDePuntuacionesJugador() {
		return lblHistorialDePuntuacionesJugador;
	}
	
	
	
}
