package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class FileDropFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private DropTargetListener listener;
	private JTextField progressText;
	private Font font = null;
	
	public FileDropFrame(String title, DropTargetListener listener){
		super(title);
		this.listener = listener;
		loadFont();
		initUI();
	}

	private void loadFont() {
		Font ttfBase = null;
		try {
			InputStream in = getClass().getResourceAsStream(File.separator + "fonts" + File.separator + "LoveYaLikeASister.ttf");
			ttfBase = Font.createFont(Font.TRUETYPE_FONT, in);
			font = ttfBase.deriveFont(Font.PLAIN, 32);
		} catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void initUI() {
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setDropBackground();
		new DropTarget(this.getContentPane(), listener);
	}
	
	public void setProgressText(String text) {
		progressText.setText(text);
		repaint();
	}
	
	public void addProgressText() {
		progressText = new JTextField("0%");
		progressText.setSize(300, 100);
		progressText.setEditable(false);
		progressText.setHorizontalAlignment(JTextField.CENTER);
		progressText.setBorder(null);
		progressText.setForeground(new Color(255,255,255,255));
		progressText.setBounds(250, 450, 300, 100);
		progressText.setBackground(new Color(255, 255, 255, 0));
		progressText.setVisible(true);
		if(font != null) progressText.setFont(font);
		getContentPane().add(progressText);
		
		pack();
		repaint();
	}
	
	public void setDropBackground() {
		try {
			InputStream in = getClass().getResourceAsStream(File.separator + "assets" + File.separator+ "bg.png");
			BufferedImage bgImage = ImageIO.read(in);
			setContentPane(new ImagePanel(bgImage));
			pack();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setDownloadBackground() {
		try {
			InputStream in = getClass().getResourceAsStream(File.separator + "assets" + File.separator+ "downloading.png");
			BufferedImage bgImage = ImageIO.read(in);
			setContentPane(new ImagePanel(bgImage));
			pack();
			addProgressText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setFinishBackground() {
		try {
			InputStream in = getClass().getResourceAsStream(File.separator + "assets" + File.separator+ "finished.png");
			BufferedImage bgImage = ImageIO.read(in);
			setContentPane(new ImagePanel(bgImage));
			if(progressText != null) progressText.setVisible(false);
			pack();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ImagePanel extends JComponent {
		private static final long serialVersionUID = 1L;
		private Image image;
		
	    public ImagePanel(Image image) {
	        this.image = image;
	        Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
	        setPreferredSize(size);
	        setMinimumSize(size);
	        setMaximumSize(size);
	        setSize(size);
	        setLayout(null);
	    }
	    
	    @Override
	    protected void paintComponent(Graphics g) {
	        g.drawImage(image, 0, 0, null);
	    }
	}

}
