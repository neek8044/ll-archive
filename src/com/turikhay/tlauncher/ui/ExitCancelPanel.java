package com.turikhay.tlauncher.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ExitCancelPanel extends JPanel {
	private static final long serialVersionUID = -1998881418330942647L;
	
	LocalizableLabel label; LocalizableButton button;
	Font font = new Font("", Font.BOLD, 12);
	
	ExitCancelPanel(final ConsoleFrame cf){
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.setBackground(Color.black);
		
		this.label = new LocalizableLabel(SwingConstants.CENTER);
		this.label.setForeground(Color.white);
		
		this.button = new LocalizableButton("console.close.cancel");
		
		label.setFont(font);
		label.setForeground(Color.white);
		
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) { cf.cancelHiding(); }
		});
		
		JPanel labelPan = new JPanel(), buttonPan = new JPanel();
		labelPan.setBackground(Color.black); buttonPan.setBackground(Color.black);
		labelPan.add(label); buttonPan.add(button);
		
		this.add("Center", labelPan);
		this.add("South", buttonPan);
	}
	
	void setTimeout(int timeout){
		this.label.setText("console.close.text", "s", timeout);
	}
}
