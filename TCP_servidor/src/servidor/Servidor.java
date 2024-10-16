package servidor;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;

public class Servidor extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField campoIntroducir;
    private final JTextArea areaPantalla;

    private ServerSocket servidor;
    private Socket conexion;
    private OutputStream out;
    private InputStream in;

    /**
     * constructor que inicia la ventana gráfica del servidor
     */
    public Servidor() {
        super("Servidor"); // le pone titulo a la ventana gráfica

        Container contenedor = getContentPane(); // crea ventana gráfica

        // Creamos componente donde escribir mensajes, campoIntroducir
        campoIntroducir = new JTextField();
        // Deshabilitamos campoIntroducir
        campoIntroducir.setEditable(false);
        // Activamos "listener" para campoIntroducir. Se encarga de detectar
        // la escritura de texto, y llamar al metodo enviarDatos() para
        // enviar los escrito al cliente
        campoIntroducir.addActionListener((ActionEvent evento) -> {
            enviarDatos(campoIntroducir.getText());
            // Tras enviar texto al cliente limpiamos componente
            campoIntroducir.setText("");
        });

        // Añadir componente campoIntroducir al contenedor
        contenedor.add(campoIntroducir, BorderLayout.NORTH);

        // crear componente areaPantalla
        areaPantalla = new JTextArea();
        // Añadir componente areaPantalla al contenedor
        contenedor.add(new JScrollPane(areaPantalla), BorderLayout.CENTER);
        setSize(300, 150);
        setVisible(true);
    }

    /**
     * Metodo para configurar y ejecutar el servidor
     */
    public void ejecutarServidor() {
        try {
            // Paso 1: crear un objeto ServerSocket:
            servidor = new ServerSocket(12345);

            // Paso 2: esperar por una solicitud de conexion
            mostrarMensaje("Esperando una conexión\n");
            conexion = servidor.accept();
            mostrarMensaje("Conexión recibida de: " + conexion.getInetAddress().getHostName());

            // Paso 3: obtener flujos de entrada y salida del socket
            out = conexion.getOutputStream();
            in = conexion.getInputStream();
            mostrarMensaje("\nSe recibieron los flujos de E/S\n");

            // enviar mensaje de conexion exitosa al cliente
            enviarDatos("Conexion exitosa");

            // permitir la escritura de mensajes en campoIntroducir
            establecerCampoTextoEditable(true);

            // Paso 4: llamada a metodo que lee del socket
            recibirDatos();

            // Paso 5: cerrar conexion
            cerrarConexion();

        } catch (Exception e) {
        }
    }

    /**
     * Metodo que recibe los datos del sockets
     */
    private void recibirDatos() throws IOException {
        String mensaje;
        Scanner entrada = new Scanner(in);
        do {
            mensaje = entrada.nextLine();
            mostrarMensaje("\n" + mensaje);
        } while (!mensaje.equals("CLIENTE>>> TERMINAR"));
        enviarDatos("TERMINAR");
    }

    /**
     * Metodo para cerrar canales y sockets
     */
    private void cerrarConexion() {
        mostrarMensaje("\nFinalizando la conexion\n");
        establecerCampoTextoEditable(false);
        try {
            in.close();
            out.close();
            conexion.close();
        } catch (IOException excepcionES) {
        }
    }

    /**
     * Metodo que envia mensajes escritos en campoIntroducir al cliente
     */
    private void enviarDatos(String mensaje) {
        PrintWriter salida = new PrintWriter(out, true);
        salida.println("SERVIDOR>>> " + mensaje);
        mostrarMensaje("\nSERVIDOR>>> " + mensaje);
    }

    /**
     * Metodo para mostrar mensajes en areaPantalla
     */
    private void mostrarMensaje(String mensajeAMostrar) {
        areaPantalla.append(mensajeAMostrar);
    }

    /*
     * Metodo que activa/desactiva edicion en componente campoIntroducir
     */
    private void establecerCampoTextoEditable(boolean editable) {
        campoIntroducir.setEditable(editable);
    }

    public static void main(String args[]) {
        Servidor aplicacion = new Servidor(); // crea servidor de la aplicacion
        aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplicacion.ejecutarServidor(); // ejecutamos el servidor
    }
}