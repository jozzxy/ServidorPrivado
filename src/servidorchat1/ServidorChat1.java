/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package servidorchat1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author EQUIPO17
 */
public class ServidorChat1 {
    private ServerSocket servidorSocket;
    private List<ComunicacionCliente> clientes;

    public ServidorChat1(InetAddress direccionIP, int puerto) {
        try {
            if (direccionIP != null) {
                servidorSocket = new ServerSocket(puerto, 0, direccionIP);
            } else {
                servidorSocket = new ServerSocket(puerto);
            }
            clientes = new ArrayList<>();
            System.out.println("Servidor de Chat en ejecución en la dirección IP " + direccionIP.getHostAddress() + " y el puerto " + puerto);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ha fallado al inicializar el servidor");
            // Aquí puedes agregar un mensaje específico de error.
        }
    }

    public void iniciar() {
        while (true) {
            try {
                Socket socketCliente = servidorSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

                ComunicacionCliente manejadorCliente = new ComunicacionCliente(socketCliente, this);
                clientes.add(manejadorCliente);

                new Thread(manejadorCliente).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void transmitirMensaje(String remitente, String mensaje) {
        for (ComunicacionCliente cliente : clientes) {
            cliente.enviarMensaje(remitente, mensaje);
        }
    }

    public synchronized void removerCliente(ComunicacionCliente cliente) {
        clientes.remove(cliente);
    }

    public synchronized List<String> getUsuariosConectados() {
        List<String> usuariosConectados = new ArrayList<>();
        for (ComunicacionCliente cliente : clientes) {
            usuariosConectados.add(cliente.getApodo());
        }
        return usuariosConectados;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java ServidorChat <dirección_IP> <puerto>");
            return;
        }

        InetAddress direccionIP = null;
        try {
            direccionIP = InetAddress.getByName(args[0]);
        } catch (IOException e) {
            System.out.println("La dirección IP especificada no es válida.");
            return;
        }
        
        int puerto = Integer.parseInt(args[1]);

        ServidorChat1 servidorChat = new ServidorChat1(direccionIP, puerto);
        servidorChat.iniciar();
    }
}
