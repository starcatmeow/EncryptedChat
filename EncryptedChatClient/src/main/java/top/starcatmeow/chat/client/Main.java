package top.starcatmeow.chat.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Main implements ActionListener {
    static ChatClientUI ui = new ChatClientUI();
    static Thread messagehandlerThread = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ui);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
