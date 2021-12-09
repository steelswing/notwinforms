/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */
package test;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author LWJGL2
 */
public class WinForms extends JPanel {

    private static final long serialVersionUID = 1L;

    private JFrame frame;
    private BufferedImage img;

    public WinForms() throws Exception {
        { // init panel
            BufferedImage in = ImageIO.read(getClass().getResourceAsStream("/test/PNG_transparency_demonstration_1.png")); // Грузим пикчу
            FastRGB data = new FastRGB(img = in);
            {
                JButton button = new JButton("Click me");
                button.addActionListener((ActionEvent e) -> {
                    long lastTime = System.nanoTime();
                    {
                        img = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        final int width = img.getWidth();
                        final int height = img.getHeight();

                        final int[] pixels = new int[width * height * 4];
                        for (int i = 0; i < 1000; i++) {
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    int pixel = data.getRGB(x, y);
                                    int pos = (y * 4 * width) + (x * 4);

                                    pixels[pos++] = pixel >> 20; // Тута должно быть 24, но для теста написаль 
                                    pixels[pos++] = pixel >> 16;
                                    pixels[pos++] = pixel >> 8;
                                    pixels[pos++] = pixel >> 0;
                                }
                            }
                        }
                        WritableRaster raster = (WritableRaster) img.getData();
                        raster.setPixels(0, 0, width, height, pixels);
                        img.setData(raster);
                    }

                    long curTime = System.nanoTime();
                    long da = (curTime - lastTime) / 1_000_000;
                    repaint(); // Перерисовка канваса
                    System.out.println("Time: " + da + " ms");
                    JOptionPane.showMessageDialog(null, "Time: " + da + " ms", "WinForms", JOptionPane.INFORMATION_MESSAGE);
                });
                add(button);
            }
            setBorder(BorderFactory.createTitledBorder("Hello World"));
        }
        frame = new JFrame(getClass().getName());

        try {
            frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/test/icon.png")));
        } catch (Exception e) {
        }
        frame.setSize(800, 600);
        frame.add(this); // Добавить это говно на фрейм
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (img == null || img.getData() == null) {
            return; // naxyi
        }

        g.drawImage(img, 20, 20, null);
    }

    public static void main(String[] args) throws Exception {
        WinForms f = new WinForms();
    }
}

/**
 * Мега класс
 *
 * @author LWJGL2
 */
class FastRGB {

    public final int width;
    public final int height;
    private final boolean hasAlphaChannel;
    private final int pixelLength;
    private final byte[] pixels;

    private final int[][] readedPixels;

    public FastRGB(BufferedImage image) {
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = hasAlphaChannel ? 4 : 3;
        readedPixels = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pos = (y * pixelLength * width) + (x * pixelLength);
                byte rgb[] = new byte[4];
                if (hasAlphaChannel) {
                    rgb[3] = (byte) (pixels[pos++] & 0xFF); // Alpha
                }
                rgb[2] = (byte) (pixels[pos++] & 0xFF); // Blue
                rgb[1] = (byte) (pixels[pos++] & 0xFF); // Green
                rgb[0] = (byte) (pixels[pos++] & 0xFF); // Red

                int v =
                        ((rgb[0] & 0xFF) << 24) |
                        ((rgb[1] & 0xFF) << 16) |
                        ((rgb[2] & 0xFF) << 8) |
                        ((rgb[3] & 0xFF) << 0);

                readedPixels[x][y] = v;
            }
        }
    }

    public int getRGB(int x, int y) {
        return readedPixels[x][y];
    }
}
