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

public class MonitorUI extends JFrame {

	ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(5);
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private static MonitorUI uiFactory;
	
	private JPanel panel_1;
	private JButton movingButton;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception{
		final MonitorUI frame = new MonitorUI();
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
	public MonitorUI() throws IOException {
		setTitle("Source Monitor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 762, 493);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 49, 725, 395);
		contentPane.add(scrollPane);
		
		JButton createTargetButton = new JButton("新　增");
		createTargetButton.setBounds(10, 10, 87, 23);
		contentPane.add(createTargetButton);
		
		JButton getTargetButton = new JButton("取得個別詳情");
		getTargetButton.setBounds(344, 10, 115, 23);
		contentPane.add(getTargetButton);
		
		JButton updateTargetButton = new JButton("修　改");
		updateTargetButton.setBounds(121, 10, 87, 23);
		contentPane.add(updateTargetButton);
		
		JButton deleteTargetButton = new JButton("刪　除");
		deleteTargetButton.setBounds(232, 10, 87, 23);
		contentPane.add(deleteTargetButton);
		
		JButton syncronizeButton = new JButton("同　步");
		syncronizeButton.setToolTipText("同步設定至檔案");
		syncronizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		syncronizeButton.setBounds(481, 10, 87, 23);
		contentPane.add(syncronizeButton);

		
		
	}
}
