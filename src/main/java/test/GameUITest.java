package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import javax.swing.JMenuItem;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;

public class GameUITest extends JFrame {

	/**
	 * 
	 */
	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static GameUITest uiFactory;
	private JTextField jTextField;
	private JLabel jLabel;
	private JPanel panel;
	
	private JPanel panel_1;
	private JButton movingButton;
	private JTextField textField;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception{
		final GameUITest frame = new GameUITest();
		uiFactory = frame;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
//		Thread movinThread,tempThread;
//		while(true){
//			movinThread = new Thread(new Runnable() {
//				Rectangle panelBounds = uiFactory.panel_1.getBounds();
//				Random random = new Random();
//				Double height,width,x,y;
//				boolean executed;
//				@Override
//				public void run() {
//					executed = false;
//					width = panelBounds.getWidth();
//					x = panelBounds.getX() + random.nextInt(width.intValue());
//					height = panelBounds.getHeight();
//					y = panelBounds.getY() + random.nextInt(height.intValue());
//					uiFactory.movingButton.setBounds(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//					uiFactory.panel_1.repaint();
//					executed = true;
//				}
//			});
//			movinThread.setName("mt");
//			movinThread.run();
//			if((tempThread = Thread.currentThread()).getName().equals("mt")){
//				tempThread.sleep(2000);
//			}
//			
//		}
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					Rectangle panelBounds = uiFactory.panel_1.getBounds();
//					Random random = new Random();
//					Double height,width,x,y;
//					while(true){
//						width = panelBounds.getWidth();
//						x = panelBounds.getX() + random.nextInt(width.intValue());
//						height = panelBounds.getHeight();
//						y = panelBounds.getY() + random.nextInt(height.intValue());
//						uiFactory.movingButton.setBounds(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//						uiFactory.panel_1.repaint();
//						Thread.currentThread().sleep(2000);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}
	
	private class MovingThread extends Thread {
		
		Rectangle panelBounds;
		Random random = new Random();
		Double height,width,x,y;
		
		@Override
		public void run(){
			panelBounds = uiFactory.panel_1.getBounds();
			width = panelBounds.getWidth();
			x = random.nextInt(width.intValue())*1d;
			height = panelBounds.getHeight();
			y = random.nextInt(height.intValue())*1d;
			uiFactory.movingButton.setBounds(x.intValue(), y.intValue(), uiFactory.movingButton.getWidth(), uiFactory.movingButton.getHeight());
			uiFactory.panel_1.repaint();
		}
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public GameUITest() throws IOException {
		setTitle("Giant Kill");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 902, 479);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menu_file = new JMenu("| 檔　案 |");
		menuBar.add(menu_file);
		
		JMenuItem mi_file_create = new JMenuItem("新增檔案");
		menu_file.add(mi_file_create);
		
		JMenuItem mi_file_open = new JMenuItem("開啟檔案");
		mi_file_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
	            chooser.setCurrentDirectory(new File("C:/"));
	            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//	             chooser.setAcceptAllFileFilterUsed(false);
	            if (chooser.showOpenDialog(GameUITest.this) == JFileChooser.OPEN_DIALOG) {
	            	System.out.println("file opened");
	            //do when open
	            } else {
	            	System.out.println("file open fails");
	                // do when cancel
	            }
			}
		});
		menu_file.add(mi_file_open);
		
		JMenuItem mi_file_save = new JMenuItem("儲存檔案");
		menu_file.add(mi_file_save);
		
		JMenu menu_edit = new JMenu("| 編　輯 |");
		menuBar.add(menu_edit);
		
		JMenu menu = new JMenu("| 說　明 |");
		menuBar.add(menu);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		// 取得滑鼠點擊的位置，把元件移動到滑鼠點擊的地方
		panel.setLayout(null);
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("x:" + e.getX() + ",y:" + e.getY());
				if (jLabel != null) {
					panel.remove(jLabel);
				}

				jLabel = new JLabel("●");
				panel.add(jLabel);
				jLabel.setBounds(e.getX(), e.getY(), 30, 30);
				panel.repaint();
//				uiFactory.setVisible(true);

			}
		});
		
		// 透過鍵盤操控元件移動
		InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE ,0 ), "doSomething");
		panel.getActionMap().put("doSomething", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jLabel.setBounds(jLabel.getX(), jLabel.getY()-10, jLabel.getBounds().width, jLabel.getBounds().height);
				panel.repaint();
//				uiFactory.setVisible(true);
			}
		});
		panel.setBounds(10, 120, 486, 311);
		contentPane.add(panel);
		
		// image
		String path = "./src/main/java/game/img/swordsman.png";
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        JLabel label = new JLabel(new ImageIcon(image));
        panel.add(label);
        label.setBounds(0, 0, image.getWidth(), image.getHeight());
        panel.repaint();

		JButton btnNewButton_2 = new JButton("New button");
		btnNewButton_2.setBounds(165, 6, 87, 23);
		panel.add(btnNewButton_2);
		JButton btnNewButton = new JButton("button1");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				jTextField = new JTextField("Hi there!");
				jTextField.setEnabled(false);
				panel.add(jTextField);
//				jTextField.setBounds(50, 50, 50, 50);
//				panel.repaint();
				jTextField.getParent().repaint();
				uiFactory.setVisible(true);

			}
		});
		btnNewButton.setBounds(33, 75, 87, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("button2");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				// jTextField = new JTextField("I am Harvey!");
				// jTextField.setEnabled(false);
				// panel.add(jTextField);
				panel.repaint();
				// contentPane.repaint();
				uiFactory.setVisible(true);
			}
		});
		btnNewButton_1.setBounds(154, 75, 87, 23);
		contentPane.add(btnNewButton_1);
		
		panel_1 = new JPanel();
		panel_1.setBounds(284, 10, 408, 98);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		movingButton = new JButton("Click me");
		movingButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clicked!");
			}
		});
		movingButton.setBounds(166, 5, 85, 23);
		panel_1.add(movingButton);
		
		JButton startButton = new JButton("Start!");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread movinThread = uiFactory.new MovingThread();
				stpe.scheduleAtFixedRate(movinThread, 1000, 2000, TimeUnit.MILLISECONDS);
			}
		});
		startButton.setBounds(154, 10, 87, 23);
		contentPane.add(startButton);
		
		JButton endButton = new JButton("End!");
		endButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stpe.shutdownNow();
			}
		});
		endButton.setBounds(154, 42, 87, 23);
		contentPane.add(endButton);
		
		JLabel lblNewLabel = new JLabel("New label");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		lblNewLabel.setBounds(33, 14, 46, 15);
		contentPane.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setForeground(Color.WHITE);
		textField.setBackground(Color.BLACK);
		textField.setBounds(729, 14, 96, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setOpaque(false);
		panel_2.setBounds(506, 120, 143, 125);
		contentPane.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(694, 142, 131, 54);
		contentPane.add(scrollPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(592, 286, 110, 71);
		contentPane.add(tabbedPane);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(700, 236, 146, 14);
		contentPane.add(progressBar);
		
		System.out.println(panel_2.getBounds().getMaxX());
		System.out.println(panel_2.getBounds().getMaxY());
		System.out.println(panel_2.getBounds().getCenterX());
		System.out.println(panel_2.getBounds().getMinX());
		
	}
}
