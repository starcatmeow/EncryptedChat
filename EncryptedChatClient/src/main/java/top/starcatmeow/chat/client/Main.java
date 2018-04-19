package top.starcatmeow.chat.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Dongruixuan Li on 2017/1/30.
 */
public class Main implements ActionListener {
    static ChatClientUI ui;
    static Thread messagehandlerThread = null;

    public static void main(String[] args) {
        getUIString.init();
        ui = new ChatClientUI();
        SwingUtilities.invokeLater(ui);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
