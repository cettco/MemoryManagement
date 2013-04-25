/*
 * File: Partition.java
 * Author: zhang shiqi
 * Function: ��̬����
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

public class Partition extends JPanel {
	first f;
	best b;
	JPanel control;
	final JComboBox<String> selection = new JComboBox<String>();//�Զ�������ѡ����䷽��
	JComboBox<String> allocBox = new JComboBox<String>();//�ֶ�
	JComboBox<String> freeBox = new JComboBox<String>();
	final JComboBox<String> selection2 = new JComboBox<String>();
	boolean usedFifo=false;
	boolean useBest=false;
	public Partition() {
		setLayout(new GridLayout(1, 3));
		f = new first();
		b = new best();
		add(f);
		add(b);		

		control = new JPanel();
		control.setLayout(null);
		
		JButton start = new JButton("��ʼ");
		start.setBounds(20, 100, 80, 30);
		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (selection.getSelectedIndex() == 0) {
					f.setAuto(0);
					f.clearList();
					repaint();
					new Thread(f).start();

				} else {
					b.setAuto(0);
					b.clearList();
					repaint();
					new Thread(b).start();
				}
			}
		});

		JLabel label1= new JLabel("�Զ�");
		JLabel label2 = new JLabel("�ֶ�");
		JLabel label3= new JLabel("alloc");
		JLabel label4 = new JLabel("free");
		label1.setBounds(50, 10, 80, 30);
		label2.setBounds(50,250, 80, 30);
		label3.setBounds(20,350, 80, 30);
		label4.setBounds(200,350, 80, 30);
		control.add(label1);
		control.add(label2);
		control.add(label3);
		control.add(label4);
		control.add(start);
		selection.addItem("�״���Ӧ�㷨");
		selection.addItem("�����Ӧ�㷨");
		selection.setBounds(20, 150, 150, 30);
		control.add(selection2);
		
		selection2.addItem("�״���Ӧ�㷨");
		selection2.addItem("�����Ӧ�㷨");
		selection2.setBounds(20, 300, 150, 30);
		control.add(selection);
		
		allocBox.setBounds(20, 400,150, 30);
		freeBox.setBounds(200, 400, 150, 30);
		control.add(allocBox);
		control.add(freeBox);
		JButton allocButton = new JButton("alloc");
		JButton freeButton = new JButton("free");
		JButton clearButton = new JButton("clear");
		clearButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (selection2.getSelectedIndex() == 0) {
					f.clearList();
					freeBox.removeAllItems();
					repaint();
				} else if(selection2.getSelectedIndex()==1){
					b.clearList();
					repaint();
					freeBox.removeAllItems();
				}
				
				
			}
		});
		allocButton.addActionListener(new allocListener());
		freeButton.addActionListener(new freeListener());
		allocButton.setBounds(20, 450,150, 30);
		freeButton.setBounds(200, 450, 150, 30);
		clearButton.setBounds(100, 550, 150, 30);
		control.add(allocButton);
		control.add(freeButton);
		control.add(clearButton);
		Border boder = BorderFactory.createEtchedBorder(); //�ӱ߿�
		Border title = BorderFactory.createTitledBorder(boder,"");
		control.setBorder(title);
		add(control);
		
		initAllocBox();
	}
	
	void initAllocBox(){
		for(int i =1;i<7;i++){
			allocBox.addItem(""+i);
		}
	}
	//�ֶ��������
	class allocListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (selection2.getSelectedIndex() == 0) {
				if(useBest){
					useBest=false;					
					freeBox.removeAllItems();
					f.clearList();
					b.clearList();
				}
				usedFifo=true;
				repaint();
				String s =(String)allocBox.getSelectedItem();
				int i = Integer.parseInt(s);
				freeBox.addItem(""+i);
				f.allocit(i);

			} else if(selection2.getSelectedIndex()==1){
				if(usedFifo){
					usedFifo=false;
					freeBox.removeAllItems();
					b.clearList();
					f.clearList();
				}
				useBest=true;
				repaint();
				String s =(String)allocBox.getSelectedItem();
				int i = Integer.parseInt(s);
				freeBox.addItem(""+i);
				b.allocit(i);
			}
			
		}
		
	}
	//�ֶ��ͷż���
	class freeListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (selection2.getSelectedIndex() == 0) {
				repaint();
				String s =(String)freeBox.getSelectedItem();
				int b1=freeBox.getSelectedIndex();
				int i = Integer.parseInt(s);
				freeBox.removeItemAt(b1);
				f.freeit(i);

			} else if(selection2.getSelectedIndex()==1){
				repaint();
				String s =(String)freeBox.getSelectedItem();
				int b2=freeBox.getSelectedIndex();
				int i = Integer.parseInt(s);
				freeBox.removeItemAt(b2);
				b.freeit(i);
			}
		}
		
	}
}
//�״���Ӧ
class first extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int startX = 50;
	final int startY = 50;
	final int width = 100;
	final int height = 640;
	JTextArea text;
	private ArrayList<Task> taskList;
	private ArrayList<Block> useList;
	private ArrayList<Block> freeList;
	boolean isAuto=true;
	public first() {
		setLayout(null);
		JLabel label = new JLabel("�״���Ӧ�㷨");
		label.setBounds(50, 10, 100, 20);
		add(label);
		JLabel label2 = new JLabel("���̼�¼");
		label2.setBounds(170, 20, 100, 20);
		add(label2);
		text = new JTextArea(5, 1);
		text.setLineWrap(true);
		text.setBounds(0, 20, 100, 300);
		JScrollPane scroll = new JScrollPane(text);
		scroll.setBounds(170, 50, 200, 300);
		add(scroll);
		useList = new ArrayList();
		freeList = new ArrayList();
		freeList.add(new Block(0, 640));
		taskList = new ArrayList();
		taskList.add(new Task(0, 0));
		taskList.add(new Task(1,130));
		taskList.add(new Task(2,60));
		taskList.add(new Task(3,100));
		taskList.add(new Task(4,200));
		taskList.add(new Task(5,140));
		taskList.add(new Task(6,60));
		taskList.add(new Task(7,50));
		Border boder = BorderFactory.createEtchedBorder(); //�ӱ߿�
		Border title = BorderFactory.createTitledBorder(boder,"�״���Ӧ�㷨");
		setBorder(title);	

	}
	//���
	public void clearList(){
		useList.clear();
		freeList.clear();
		freeList.add(new Block(0, 640));
		text.setText("");
		repaint();
	}
//��ͼ
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.yellow);
		g.fillRect(startX, startY, width, height);
		for(int i = 0;i<useList.size();i++){
			g.setColor(Color.red);
			Block block = (Block) useList.get(i);
			int y = block.getUpLimit()+startY;
			int h = block.Size();
			g.fillRect(startX, y, width, h);
			g.setColor(Color.black);
			g.drawLine(startX, y, startX+width, y);
			g.drawLine(startX, y+h, startX+width, y+h);
		}
		g.setColor(Color.yellow);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			if(isAuto)autorun();//�Զ�����
			
	}
	public void sleep(int i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	//�����㷨
	public void alloc(int id){
		Task task = (Task) taskList.get(id);
		int temp=0;

		for(int i = 0;i<freeList.size();i++){
			Block block = (Block) freeList.get(i);
			if(block.Size()==task.Size()){
				temp = block.getUpLimit();
				freeList.remove(i);	
			}
			else if(block.Size()>task.Size()){
				temp = block.getUpLimit();
				freeList.remove(i);
				freeList.add(i,new Block(block.getUpLimit()+task.Size(), block.Size()-task.Size()));
			}
		}
		useList.add(new Block(id,temp, task.Size()));
		text.append("��ҵ"+task.getId()+"���� "+task.Size()+"\n");
		
	}
	//�ͷ��㷨
	public void free(int id){
		boolean merge = false;
		for(int i =0;i<useList.size();i++){
			Block block = (Block) useList.get(i);
			if(block.getId()==id){
				{
					for(int j = 0;j<freeList.size();j++){  //�ϲ��㷨
						Block freeBlock = (Block) freeList.get(j);
						if(block.getUpLimit()==freeBlock.getDownLimit()){
							int up = freeBlock.getUpLimit();
							int size = block.Size();
							int freesize = freeBlock.Size();
							merge = true;
							freeList.remove(j);
							freeList.add(j,new Block(up,size+freesize ));
							System.out.println("");
						}
						else if(block.getDownLimit()==freeBlock.getUpLimit()){
							int up = block.getUpLimit();
							int size = block.Size();
							int freesize = freeBlock.Size();
							merge = true;
							freeList.remove(j);
							freeList.add(j,new Block(up, freesize+size));
							System.out.println("2");
						}

					}
					if(!merge){
						freeList.add(new Block(block.getUpLimit(), block.Size()));
						System.out.println("helloworld");
						
					}
					
					useList.remove(i);
					text.append("��ҵ"+block.getId()+"�ͷ�"+block.Size()+"\n");
					break;				
				}
			}
		}
		
	}
	//�Զ����к���
	private void autorun(){
		alloc(1);
		repaint();
		sleep(2000)	;
		alloc(2);
		repaint();
		sleep(2000)	;
		alloc(3);
		repaint();
		sleep(2000)	;
		free(2);
		repaint();
		sleep(2000);
		alloc(4);
		repaint();
		sleep(2000)	;
		free(3);
		repaint();
		sleep(2000)	;
		free(1);
		repaint();
		sleep(2000)	;
		alloc(5);
		repaint();
		sleep(2000)	;
		alloc(6);
		repaint();
		sleep(2000)	;
		alloc(7);
		repaint();
		sleep(2000);
		free(6);
		repaint();
		sleep(2000);
	}
	//һ�������ֶ�������ͷ�
	public void allocit(int i){
		alloc(i);
		repaint();
	}
	public void freeit(int i){
		free(i);
		repaint();
	}

	public void setAuto(int i){
		if(i==0) isAuto = true;
		else isAuto = false;
	}

}
//��ѷ���
class best extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final int startX = 50;
	final int startY = 50;
	final int width = 100;
	final int height = 640;
	JTextArea text;
	private ArrayList<Task> taskList;
	private ArrayList<Block> useList;
	private ArrayList<Block> freeList;
	boolean isAuto=true;
	public best() {
		setLayout(null);
		JLabel label = new JLabel("����㷨");
		label.setBounds(50, 10, 100, 20);
		add(label);
		JLabel label2 = new JLabel("���̼�¼");
		label2.setBounds(170, 20, 100, 20);
		add(label2);
		text = new JTextArea(5, 1);
		text.setLineWrap(true);
		text.setBounds(0, 20, 100, 300);
		JScrollPane scroll = new JScrollPane(text);
		scroll.setBounds(170, 50, 200, 300);
		add(scroll);
		useList = new ArrayList();
		freeList = new ArrayList();
		freeList.add(new Block(0, 640));
		taskList = new ArrayList();
		taskList.add(new Task(0, 0));
		taskList.add(new Task(1,130));
		taskList.add(new Task(2,60));
		taskList.add(new Task(3,100));
		taskList.add(new Task(4,200));
		taskList.add(new Task(5,140));
		taskList.add(new Task(6,60));
		taskList.add(new Task(7,50));
		Border boder = BorderFactory.createEtchedBorder(); //�ӱ߿�
		Border title = BorderFactory.createTitledBorder(boder,"����㷨");
		setBorder(title);	

	}
	public void clearList(){
		useList.clear();
		freeList.clear();
		freeList.add(new Block(0, 640));
		text.setText("");
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.yellow);
		g.fillRect(startX, startY, width, height);
		for(int i = 0;i<useList.size();i++){
			g.setColor(Color.red);
			Block block = (Block) useList.get(i);
			int y = block.getUpLimit()+startY;
			int h = block.Size();
			g.fillRect(startX, y, width, h);
			g.setColor(Color.black);
			g.drawLine(startX, y, startX+width, y);
			g.drawLine(startX, y+h, startX+width, y+h);
		}
		g.setColor(Color.yellow);

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
			if(isAuto) autorun();
			
	}
	public void sleep(int i){
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	//����
	public void alloc(int id){
		Task task = (Task) taskList.get(id);
		int temp=0;
		sortList();
		for(int i = 0;i<freeList.size();i++){
			Block block = (Block) freeList.get(i);
			if(block.Size()==task.Size()){
				temp = block.getUpLimit();
				freeList.remove(i);
				break;
			}
			else if(block.Size()>task.Size()){
				temp = block.getUpLimit();
				freeList.remove(i);
				freeList.add(i,new Block(block.getUpLimit()+task.Size(), block.Size()-task.Size()));
				break;
			}
		}
		useList.add(new Block(id,temp, task.Size()));
		text.append("��ҵ"+task.getId()+"���� "+task.Size()+"\n");
		
	}
	//�ͷ�
	public void free(int id){
		boolean merge = false;
		for(int i =0;i<useList.size();i++){
			Block block = (Block) useList.get(i);
			if(block.getId()==id){
				{
					for(int j = 0;j<freeList.size();j++){
						Block freeBlock = (Block) freeList.get(j);
						if(block.getUpLimit()==freeBlock.getDownLimit()){
							int up = freeBlock.getUpLimit();
							int size = block.Size();
							int freesize = freeBlock.Size();
							merge = true;
							freeList.remove(j);
							freeList.add(j,new Block(up,size+freesize ));
							System.out.println("");
						}
						else if(block.getDownLimit()==freeBlock.getUpLimit()){
							int up = block.getUpLimit();
							int size = block.Size();
							int freesize = freeBlock.Size();
							merge = true;
							freeList.remove(j);
							freeList.add(j,new Block(up, freesize+size));
							System.out.println("2");
						}

					}
					if(!merge){
						freeList.add(new Block(block.getUpLimit(), block.Size()));
						System.out.println("helloworld");
						
					}					
					useList.remove(i);
					text.append("��ҵ"+block.getId()+"�ͷ�"+block.Size()+"\n");
					break;				
				}
			}
		}
		
	}
	//�����㷨
	void sortList(){
		int num = freeList.size();
		for(int i = 0;i<num;i++){
			for(int j = i+1 ;j<num;j++){
				Block temp = (Block) freeList.get(i);
				Block temp2 = (Block) freeList.get(j);
				if(temp.Size()>temp2.Size()){
					int size = temp.Size();
					int up = temp.getUpLimit();
					freeList.remove(i);
					freeList.add(i,new Block(temp2.getUpLimit(), temp2.Size()));
					freeList.remove(j);
					freeList.add(j,new Block(up, size));
				}
			}
		}
	}
	private void autorun(){
		alloc(1);
		repaint();
		sleep(2000)	;
		alloc(2);
		repaint();
		sleep(2000)	;
		alloc(3);
		repaint();
		sleep(2000)	;
		free(2);
		repaint();
		sleep(2000);
		alloc(4);
		repaint();
		sleep(2000)	;
		free(3);
		repaint();
		sleep(2000)	;
		free(1);
		repaint();
		sleep(2000)	;
		alloc(5);
		repaint();
		sleep(2000)	;
		alloc(6);
		repaint();
		sleep(2000)	;
		alloc(7);
		repaint();
		sleep(2000);
		free(6);
		repaint();
		sleep(2000);
	}
	public void allocit(int i){
		alloc(i);
		repaint();
	}
	public void freeit(int i){
		free(i);
		repaint();
	}

	public void setAuto(int i){
		if(i==0) isAuto = true;
		else isAuto = false;
	}



}
//���ڴ洢�Ѿ��������Ͻ�ʹ�С
class Block{
	private int upLimit;
	private int size;
	private int id;
	public Block(int upLimit,int size){
		this.upLimit = upLimit;
		this.size = size;
	}
	public Block(int id,int upLimit,int size){
		this.upLimit = upLimit;
		this.size = size;
		this.id = id;
	}
	int getUpLimit(){
		return upLimit;
	}
	int getDownLimit(){
		return upLimit+size;
	}
	int Size(){
		return size;
	}
	int getId(){
		return id;
	}
}
//�������ڴ�������id����С
class Task{
	private int id,size;
	public Task(int id,int size){
		this.id = id;
		this.size = size;
	}
	int Size(){
		return size;
	}
	int getId(){
		return id;
	}
}

