/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorchat1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteChat {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java ClienteChat <dirección_IP_del_servidor> <puerto_del_servidor>");
            return;
        }

        String servidorIP = args[0];
        int puertoServidor = Integer.parseInt(args[1]);

        try {
            Socket socketCliente = new Socket(servidorIP, puertoServidor);
            ObjectOutputStream salidaStream = new ObjectOutputStream(socketCliente.getOutputStream());
            ObjectInputStream entradaStream = new ObjectInputStream(socketCliente.getInputStream());

            Scanner scanner = new Scanner(System.in);

            System.out.print("Ingresa tu apodo: ");
            String apodo = scanner.nextLine();

            // Registro de usuario
            MensajeChat mensajeRegistro = new MensajeChat(apodo, "Se ha unido al chat.");
            salidaStream.writeObject(mensajeRegistro);

            new Thread(() -> {
                try {
                    while (true) {
                        // Escuchar mensajes del servidor
                        MensajeChat mensajeChat = (MensajeChat) entradaStream.readObject();
                        System.out.println(mensajeChat.getRemitente() + ": " + mensajeChat.getMensaje());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();

            // Enviar mensajes al servidor
            while (true) {
                String mensaje = scanner.nextLine();
                MensajeChat mensajeChat = new MensajeChat(apodo, mensaje);
                salidaStream.writeObject(mensajeChat);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

