package client;

import dao.IMembre;
import dao.MembreImpl;
import entities.Membre;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Membre user;

    public Client(Socket socket, Membre user) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = user;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public static void main(String[] args) throws IOException {
        IMembre iMembre = new MembreImpl();
        Membre u = null;
        String username = "";
        String password = "";
        int choix = 0;
        Scanner scanner = new Scanner(System.in);
        while (!(choix == 1 || choix == 2)) {
            System.out.println("----Bienvenu dans notre groupe chat----");
            System.out.println("1- Se connecter avec un compte existant!");
            System.out.println("2- Se connecter avec un nouveau compte!");
            System.out.println("----------------------------------------");
            choix = scanner.nextInt();
        }
        scanner.nextLine();
        switch (choix) {
            case 1 -> {
                System.out.println("Entrer votre nom d'utilisateur :");
                username = scanner.nextLine();
                System.out.println("Entrer votre mot de passe :");
                password = scanner.nextLine();
                u = iMembre.getM(username, password);
                if (u != null) {
                    Socket socket = new Socket("localhost", 1234);
                    Client client = new Client(socket, u);
                    client.listenForMessage();
                    client.sendMessage();
                } else {
                    System.out.println("Username ou Mot de passe incorrect!");
                }
            }
            case 2 -> {
                System.out.println("Entrer votre nom d'utilisateur :");
                username = scanner.nextLine();
                if (iMembre.getU(username) == null) {
                    System.out.println("Entrer votre mot de passe :");
                    password = scanner.nextLine();
                    u = new Membre();
                    u.setUsername(username);
                    u.setPassword(password);
                    iMembre.create(u);
                    u = iMembre.getM(username, password);
                    if (u != null) {
                        Socket socket = new Socket("localhost", 1234);
                        Client client = new Client(socket, u);
                        client.listenForMessage();
                        client.sendMessage();
                    } else {
                        System.out.println("Username ou Mot de passe incorrect!");
                    }
                } else {
                    System.out.println("Y'a déja un compte sous ce nom");
                }
            }
            default -> {
            }
        }


    }

    public void sendMessage() {
        try {

            bufferedWriter.write(user.getIdM() + "~" + user.getUsername().toUpperCase() + "~" + user.getPassword());
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write((user.getUsername()).toUpperCase() + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {

                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                        String[] message = msgFromGroupChat.split(":");
                        if (message[1].trim().equals("Vous n'est plus connecté!")) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            System.exit(0);
                        }
                        if (message[1].trim().equals("Le chat est plein!")) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            System.exit(0);
                        }
                        if (message[1].trim().equals("Il y'a déja un membre sous ce nom!")) {
                            closeEverything(socket, bufferedReader, bufferedWriter);
                            System.exit(0);
                        }

                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
