package es.florida.blackJack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class Controlador {
	
	private Vista vista;
    private Modelo modelo;
    
    public Controlador(Vista vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        initEventHandlers();
    }
    
    public void initEventHandlers() {
    	
    	vista.getBtnCargarCartas().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					modelo.cargarImagenes();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
    	});

    	vista.getBtnRegistrarse().addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			// Crear un JPanel personalizado para los campos de usuario y contraseñas
    			JPanel panel = new JPanel(new GridLayout(3, 2));

                // Campos de texto para ingresar el usuario y contraseñas
                JTextField txtUsuario = new JTextField(15);
                JPasswordField pwdContrasenya = new JPasswordField(15);
                JPasswordField pwdRepiteContrasenya = new JPasswordField(15);

                panel.add(new JLabel("Usuario:"));
                panel.add(txtUsuario);
                panel.add(new JLabel("Contraseña:"));
                panel.add(pwdContrasenya);
                panel.add(new JLabel("Repetir Contraseña:"));
                panel.add(pwdRepiteContrasenya);
                
                // Mostrar el JOptionPane con los campos
                int option = JOptionPane.showConfirmDialog(vista, panel, "Registro de Usuario",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                // Si se presiona OK
                if (option == JOptionPane.OK_OPTION) {
                    String usuario = txtUsuario.getText();
                    String contrasenya = new String(pwdContrasenya.getPassword());
                    String repiteContrasenya = new String(pwdRepiteContrasenya.getPassword());

                    try {
						modelo.registrarse(usuario, contrasenya, repiteContrasenya);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                }
    		}
    	});

    	vista.getBtnAcceder().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Crear un JPanel personalizado para los campos de usuario y contraseña
    			JPanel panel = new JPanel(new GridLayout(2, 2));

                JTextField txtUsuario = new JTextField(15);
                JPasswordField pwdContrasenya = new JPasswordField(15);

                panel.add(new JLabel("Usuario:"));
                panel.add(txtUsuario);
                panel.add(new JLabel("Contraseña:"));
                panel.add(pwdContrasenya);
                
                int option = JOptionPane.showConfirmDialog(vista, panel, "Registro de Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (option == JOptionPane.OK_OPTION) {
                    String usuario = txtUsuario.getText();
                    String contrasenya = new String(pwdContrasenya.getPassword());

                    try {
						modelo.acceder(usuario, contrasenya);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
                    
                    if (!modelo.getUsuarioLogueado().equals("")) {
                    	vista.getLblJugador().setText("JUGADOR: " + modelo.getUsuarioLogueado());
                    	vista.getBtnAcceder().setBackground(Color.GREEN);
                    	vista.getBtnAcceder().setEnabled(false);
                    }
                }
			}
		});
    	
    	vista.getCmbEsFr().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
    	
    	vista.getBtnStart().addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
    			
    			limpiarInterfazYValores();
    			
    			try {
    				String[] opciones = {"Crupier (IA)", modelo.getUsuarioLogueado()};
        			int primerTurno = JOptionPane.showOptionDialog(vista, null, "¿Quién empieza?", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, null);
        			
        			if (primerTurno == JOptionPane.CLOSED_OPTION) return;
        			System.out.println("Primer turno: " + primerTurno + ", Opcion: " + opciones[primerTurno]);
        			
        			modelo.setTurno(opciones[primerTurno]);
        			
        			JOptionPane.showMessageDialog(vista, "Empieza: " + modelo.getTurno());
        			
        			String tipoBaraja = (String) vista.getCmbEsFr().getModel().getSelectedItem();
        			modelo.setTipoBarajaAvreviadoString(tipoBaraja);
        			
        			if (tipoBaraja.equals("ES")) modelo.setTipoBaraja("cards_es");
        			else modelo.setTipoBaraja("cards_fr");
        			
        			modelo.barajarCartas();
        			
        			iniciarPartida();
				} catch (Exception e2) {
					e2.printStackTrace();
				}			
    		}
		});
    	
    	vista.getBtnCartaNueva().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
				if (!modelo.isPartidaIniciada()) {
					JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que iniciar partida!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
				}
				pedirCarta();
				// Seguir con la partida
				iniciarPartida();
			}
		});
    	
    	vista.getBtnPlantarse().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
				if (!modelo.isPartidaIniciada()) {
					JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que iniciar partida!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
				}
				modelo.setPlantadoUsuario(true);
				modelo.setTurno("Crupier (IA)");
				JOptionPane.showMessageDialog(vista, modelo.getUsuarioLogueado() + " se ha plantado", "Info", JOptionPane.INFORMATION_MESSAGE);
				if (modelo.isPlantadaIA() && modelo.isPlantadoUsuario()) {
					modelo.setPartidaFinalizada(true);
					if (modelo.getPuntuacionTotalCrupier() == modelo.getPuntuacionTotalUsuario()) {
						JOptionPane.showMessageDialog(vista, "¡El ganador es Crupier (IA)!", "Info", JOptionPane.INFORMATION_MESSAGE);
					}
					if (modelo.getPuntuacionTotalUsuario() > modelo.getPuntuacionTotalCrupier()) {
						JOptionPane.showMessageDialog(vista, "¡El ganador es " + modelo.getUsuarioLogueado() + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				// Seguir con la partida
				iniciarPartida();
			}
		});
    	
    	vista.getBtnGuardar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
				try {
					modelo.guardar();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
    	
    	vista.getBtnHallOfFame().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
				List<String> listaPuntuaciones;
				try {
					listaPuntuaciones = modelo.puntuaciones();

					// Crear un panel con un JTextArea para mostrar las puntuaciones
					JTextArea textArea = new JTextArea();
					textArea.setEditable(false);
					//textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Usar fuente monoespaciada para alinear columnas

					// Agregar el título al JTextArea
					textArea.append("SCORES\n\n");

					// Agregar cada puntuación a la lista
					for (String puntuacion : listaPuntuaciones) {
						textArea.append(puntuacion + "\n");
					}

					// Colocar el JTextArea en un JScrollPane para soporte de scroll
					JScrollPane scrollPane = new JScrollPane(textArea);
					scrollPane.setPreferredSize(new Dimension(300, 400));

					// Mostrar el JScrollPane dentro de un JOptionPane
					JOptionPane.showMessageDialog(null, scrollPane, "21 Blackjack Hall of Fame", JOptionPane.PLAIN_MESSAGE);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
    	
    	vista.getBtnDesconectarse().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!modelo.isUsuarioActivo()) {
    				JOptionPane.showMessageDialog(null, "No puedes hacer esta acción, ¡Tienes que acceder!", "Alerta", JOptionPane.PLAIN_MESSAGE);
    				return;
    			}
				try {
					modelo.desconectar();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				vista.getLblJugador().setText("JUGADOR: ");
				vista.getBtnAcceder().setBackground(new Color(240, 240, 240));
				vista.getBtnAcceder().setEnabled(true);
				
				limpiarInterfazYValores();
			}
		});
    	
    }
    
    private void iniciarPartida() {
    	
    	modelo.setPartidaIniciada(true);
    	while (!modelo.isPartidaFinalizada()) {
    		if (modelo.isPlantadaIA()) {
    			modelo.setTurno(modelo.getUsuarioLogueado());
    		}
    		if (modelo.isPlantadoUsuario()) {
    			modelo.setTurno("Crupier (IA)");
    		}
    		if (modelo.getTurno().equals("Crupier (IA)")) {
    			pedirCarta();
    		} else {
    			return;
    		}
    		
    	}
    	modelo.setPartidaIniciada(false);
    	   	
    }
    
    private void pedirCarta() {
    	try {
			// Cojo el ancho y alto de lblCartaCrupier que es el mismo que el de LblCartaJugador
			Image imagen = modelo.cargarCarta(vista.getLblCartaCrupier().getWidth(), vista.getLblCartaCrupier().getHeight());
			
			// Turno CRUPIER IA
			if (modelo.getTurno().equals("Crupier (IA)")) {
				
				vista.getLblCartaCrupier().setIcon(new ImageIcon(imagen));
				
				
				Carta carta = modelo.getCartasCrupier().getLast();
				if (carta.getPuntos() > 10) carta.setPuntos(10);
				if (carta.getPuntos() == 1) {
					int totalSupuesto = 11 + modelo.getPuntuacionTotalCrupier();
					if (totalSupuesto <= 21) carta.setPuntos(11);
					else carta.setPuntos(1);
				}
				modelo.setPuntuacionTotalCrupier(modelo.getPuntuacionTotalCrupier() + carta.getPuntos());
				modelo.setHistorialCrupier(modelo.getHistorialCrupier() + carta.getPuntos() + " ");
				System.out.println("Puntos crupier: " + modelo.getPuntuacionTotalCrupier());
				
				vista.getLblPuntuacionTotalCrupier().setText("PUNTUACIÓN TOTAL: " + modelo.getPuntuacionTotalCrupier());
				vista.getLblHistorialDePuntuacionesCrupier().setText("Historial de puntuaciones: " + modelo.getHistorialCrupier());
				
				if (modelo.getPuntuacionTotalCrupier() <= 16) {
					modelo.setPlantadaIA(false);
				} else if (modelo.getPuntuacionTotalCrupier() < 21) {
					modelo.setPlantadaIA(true);
					JOptionPane.showMessageDialog(vista, "Crupier (IA) se ha plantado", "Info", JOptionPane.INFORMATION_MESSAGE);
					if (modelo.isPlantadaIA() && modelo.isPlantadoUsuario()) {
						modelo.setPartidaFinalizada(true);
						if (modelo.getPuntuacionTotalCrupier() == modelo.getPuntuacionTotalUsuario()) {
							JOptionPane.showMessageDialog(vista, "¡El ganador es " + modelo.getUsuarioLogueado() + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
						}
						if (modelo.getPuntuacionTotalCrupier() > modelo.getPuntuacionTotalUsuario()) {
							JOptionPane.showMessageDialog(vista, "¡El ganador es Crupier (IA)!", "Info", JOptionPane.INFORMATION_MESSAGE);
						}
					}
					return;
				} else if (modelo.getPuntuacionTotalCrupier() == 21) {
					modelo.setPerdidoUsuario(true);
					modelo.setPartidaFinalizada(true);
					JOptionPane.showMessageDialog(vista, "¡El ganador es Crupier (IA)!", "Info", JOptionPane.INFORMATION_MESSAGE);
					return;
				} else if (modelo.getPuntuacionTotalCrupier() > 21) {
					modelo.setPerdidoIA(true);
					modelo.setPartidaFinalizada(true);
					JOptionPane.showMessageDialog(vista, "¡El ganador es " + modelo.getUsuarioLogueado() + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				modelo.setTurno(modelo.getUsuarioLogueado());
				
				
			
			// Turno USUARIO
			} else {
				
				vista.getLblCartaJugador().setIcon(new ImageIcon(imagen));
				
				Carta carta = modelo.getCartasUsuario().getLast();
				if (carta.getPuntos() > 10) carta.setPuntos(10);
				if (carta.getPuntos() == 1) {
					String[] opciones = {"1", "11"};
	    			int opcion = JOptionPane.showOptionDialog(vista, null, "Elige tus puntos", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opciones, null);
	    			if (opcion == JOptionPane.CLOSED_OPTION) carta.setPuntos(1);
	    			else carta.setPuntos(Integer.parseInt(opciones[opcion]));
	    			System.out.println("Puntos elegidos entre 1 y 11: " + carta.getPuntos());
				}
				modelo.setPuntuacionTotalUsuario(modelo.getPuntuacionTotalUsuario() + carta.getPuntos());
				modelo.setHistorialUsuario(modelo.getHistorialUsuario() + carta.getPuntos() + " ");
				System.out.println("Puntos jugador: " + modelo.getPuntuacionTotalUsuario());
				
				vista.getLblPuntuacionTotalJugador().setText("PUNTUACIÓN TOTAL: " + modelo.getPuntuacionTotalUsuario());
				vista.getLblHistorialDePuntuacionesJugador().setText("Historial de puntuaciones: " + modelo.getHistorialUsuario());
				
				if (modelo.getPuntuacionTotalUsuario() == 21) {
					modelo.setPerdidoIA(true);
					modelo.setPartidaFinalizada(true);
					JOptionPane.showMessageDialog(vista, "¡El ganador es " + modelo.getUsuarioLogueado() + "!", "Info", JOptionPane.INFORMATION_MESSAGE);
					return;
				} else if (modelo.getPuntuacionTotalUsuario() > 21) {
					modelo.setPerdidoUsuario(true);
					modelo.setPartidaFinalizada(true);
					JOptionPane.showMessageDialog(vista, "¡El ganador es Crupier (IA)!", "Info", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if (!modelo.isPlantadaIA()) {
					modelo.setTurno("Crupier (IA)");
				}
			}
			
			JOptionPane.showMessageDialog(vista, "Turno de " + modelo.getTurno(), "Info", JOptionPane.INFORMATION_MESSAGE);

			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
    
    private void limpiarInterfazYValores() {
    	modelo.setBarajaCartas(new ArrayList<Carta>());
    	modelo.setCartasCrupier(new ArrayList<Carta>());
    	modelo.setCartasUsuario(new ArrayList<Carta>());
    	modelo.setHistorialCrupier("");
    	modelo.setHistorialUsuario("");
    	modelo.setPartidaFinalizada(false);
    	modelo.setPartidaIniciada(false);
    	modelo.setPerdidoIA(false);
    	modelo.setPerdidoUsuario(false);
    	modelo.setPlantadaIA(false);
    	modelo.setPlantadoUsuario(false);
    	modelo.setPuntuacionTotalCrupier(0);
    	modelo.setPuntuacionTotalUsuario(0);
    	vista.getLblCartaCrupier().setIcon(null);
    	vista.getLblCartaJugador().setIcon(null);
    	vista.getLblHistorialDePuntuacionesCrupier().setText("Historial de puntuaciones: ");
    	vista.getLblHistorialDePuntuacionesJugador().setText("Historial de puntuaciones: ");
    	vista.getLblPuntuacionTotalCrupier().setText("PUNTUACIÓN TOTAL: ");
    	vista.getLblPuntuacionTotalJugador().setText("PUNTUACIÓN TOTAL: ");
    }

}
