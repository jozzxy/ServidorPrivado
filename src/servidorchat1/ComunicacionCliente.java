/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorchat1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ComunicacionCliente implements Runnable {
    private Socket socketCliente;
    private ServidorChat1 servidorChat;
    private ObjectInputStream entradaStream;
    private ObjectOutputStream salidaStream;
    private String apodo;
    private FiltroCensura filtroCensura;

    public ComunicacionCliente(Socket socketCliente, ServidorChat1 servidorChat) {
        this.socketCliente = socketCliente;
        this.servidorChat = servidorChat;
        filtroCensura = new FiltroCensura();
        try {
            salidaStream = new ObjectOutputStream(socketCliente.getOutputStream());
            entradaStream = new ObjectInputStream(socketCliente.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Recibir registro de usuario
            Object objetoRegistro = entradaStream.readObject();
            if (objetoRegistro instanceof MensajeChat) {
                MensajeChat mensajeRegistro = (MensajeChat) objetoRegistro;
                apodo = mensajeRegistro.getRemitente();
                // Informar a todos los clientes sobre el nuevo usuario
                servidorChat.transmitirMensaje("Servidor", apodo + " se ha unido al chat.");

                // Informar al nuevo usuario sobre los usuarios actuales
                List<String> usuariosConectados = servidorChat.getUsuariosConectados();
                enviarMensaje("Servidor", "Usuarios conectados: " + usuariosConectados);

                // Comenzar a escuchar mensajes
                while (true) {
                    Object objetoMensaje = entradaStream.readObject();
                    if (objetoMensaje instanceof MensajeChat) {
                        MensajeChat mensajeChat = (MensajeChat) objetoMensaje;
                        servidorChat.transmitirMensaje(mensajeChat.getRemitente(), mensajeChat.getMensaje());
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Manejar desconexión
            System.out.println("Cliente desconectado: " + socketCliente.getInetAddress().getHostAddress());
            servidorChat.transmitirMensaje("Servidor", apodo + " ha abandonado el chat.");
            servidorChat.removerCliente(this);
        }
    }

    public void enviarMensaje(String remitente, String mensaje) {
  	  mensaje = filtroCensura.filtrarMensaje(mensaje);
        try {
        
            MensajeChat mensajeChat = new MensajeChat(remitente, mensaje);
            salidaStream.writeObject(mensajeChat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getApodo() {
        return apodo;
    }
}
