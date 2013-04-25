/*
 * File: Page.java
 * Author: zhang shiqi
 * Function: 页面置换
 */
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Array;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class Page extends JPanel {
	myPage fifo;
	myPage lru;
	JPanel control = new JPanel();//控制按键panel
	private String state = "f";

	public Page() {
		setLayout(null);
		fifo = new myPage(0);
		lru = new myPage(1);
		fifo.setBounds(0, 0, 500, 700);
		lru.setBounds(500, 0, 500, 700);
		add(fifo);
		add(lru);

		control.setBounds(1000, 0, 200, 700);
		control.setBackground(Color.orange);
		control.setLayout(null);
		JRadioButton f = new JRadioButton("FIFO");
		JRadioButton l = new JRadioButton("LRU");
		ButtonGroup group = new ButtonGroup();
		group.add(f);
		group.add(l);
		f.setBounds(50, 100, 50, 20);
		l.setBounds(120, 100, 50, 20);
		control.add(f);
		control.add(l);
		Border boder = BorderFactory.createEtchedBorder(); //加边框
		Border title = BorderFactory.createTitledBorder(boder,"");
		control.setBorder(title);
		f.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				state = "f";

			}
		});
		l.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				state = "l";
			}
		});
		JButton start = new JButton("start");
		start.setBounds(70, 150, 80, 30);
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (state == "f") {
					fifo.clear();
					new Thread(fifo).start();
				}
				if (state == "l") {
					lru.clear();
					new Thread(lru).start();
				}
			}
		});
		JButton clear = new JButton("clear");
		clear.setBounds(70, 250, 80, 30);
		clear.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				fifo.clear();
				lru.clear();
			}
		});
		control.add(clear);
		control.add(start);
		add(control);
	}
}


