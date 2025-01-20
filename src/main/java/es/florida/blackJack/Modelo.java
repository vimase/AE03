package es.florida.blackJack;

import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.apache.commons.codec.binary.Base64;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Filters.*;

public class Modelo {
	
	String pathImagenes = "src/main/resources/img/";
	
	private MongoClient mongoClient;
	private MongoDatabase database;
	private boolean usuarioActivo = false;
	private String usuarioLogueado = "";
	private String turno;
	private String tipoBaraja;
	private String tipoBarajaAvreviadoString;
	private int puntuacionTotalCrupier = 0;
	private int puntuacionTotalUsuario = 0;
	private String historialCrupier = "";
	private String historialUsuario = "";
	private boolean plantadaIA = false;
	private boolean plantadoUsuario = false;
	private boolean perdidoIA = false;
	private boolean perdidoUsuario = false;
	private boolean partidaFinalizada = false;
	private boolean partidaIniciada = false;

	private List<Carta> barajaCartas;
	private List<Carta> cartasCrupier = new ArrayList<Carta>();
	private List<Carta> cartasUsuario = new ArrayList<Carta>();

	public Modelo() throws IOException {
        
	}
	
	/**
     * Conecta a la base de datos MongoDB.
     * Lee la configuración desde un archivo JSON local o remoto según la necesidad.
     * 
     * @throws IOException Si ocurre un error al leer el archivo de configuración o al conectar a la base de datos.
     */
	private void conectarMongoBD() throws IOException {
		// No mostrar logs de MongoDB
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); // e.g. or Log.WARNING, etc.

		// En local
//		String content = new String(Files.readAllBytes(Paths.get("src/main/resources/config_local.json")));
//		JSONObject config = new JSONObject(content);
//		MongoClientURI uri = new MongoClientURI("mongodb://" + config.get("usuario") + ":" + config.get("contrasenya") + "@" + config.get("ip") + ":" + config.get("puerto") + "/");
		//MongoClientURI uri = new MongoClientURI(config.getString("cadenaConexion"));
		
		// En remoto
		String content = new String(Files.readAllBytes(Paths.get("src/main/resources/config_remoto.json")));
		JSONObject config = new JSONObject(content);
		MongoClientURI uri = new MongoClientURI("mongodb+srv://" + config.get("usuario") + ":" + config.get("contrasenya") + "@" + config.get("cluster") + config.get("configuracion"));
		//MongoClientURI uri = new MongoClientURI(config.getString("cadenaConexion"));

