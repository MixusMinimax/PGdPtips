package pgdp.datastructures;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SimplePaint implements Runnable
{
    private static final int pixelSize = 32;
    private static final int imgSize = 8;

	public static void main(String[] argv)
	{
		SwingUtilities.invokeLater(new SimplePaint());
	}

	public static QuadTreeKnoten buildFromImage(BufferedImage image)
	{
		if (image == null)
		{
			throw new IllegalArgumentException("Image cannot be null");
		}
		int maxDimensions = Math.max(image.getHeight(), image.getWidth());
		int dimensions = 1;
		while (dimensions < maxDimensions)
		{
			dimensions *= 2;
		}
		int[][] imageData = new int[dimensions][dimensions];
		for (int i = image.getMinX(); i < image.getWidth(); i++)
		{
			for (int j = image.getMinY(); j < image.getHeight(); j++)
			{
				// Remove alpha channel if it exists
				imageData[j][i] = image.getRGB(i, j) % 0x1000000;
			}
		}
		return QuadTreeKnotenImpl.buildFromIntArray(imageData);
	}

	@Override public void run()
	{
		final JFrame window = new JFrame("Praktikum Grundlagen der Pinguine");
		// Build the paint component
		final PaintPanel paint = new PaintPanel(QuadTreeKnotenImpl.buildFromIntArray(new int[imgSize][imgSize]));
		window.add(paint, BorderLayout.CENTER);

		// Build the menu bar
		final JMenuBar menuBar = new JMenuBar();
		final JMenu menuFile = new JMenu("File");
		final JMenuItem openMenuItem = new JMenuItem("Open");
		final JMenu menuInfo = new JMenu("Info");
		final JMenuItem infoMenuItem = new JMenuItem("Show Info");
		final JCheckBoxMenuItem showBordersMenuItem = new JCheckBoxMenuItem("Show QuadTreeKnoten borders");
		final JFileChooser fc = new JFileChooser();

		openMenuItem.addActionListener(e -> {
			int returnVal = fc.showOpenDialog(window);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File f = fc.getSelectedFile();
				try
				{
					paint.setQuadTree(buildFromImage(ImageIO.read(f)));
					window.pack();
					window.repaint();
				} catch (IOException e1)
				{
					JOptionPane.showMessageDialog(window, "IOException: " + e);
				} catch (IllegalArgumentException n)
				{
					JOptionPane.showMessageDialog(window, "ImageIO: Error reading image file.");
				}
			}
		});
		menuFile.add(openMenuItem);
		infoMenuItem.addActionListener(e -> JOptionPane.showMessageDialog(window,
				"Info:\n" + "Image Size: " + paint.getQuadTree().getDimension() + "x" + paint.getQuadTree()
						.getDimension() + "\n" + "Bytes: " + paint.getQuadTree().getDimension() * paint.getQuadTree()
						.getDimension() + "\n" + "Nodes Used: " + paint.getQuadTree().getSize() + "\n"
						+ "Compression Ratio: " + String.format("%1.4f", paint.getQuadTree().getCompressionRatio())));
		showBordersMenuItem.setState(false);
		showBordersMenuItem.addActionListener(e -> paint.setShowNodeBorders(showBordersMenuItem.getState()));
		menuInfo.add(infoMenuItem);
		menuInfo.add(showBordersMenuItem);
		menuBar.add(menuFile);
		menuBar.add(menuInfo);
		window.setJMenuBar(menuBar);

		// Configure the window
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.setVisible(true);

		window.repaint();
	}

	private static class PaintPanel extends Component implements MouseListener, MouseMotionListener
	{
		/**
		 * Serial version UID
		 */
		private static final long serialVersionUID = -2271950113761629787L;

		private static final Color BACKGROUND = Color.BLACK;
		private int currentColor = 0xffffff;
		private QuadTreeKnoten tree;
		private int width;
		private int height;
		private boolean showNodeBorders = false;
		private int prevX = -1, prevY = -1;

		public PaintPanel(QuadTreeKnoten backingInstance)
		{
			this.width = backingInstance.getDimension() * pixelSize;
			this.height = backingInstance.getDimension() * pixelSize;
			this.tree = backingInstance;
			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		public void setShowNodeBorders(boolean show)
		{
			this.showNodeBorders = show;
			this.repaint();
		}

		public QuadTreeKnoten getQuadTree()
		{
			return this.tree;
		}

		public void setQuadTree(QuadTreeKnoten qt)
		{
			this.tree = qt;
			this.width = qt.getDimension();
			this.height = qt.getDimension();
			this.repaint();
		}

		@Override public Dimension getPreferredSize()
		{
			return new Dimension(this.width, this.height);
		}

		@Override public void update(Graphics g)
		{
			paint(g);
		}

		@Override public void paint(Graphics g)
		{
			g.setColor(BACKGROUND);
			g.fillRect(0, 0, width, height);
			this.recursivePaint(g, this.tree, 0, 0);
		}

		private void recursivePaint(Graphics g, QuadTreeKnoten n, int x, int y)
		{
			if (n.isLeaf())
			{
				g.setColor(new Color(n.getRelativeColor(0, 0)));
				g.fillRect(x * pixelSize, y * pixelSize, n.getDimension() * pixelSize, n.getDimension() * pixelSize);
				if (showNodeBorders)
				{
					g.setColor(Color.RED);
					g.drawRect(x * pixelSize, y * pixelSize, n.getDimension() * pixelSize,
							n.getDimension() * pixelSize);
				}
			} else
			{
				int midPoint = n.getDimension() / 2;
				recursivePaint(g, n.getTopLeft(), x, y);
				recursivePaint(g, n.getTopRight(), x + midPoint, y);
				recursivePaint(g, n.getBottomLeft(), x, y + midPoint);
				recursivePaint(g, n.getBottomRight(), x + midPoint, y + midPoint);
			}
		}

		@Override public void mouseClicked(MouseEvent e)
		{
		}

		@Override public void mouseEntered(MouseEvent arg0)
		{
		}

		@Override public void mouseExited(MouseEvent arg0)
		{
		}

		@Override public void mousePressed(MouseEvent e)
		{
		}

		@Override public void mouseReleased(MouseEvent e)
		{
			prevX = -1;
			prevY = -1;
		}

		@Override public void mouseDragged(MouseEvent e)
		{
			if (SwingUtilities.isLeftMouseButton(e))
			{
				this.currentColor = 0xffffff;
			} else
			{
				this.currentColor = 0;
			}

			if (prevX < 0 || prevY < 0)
			{
				prevX = e.getX() / pixelSize;
				prevY = e.getY() / pixelSize;
				return;
			}
			this.drawLine(prevX, prevY, e.getX() / pixelSize, e.getY() / pixelSize);
			this.repaint();
			prevX = e.getX() / pixelSize;
			prevY = e.getY() / pixelSize;
		}

		@Override public void mouseMoved(MouseEvent e)
		{
		}

		private void drawLine(int fromX, int fromY, int toX, int toY)
		{
			fromX = Math.min(Math.max(0, fromX), this.tree.getDimension() - 1);
			fromY = Math.min(Math.max(0, fromY), this.tree.getDimension() - 1);
			toX = Math.min(Math.max(0, toX), this.tree.getDimension() - 1);
			toY = Math.min(Math.max(0, toY), this.tree.getDimension() - 1);
			int deltaX = toX - fromX;
			int deltaY = toY - fromY;
			if (deltaX == 0)
			{
				for (int y = fromY; toY > fromY ? y <= toY : y >= toY; )
				{
					this.tree.setRelativeColor(fromX, y, this.currentColor);
					if (toY > fromY)
					{
						y++;
					} else
					{
						y--;
					}
				}
				return;
			}
			float error = 0;
			float deltaError = Math.abs((float) deltaY / deltaX);
			int y = fromY;
			for (int x = fromX; toX > fromX ? x <= toX : x >= toX; )
			{
				this.tree.setRelativeColor(x, y, this.currentColor);
				error += deltaError;
				while (error >= 0.5)
				{
					this.tree.setRelativeColor(x, y, this.currentColor);
					y = Math.min(Math.max(y + (toY > fromY ? 1 : -1), 0), this.tree.getDimension() - 1);
					error -= 1.0;
				}
				if (toX > fromX)
				{
					x++;
				} else
				{
					x--;
				}
			}
		}
	}
}