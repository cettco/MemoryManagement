/*
 * File: MainFrame.java
 * Author: zhang shiqi
 * Function: build the main frame,which includes two panels. one is
 *           partition function panel,the other is page function panel. using cardlayout you can swithc the
 *           panels.
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainFrame extends JFrame {
	CardLayout card ;
	JPanel switchPage;
	JPanel show;
	Partition partition;
	Page page;
	public MainFrame(){
		card = new CardLayout();
		switchPage = new JPanel();
		show = new JPanel(card);
		partition = new Partition();
		page = new Page();
		JButton b1 =new JButton("动态分区");
		JButton b2 =new JButton("请求调页");
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				card.show(show, "part");
				
			}
		});
		b2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				card.show(show, "page");
				
			}
		});
		switchPage.add(b1);
		switchPage.add(b2);
		add(switchPage,BorderLayout.NORTH);
	    show.add(partition,"part");
	    show.add(page,"page");
	    add(show);
				
	}
}