		mongoClient = new MongoClient(uri);
		database = mongoClient.getDatabase("casino");
	}
	
	/**
     * Cierra la conexión a MongoDB si está activa.
     */
	public void cerrarConexion() {
		if (mongoClient != null) {
			mongoClient.close();  // Cerrar la conexión
		}
	}
	
	/**
     * Carga imágenes en la base de datos.
     * Borra las colecciones existentes y las recrea con las imágenes nuevas.
     * 
     * @throws IOException Si ocurre un error al leer las imágenes o al interactuar con la base de datos.
     */
	public void cargarImagenes() throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
		borrarColeccion("cards_es");
		borrarColeccion("cards_fr");
		
		generarColeccion("cards_es");
		generarColeccion("cards_fr");
		
		JOptionPane.showMessageDialog(null, "Las imágenes se han cargado correctamente", "INFO", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
     * Borra una colección específica en MongoDB.
     * 
     * @param coleccion Nombre de la colección a borrar.
     */
	private void borrarColeccion(String coleccion) {
		MongoCollection<Document> col = database.getCollection(coleccion);
		col.drop();
	}
	
	/**
     * Genera una colección en MongoDB con imágenes en base64.
     * 
     * @param coleccion Nombre de la colección a generar.
     * @throws IOException Si ocurre un error al leer las imágenes.
     */
	private void generarColeccion(String coleccion) throws IOException {
		MongoCollection<Document> col = database.getCollection(coleccion);
		List<Document> docs = new ArrayList<Document>();
		
		File dir = new File(pathImagenes + coleccion);
		File[] files = dir.listFiles();
		for (File file : files) {
			String nombre = file.getName();
			String[] nombreSplit = nombre.split("_");
        	String palo = nombreSplit[0];
        	int num = Integer.parseInt(nombreSplit[1].split("\\.")[0]);
        	String imagenBase64 = convertirImagenABase64(file.getPath());
			
        	Document doc = new Document();
    		doc.append("suit", palo);
    		doc.append("points", num);
    		doc.append("base64", imagenBase64);
    		docs.add(doc);
        }
		col.insertMany(docs);
	}
	
	/**
     * Convierte una imagen en un archivo a una cadena en base64.
     * 
     * @param archivo Ruta del archivo de imagen.
     * @return Cadena de texto en formato base64.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
	private String convertirImagenABase64(String archivo) throws IOException {
		File fichero = new File(archivo);
		byte[] fileContent = Files.readAllBytes(fichero.toPath());
		String encodedString = Base64.encodeBase64String(fileContent);
		return encodedString;
	}
	
	/**
     * Registra un nuevo usuario en la base de datos.
     * 
     * @param user        Nombre del usuario.
     * @param pass        Contraseña del usuario.
     * @param passConfirm Confirmación de la contraseña.
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public void registrarse(String user, String pass, String passConfirm) throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
		if (user.isEmpty() || pass.isEmpty() || passConfirm.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No se admiten campos vacíos", "Alerta", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		try {
			// Comprobar si el usuario ya existe en la base de datos
			MongoCollection<Document> coleccion = database.getCollection("users");
			Bson query = eq("user", user);
			MongoCursor<Document> cursor = coleccion.find(query).iterator();
			if (cursor.hasNext()) {
				JOptionPane.showMessageDialog(null, "Ya existe el usuario", "Alerta", JOptionPane.PLAIN_MESSAGE);
				return;
			}

			if (pass.equals(passConfirm)) {
				Document doc = new Document();
				doc.append("user", user);
				doc.append("pass", convertirAHashSHA256(pass));
				coleccion.insertOne(doc);
				
				JOptionPane.showMessageDialog(null, "Usuario registrado: " + user, "Alerta", JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(null, "Las contraseñas no coinciden", "Alerta", JOptionPane.WARNING_MESSAGE);
			}
			
		} catch (Exception e) { 
			System.out.println(e.getMessage() + "\n" + e);
		}
	}
	
	/**
     * Permite a un usuario acceder si sus credenciales son correctas.
     * 
     * @param user Nombre del usuario.
     * @param pass Contraseña del usuario.
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public void acceder(String user, String pass) throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
		if (user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No se admiten campos vacíos", "Alerta", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		MongoCollection<Document> coleccion = database.getCollection("users");
		Bson query = eq("user", user);
		MongoCursor<Document> cursor = coleccion.find(query).iterator();
		if (!cursor.hasNext()) {
			JOptionPane.showMessageDialog(null, "No existe el usuario", "Alerta", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		query = and(eq("user", user), eq("pass", convertirAHashSHA256(pass)));
		cursor = coleccion.find(query).iterator();
		if (!cursor.hasNext()) {
			JOptionPane.showMessageDialog(null, "Contraseña incorrecta", "Alerta", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, "Bienvenido: " + user, "Alerta", JOptionPane.PLAIN_MESSAGE);
		usuarioActivo = true;
		usuarioLogueado = user;
	}
	
	/**
     * Convierte un texto en un hash SHA-256.
     * 
     * @param texto Texto a convertir.
     * @return Cadena en formato hash SHA-256.
     */
	public static String convertirAHashSHA256(String texto) {
        try {
            // Crear una instancia de MessageDigest con el algoritmo SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Obtener el hash como un array de bytes
            byte[] hashBytes = digest.digest(texto.getBytes());
            // Convertir el array de bytes en un String hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Convertir cada byte en un valor hexadecimal de 2 dígitos
                hexString.append(String.format("%02x", b));
            }
            // Devolver el hash como un String hexadecimal
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/**
     * Baraja las cartas disponibles para la partida.
     * 
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public void barajarCartas() throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
		// Recoger los campos que no son el de base64 para luego hacer la gestión del juego
		MongoCollection<Document> cartas = database.getCollection(tipoBaraja);
		// Proyección para excluir el campo "_id" y "base64"
		Bson projection = Projections.exclude("_id", "base64");

		// Consulta para obtener todos los documentos, excluyendo "_id" y "base64"
		MongoCursor<Document> cursor = cartas.find().projection(projection).iterator();

		barajaCartas = new ArrayList<Carta>();
		while (cursor.hasNext()) {
			JSONObject obj = new JSONObject(cursor.next().toJson());
			barajaCartas.add(new Carta(obj.getString("suit"), obj.getInt("points")));
		}

		// Desordenamos las cartas aleatoriamente (barajamos)
		Collections.shuffle(barajaCartas);
		
	}
	
	/**
     * Carga una carta desde la base de datos y la convierte en una imagen escalada.
     * 
     * @param ancho Ancho deseado para la imagen.
     * @param alto  Alto deseado para la imagen.
     * @return Objeto Image escalado.
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public Image cargarCarta(int ancho, int alto) throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
		Carta carta = barajaCartas.removeFirst();
		//System.out.println(barajaCartas.toString());
		
		if (turno.equals("Crupier (IA)")) cartasCrupier.add(carta);
		else cartasUsuario.add(carta);
		System.out.println("Cartas CrupierIA: " + cartasCrupier.toString() + "\nCartas Usuario: " + cartasUsuario.toString());
		
		MongoCollection<Document> cartas = database.getCollection(tipoBaraja);
		Bson projection = Projections.exclude("_id", "suit", "points");
		
		Bson query = and(eq("suit", carta.getPalo()), eq("points", carta.getPuntos()));
		MongoCursor<Document> cursor = cartas.find(query).projection(projection).iterator();
		
		JSONObject obj = new JSONObject(cursor.next().toJson());
		String imagenBase64 = obj.getString("base64");
		
		return convertirBase64AImagen(imagenBase64, ancho, alto);
		
	}
	
	/**
     * Convierte una cadena en base64 a una imagen escalada.
     * 
     * @param base64String Cadena en base64.
     * @param ancho        Ancho deseado para la imagen.
     * @param alto         Alto deseado para la imagen.
     * @return Imagen escalada.
     * @throws IOException Si ocurre un error al procesar la imagen.
     */
	public Image convertirBase64AImagen(String base64String, int ancho, int alto) throws IOException {
		byte[] btDataFile = Base64.decodeBase64(base64String);
		BufferedImage imagen = ImageIO.read(new ByteArrayInputStream(btDataFile));
		Image imagenEscalada = imagen.getScaledInstance( ancho, alto, Image.SCALE_SMOOTH);
		return imagenEscalada;
	}
	
	/**
     * Guarda la puntuación de la partida actual en la base de datos.
     * 
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public void guardar() throws IOException {
		if (mongoClient == null) conectarMongoBD();
		
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecha = sdf.format(new Date());
        
        MongoCollection<Document> col = database.getCollection("scores");
		Document doc = new Document();
		doc.append("user", usuarioLogueado);
		doc.append("suit", tipoBarajaAvreviadoString);
		doc.append("points", puntuacionTotalUsuario);
		doc.append("timestamp", fecha);
		col.insertOne(doc);

	}
	
	/**
     * Obtiene una lista de puntuaciones almacenadas en la base de datos.
     * Las puntuaciones están ordenadas de mayor a menor.
     * 
     * @return Lista de puntuaciones en formato String.
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public List<String> puntuaciones() throws IOException {
		if (mongoClient == null) conectarMongoBD();
		List<String> lista = new ArrayList<String>();
		
		MongoCollection<Document> col = database.getCollection("scores");
		MongoCursor<Document> cursor = col.find().sort(descending("points")).iterator();
		while (cursor.hasNext()) {
			JSONObject obj = new JSONObject(cursor.next().toJson());
			lista.add(obj.getString("user") + " " + obj.getInt("points") + " points (Suit " + obj.getString("suit") + ", " + obj.getString("timestamp") + ")");
		}
		
		return lista;
	}
	
	/**
     * Desconecta al usuario actual y cierra la conexión a MongoDB.
     * 
     * @throws IOException Si ocurre un error al interactuar con la base de datos.
     */
	public void desconectar() throws IOException {
		if (mongoClient == null) conectarMongoBD();
		JOptionPane.showMessageDialog(null, "¡Hasta pronto!", "Alerta", JOptionPane.PLAIN_MESSAGE);
		usuarioActivo = false;
		usuarioLogueado = "";
		turno = null;
		barajaCartas = null;
		cartasCrupier = new ArrayList<Carta>();
		cartasUsuario = new ArrayList<Carta>();
		mongoClient.close();
	}

	public boolean isUsuarioActivo() {
		return usuarioActivo;
	}

	public String getUsuarioLogueado() {
		return usuarioLogueado;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getTipoBaraja() {
		return tipoBaraja;
	}

	public void setTipoBaraja(String tipoBaraja) {
		this.tipoBaraja = tipoBaraja;
	}

	public int getPuntuacionTotalCrupier() {
		return puntuacionTotalCrupier;
	}

	public void setPuntuacionTotalCrupier(int puntuacionTotalCrupier) {
		this.puntuacionTotalCrupier = puntuacionTotalCrupier;
	}

	public int getPuntuacionTotalUsuario() {
		return puntuacionTotalUsuario;
	}

	public void setPuntuacionTotalUsuario(int puntuacionTotalUsuario) {
		this.puntuacionTotalUsuario = puntuacionTotalUsuario;
	}

	public String getHistorialCrupier() {
		return historialCrupier;
	}

	public void setHistorialCrupier(String historialCrupier) {
		this.historialCrupier = historialCrupier;
	}

	public String getHistorialUsuario() {
		return historialUsuario;
	}

	public void setHistorialUsuario(String historialUsuario) {
		this.historialUsuario = historialUsuario;
	}

	public List<Carta> getBarajaCartas() {
		return barajaCartas;
	}

	public void setBarajaCartas(List<Carta> barajaCartas) {
		this.barajaCartas = barajaCartas;
	}

	public List<Carta> getCartasCrupier() {
		return cartasCrupier;
	}

	public void setCartasCrupier(List<Carta> cartasCrupier) {
		this.cartasCrupier = cartasCrupier;
	}

	public List<Carta> getCartasUsuario() {
		return cartasUsuario;
	}

	public void setCartasUsuario(List<Carta> cartasUsuario) {
		this.cartasUsuario = cartasUsuario;
	}

	public boolean isPlantadaIA() {
		return plantadaIA;
	}

	public void setPlantadaIA(boolean plantadaIA) {
		this.plantadaIA = plantadaIA;
	}

	public boolean isPerdidoIA() {
		return perdidoIA;
	}

	public void setPerdidoIA(boolean perdidoIA) {
		this.perdidoIA = perdidoIA;
	}

	public boolean isPerdidoUsuario() {
		return perdidoUsuario;
	}

	public void setPerdidoUsuario(boolean perdidoUsuario) {
		this.perdidoUsuario = perdidoUsuario;
	}

	public boolean isPlantadoUsuario() {
		return plantadoUsuario;
	}

	public void setPlantadoUsuario(boolean plantadoUsuario) {
		this.plantadoUsuario = plantadoUsuario;
	}

	public boolean isPartidaFinalizada() {
		return partidaFinalizada;
	}

	public void setPartidaFinalizada(boolean partidaFinalizada) {
		this.partidaFinalizada = partidaFinalizada;
	}

	public boolean isPartidaIniciada() {
		return partidaIniciada;
	}

	public void setPartidaIniciada(boolean partidaIniciada) {
		this.partidaIniciada = partidaIniciada;
	}

	public String getTipoBarajaAvreviadoString() {
		return tipoBarajaAvreviadoString;
	}

	public void setTipoBarajaAvreviadoString(String tipoBarajaAvreviadoString) {
		this.tipoBarajaAvreviadoString = tipoBarajaAvreviadoString;
	}
	
	


}
