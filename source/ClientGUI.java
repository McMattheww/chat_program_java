import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ClientGUI extends JFrame implements ActionListener{

    //client object used to manage connection
    Client client;

    /** text fields and submit button  */
    JTextArea outputArea;
    JScrollPane scrollPane;
    JTextField inputArea;
    JButton submitButton;

    /** menu items */
    JMenuBar menus;
    JMenu fileMenu;
    JMenuItem quitItem;
    JMenuItem newConnectionItem;
    JMenuItem disconnectItem;


    public static void main(String [] args){
        //create GUI object
        ClientGUI gui = new ClientGUI();

        //set GUI properties
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setTitle("Chat");
        gui.pack();
        gui.setSize(500, 500); //starting size
        gui.setVisible(true);
    }

    public ClientGUI() {
        client = new Client(); //instantiate client
        setupGUI();  //call setup functions
        setupMenu();
    }


    private void setupGUI(){
        setLayout(new BorderLayout());

        //setup text areas and buttons
        submitButton = new JButton("Send");
        inputArea = new JTextField();
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        scrollPane = new JScrollPane(outputArea);
        scrollPane.setMinimumSize(new Dimension(200, 150));
        inputArea.setFont(new Font("Arial", Font.PLAIN, 16));
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));


        //setup custom print stream to redirect console output to the text display area
        PrintStream printStream = new PrintStream(new CustomOutputStream(outputArea));
        System.setOut(printStream);
        System.setErr(printStream);


        //add text fields and submit button to frame
        add(scrollPane, BorderLayout.CENTER);
        add(inputArea, BorderLayout.AFTER_LAST_LINE);
        add(submitButton, BorderLayout.AFTER_LINE_ENDS);


        //add submit button to action listener, and set as default button
        submitButton.addActionListener(this);
        getRootPane().setDefaultButton(submitButton);

    }

    private void setupMenu () {
        //setup file menu
        fileMenu = new JMenu("File");
        quitItem = new JMenuItem("Quit");
        newConnectionItem = new JMenuItem("New Connection");
        disconnectItem = new JMenuItem("Disconnect");
        fileMenu.add(newConnectionItem);
        fileMenu.add(quitItem);
        fileMenu.add(disconnectItem);
        menus = new JMenuBar();
        setJMenuBar(menus);
        menus.add(fileMenu);

        // register the menu items with this action listener
        quitItem.addActionListener(this);
        newConnectionItem.addActionListener(this);
        disconnectItem.addActionListener(this);
    }



    public void actionPerformed (ActionEvent e) {
        // determine which button or menu was selected
        JComponent buttonPressed = (JComponent) e.getSource();

        //user pressed quit menu button
        if (buttonPressed == quitItem){
            System.exit(1);
        }

        //user pressed disconnect menu button
        if (buttonPressed == disconnectItem){
            client.disconnect();
        }

        //user pressed submit button to send message
        if (buttonPressed == submitButton){
            if (client.socket != null && client.socket.isConnected()) {
                String message = inputArea.getText();
                if (!message.isEmpty()) {
                    client.send_message(message);
                    inputArea.setText("");
                }
            }
        }

        //user pressed menu button to initiate new connection
        if (buttonPressed == newConnectionItem){
            try {
                // Show input dialog and get username and IP for connection
                String name = JOptionPane.showInputDialog(null, "Enter your username:",
                        "Input Dialog", JOptionPane.QUESTION_MESSAGE);
                String IP = JOptionPane.showInputDialog(null, "Enter IP address:",
                        "Input Dialog", JOptionPane.QUESTION_MESSAGE);

                // Check if the user clicked "Cancel" or closed the dialog without typing anything
                if (name == null || IP == null) {
                    JOptionPane.showMessageDialog(null, "No input provided. Canceling.");
                    return;
                }
                //have the client attempt connection using the name and IP
                client.connect(IP, name);
            } catch (Exception e2) {
                JOptionPane.showMessageDialog(null, "An error occurred: " + e2.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    //custom output stream that redirects system.out to the GUI text area
    public class CustomOutputStream extends OutputStream {
        private final JTextArea textArea;

        public CustomOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }
        @Override
        public void write(int b) throws IOException {
            SwingUtilities.invokeLater(() -> {
                textArea.append(String.valueOf((char)b));
                textArea.setCaretPosition(textArea.getDocument().getLength());
            });
        }
    }
}
