package chatApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame{

    Socket socket;

    BufferedReader br;
    PrintWriter out;


    //declare gui
    private JLabel heading = new JLabel("Client area");
    private JTextArea msgArea = new JTextArea();
    private JTextField msgip = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Client(){
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());


            createGUI();
            HandleEvent();

            startReading();
            //startWritting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void HandleEvent() {
        msgip.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode()==10){
                    String getContent = msgip.getText();
                    msgArea.append("Me : " + getContent + "\n");
                    System.out.println(getContent);
                    out.println(getContent);
                    out.flush();
                    msgip.setText("");
                    msgip.requestFocus();
                    System.out.println("Entered");
                }
            }
        });
    }

    private void createGUI() {
        //gui
        this.setTitle("Client Messenger");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //code for component
        heading.setFont(font);
        msgip.setFont(font);
        msgArea.setFont(font);

        heading.setIcon(new ImageIcon("logo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        msgArea.setEditable(false);
        msgip.setHorizontalAlignment(SwingConstants.CENTER);
        //border layout
        this.setLayout(new BorderLayout());

        //position
        this.add(heading,BorderLayout.NORTH);
        this.add(msgArea,BorderLayout.CENTER);
        this.add(msgip,BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private void startReading() {
        Runnable r1 = ()->{
            System.out.println("Reader Started");
            try{
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("Exit")){
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        msgip.setEnabled(false);
                        socket.close();
                        break;
                    }
                    msgArea.append("Server : " + msg+ "\n");
                }
            }
            catch (Exception e){
                System.out.println("Connection Closed");
            }
        };
        new Thread(r1).start();
    }

    private void startWritting() {
        System.out.println("Writter started");
        Runnable r2 =()->{
            try {
                while (!socket.isClosed()){
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("Exit")){
                        socket.close();
                        break;
                    }
                }
            }
            catch (Exception e){
                System.out.println("Connection Closed");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client");
        new Client();
    }
}
