package client;

import dao.CommentaireImpl;
import entities.Commentaire;
import entities.Membre;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private CommentaireImpl commentaire = new CommentaireImpl();
    private Socket socket;
    private BufferedReader bufferedReader;
    private Membre user;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.user = new Membre();
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientInf = bufferedReader.readLine();
            String[] inf = clientInf.split("~");
            user.setIdM(Integer.parseInt(inf[0]));
            user.setUsername(inf[1]);
            this.clientUsername = user.getUsername();
            boolean con = false;
            boolean affiche = true;
            for (ClientHandler c :
                    clientHandlers) {
                if (c.clientUsername.equals(clientUsername)) {
                    con = true;
                    break;
                }
            }
            if (con == false)
                clientHandlers.add(this);
            user.setPassword(inf[2]);
            if (clientHandlers.size() >=4) {
                broadcastMessageServer("Server : Le chat est plein!");
                affiche = false;
                removeClientHandler();
            }
            for (ClientHandler c :
                    clientHandlers) {
                System.out.println(c.clientUsername);
                if (c.clientUsername.equals(clientUsername) && con==true) {
                    broadcastMessageServer("Server : Il y'a déja un membre sous ce nom!");
                    affiche = false;
                    removeClientHandler();
                    break;
                }
            }
            if (affiche) {
                broadcastMessageServer("SERVER : Bienvenu dans le chat!");
                broadcastMessage("SERVER : " + user.getUsername() + " est entré dans le chat");
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);
                String[] message = messageFromClient.split(":");
                if (message[1].trim().equals("/quit")) {
                    broadcastMessageServer("Server : Vous n'est plus connecté!");
                    removeClientHandler();
                    break;
                } else {
                    Commentaire c = new Commentaire();
                    c.setMessage(message[1]);
                    c.setMembre(user);
                    c.setDateC(LocalDateTime.now());
                    commentaire.create(c);
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void broadcastMessageServer(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER : " + clientUsername + " est sorti du chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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

    public String getClientUsername() {
        return clientUsername;
    }

}