class myPage extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea text2;
	JTable table = new JTable(320, 6);
	int[] block;
	int[] order;
	int[] useTimes;
	int writeOut;
	String[] s = new String[4];
	String s2="";
	int outPage=0;
	int type;// type 为0，FIFO,为1，LRU
	public myPage(int type) {
		this.type = type;
		setLayout(null);
		setBackground(Color.white);
		text2 = new JTextArea();
		text2.setBackground(Color.pink);
		JScrollPane p2 = new JScrollPane(text2);
		p2.setBounds(10, 500, 480, 200);
		add(p2);
		JLabel label1 = new JLabel("LIFO");
		JLabel label2 = new JLabel("缺页率");
		label1.setBounds(80, 0, 50, 30);
		label2.setBounds(80, 450, 50, 30);
		add(label1);
		add(label2);

		JScrollPane p = new JScrollPane(table);
		p.setBounds(10, 50, 480, 400);
		add(p);
		setNumber();
		setHeader();

		block = new int[4];
		order = new int[320];
		useTimes = new int[33];
		initBlockArray();
		initOrder_usedArray();
		
		Border boder = BorderFactory.createEtchedBorder(); //加边框
		Border title = BorderFactory.createTitledBorder(boder,"");
		setBorder(title);
	}
	void initLabel(){
		String string="";
		if(type==0)
			string="LIFO";
		else string ="LRU";
		JLabel label1 = new JLabel(string);
		JLabel label2 = new JLabel("缺页率");
		label1.setBounds(80, 0, 50, 30);
		label2.setBounds(80, 450, 50, 30);
		add(label1);
		add(label2);
	}
	//初始化tableheader内容
	void setHeader() {
		table.getColumnModel().getColumn(0).setHeaderValue("序号");
		table.getColumnModel().getColumn(1).setHeaderValue("指令");
		table.getColumnModel().getColumn(2).setHeaderValue("所在页");
		table.getColumnModel().getColumn(3).setHeaderValue("调出页");
		table.getColumnModel().getColumn(4).setHeaderValue("现在页");
		table.getColumnModel().getColumn(5).setHeaderValue("是否缺页");
	}
	public void clear(){
		initBlockArray();
		initOrder_usedArray();
		s2="";
		outPage=0;
		text2.setText("");
		for(int i =0;i<320;i++)
			for(int j = 1;j<6;j++){
				table.setValueAt("", i, j);
			}
		
	}
	void setNumber() {
		for (int i = 1; i <= 320; i++) {
			table.setValueAt(i, i - 1, 0);
		}
	}

	void initOrder_usedArray() {
		for (int i = 0; i < 320; i++) {
			order[i] = 0;	
		}
		for(int i = 0;i<33;i++)
		useTimes[i]=0;
	}

	void initBlockArray() {
		for (int i = 0; i < 4; i++) {
			block[i] = 0;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(type==0)
		fstart();
		else lstart();
	}

	//FIFO算法
	void fstart() {
		int command, page;
		String absence = "NO";
		boolean back = true;
		boolean isFirst = true;
		boolean isInTurn = false;
		command = (int) (Math.random() * 319);
		int count = 0;
		while (count < 320) {
			if (isFirst) {
				isFirst = false;
			}
			else 
				{
				if(!isInTurn){
									
				while (true) {
					if(command==319||command==318){
						back=false;
					}
					if(command==0||command==1){
						back=true;
					}
					if (back) {
						command = (int) (command + Math.random()
								* (320 - command ));
						if (order[command] == 0) {
							back = false;							
							break;
						}
							
					} else {						
						command = (int) (Math.random() * command);
						{
							if (order[command] == 0) {
								back = true;
								break;
							}
						}
					}
				}
				
			}
			else{
				if(command!=319){
					command=command+1;					
				}
				else{
					while(true){
						command = (int)(Math.random()*command);
								if(order[command]==0) break;
					}
				}
				
				
			}
				}
			page = (int) (command / 10)+1;
			int temp = f_checkBlock(page);
			nowPage();
			if (temp == 1) {
				absence = "NO";

				setValue(count, command, page, 0, s2, absence);
			} else {
				absence = "YES";
				setValue(count, command, page, writeOut, s2, absence);
				outPage++;
			}
			count++;
			order[command]=1;
			if(isInTurn==true) isInTurn = false;
			else isInTurn = true;
		}
		text2.append("缺页率： "+(double)outPage/320.0+"\n");
	}
	
	//LRU算法
	void lstart(){
		int command, page;
		String absence = "NO";
		boolean back = true;
		boolean isFirst = true;
		boolean isInTurn = false;
		command = (int) (Math.random() * 319);
		int count = 0;
		while (count < 320) {
			if (isFirst) {
				isFirst = false;
			}
			else 
				{
				if(!isInTurn){
									
				while (true) {
					if(command==319||command==318){
						back=false;
					}
					if(command==0||command==1){
						back=true;
					}
					if (back) {
						command = (int) (command + Math.random()
								* (320 - command ));
						if (order[command] == 0) {
							back = false;							
							break;
						}
							
					} else {						
						command = (int) (Math.random() * command);
						{
							if (order[command] == 0) {
								back = true;
								break;
							}
						}
					}
				}
				
			}
			else{
				if(command!=319){
					command=command+1;					
				}
				else{
					while(true){
						command = (int)(Math.random()*command);
								if(order[command]==0) break;
					}
				}
				
				
			}
				}
			page = (int) (command / 10)+1;
			int temp =f_checkBlock(page);
			nowPage();
			if (temp == 1) {
				absence = "NO";
				setValue(count, command, page, 0, s2, absence);
			} else {
				absence = "YES";
				setValue(count, command, page, writeOut, s2, absence);
				outPage++;
			}
			count++;
			order[command]=1;
			if(isInTurn==true) isInTurn = false;
			else isInTurn = true;
		}
		text2.append("缺页率： "+(double)outPage/320.0+"\n");
	}
	//现在4个块中所含页的情况
	void nowPage(){
		s2="";
		for(int i =0;i<4;i++){
			if(block[i]!=0){
				s2=s2+block[i]+" ";
			}
		}
		
	}
	//设置table一行的内容
	void setValue(int seq, int command, int page, int out, String now,
			String absence) {
		table.setValueAt(command, seq, 1);
		table.setValueAt(page, seq, 2);
		table.setValueAt(out, seq, 3);
		table.setValueAt(now, seq, 4);
		table.setValueAt(absence, seq, 5);
	}
	//FIFO的页面置换算法
	int f_checkBlock(int page) {
		for (int i = 0; i < 4; i++) {
			if (page == block[i]) {
				useTimes[page]+=1;
				return 1;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (block[i] == 0) {
				block[i] = page;
				useTimes[page]+=1;
				return 1;
			}
		}
		writeOut = block[0];
		block[0] = block[1];
		block[1] = block[2];
		block[2] = block[3];
		block[3] = page;
		return 0;
	}
	//LRU的页面置换算法
	int l_checkBlock(int page){
		for (int i = 0; i < 4; i++) {
			if (page == block[i]) {
				useTimes[page]+=1;
				return 1;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (block[i] == 0) {
				block[i] = page;
				useTimes[page]+=1;
				return 1;
			}
		}
		int max=500,index = 0;
		for(int i=0;i<4;i++){
			int num=block[i];
			if(useTimes[num]<max){
				max=useTimes[num];
				index=num;
			}
		}
		writeOut = block[index];		
		block[index] = page;
		useTimes[page]++;
		return 0;
	}
}


