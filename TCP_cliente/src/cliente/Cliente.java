package cliente;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.*;

public class Cliente extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField campoIntroducir;
    private final JTextArea areaPantalla;
    private final String servidorChat;

    private Socket cliente;
    private OutputStream out;
    private InputStream in;

    /*
     * el constructor del cliente inicia la ventana en la que se ejecutar· el
     * cliente
     */
    public Cliente(String host) {
        super("Cliente"); // le pone titulo a la ventana grafica

        servidorChat = host; // servidor al que se conecta el cliente

        Container contenedor = getContentPane(); // crea ventana grafica

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

    /*
     * Metodo para configurar y ejecutar el cliente
     */
    private void ejecutarCliente() {
        mostrarMensaje("Intentando realizar conexion\n");
        try {

            // Paso 1: crear socket para realizar la conexion con el servidor
            cliente = new Socket(InetAddress.getByName(servidorChat), 12345);
            mostrarMensaje("Conectado a: " + cliente.getInetAddress().getHostName());

            // Paso 2: obtener flujos de entrada y salida del socket
            out = cliente.getOutputStream();
            in = cliente.getInputStream();
            mostrarMensaje("\nSe crearon los canales de E/S\n");

            // permitir la escritura de mensajes en campoIntroducir
            establecerCampoTextoEditable(true);

            // Paso 3: llamada a metodo que lee del socket
            recibirDatos();

            // Paso 4: cerrar conexion
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
        } while (!mensaje.equals("SERVIDOR>>> TERMINAR"));
        enviarDatos("TERMINAR");
    }

    /*
     * Metodo para cerrar canales y sockets
     */
    private void cerrarConexion() {
        mostrarMensaje("\nFinalizando la conexion\n");
        establecerCampoTextoEditable(false);
        try {
            in.close();
            out.close();
            cliente.close();
        } catch (IOException excepcionES) {
        }
    }

    /**
     * Metodo que envia mensajes escritos en campoIntroducir al servidor
     */
    private void enviarDatos(String mensaje) {
        PrintWriter salida = new PrintWriter(out, true);
        salida.println("CLIENTE>>> " + mensaje);
        mostrarMensaje("\nCLIENTE>>> " + mensaje);
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
        Cliente aplicacion;

        if (args.length == 0)
            aplicacion = new Cliente("127.0.0.1");
        else
            // mediante arg[0] se indica la direccion del servidor
            aplicacion = new Cliente(args[0]);

        aplicacion.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aplicacion.ejecutarCliente();
    }
}