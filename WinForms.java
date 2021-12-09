/*
 * Ну вы же понимаете, что код здесь только мой?
 * Well, you do understand that the code here is only mine?
 */
package test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author LWJGL2
 */
public class WinForms extends JPanel {

    private JFrame frame;
    private BufferedImage img;

    public WinForms() throws Exception {
        { // init panel
            BufferedImage in = ImageIO.read(getClass().getResourceAsStream("/test/PNG_transparency_demonstration_1.png")); // Грузим пикчу
            img = in;
            {
                JButton button = new JButton("Click me");
                button.addActionListener((e) -> {
                    long lastTime = System.nanoTime();
                    for (int i = 0; i < 1000; i++) {
                        {
                            img = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            FastRGB data = new FastRGB(in);

                            for (int x = 0; x < img.getWidth(); x++) {
                                for (int y = 0; y < img.getHeight(); y++) {
                                    short[] pixel = data.getRGB(x, y);

                                    int r = 255;//pixel[0];
                                    int g = pixel[1];
                                    int b = pixel[2];
                                    int a = pixel[3];

                                    int rgb =
                                            ((a & 0xFF) << 24) |
                                            ((r & 0xFF) << 16) |
                                            ((g & 0xFF) << 8) |
                                            ((b & 0xFF) << 0);
                                    img.setRGB(x, y, rgb);
                                }
                            }
                        }
                    }
                    long curTime = System.nanoTime();
                    long da = (curTime - lastTime) / 1_000_000;
                    System.out.println("Time: " + da + " ms");

                    try {
                        // Если нужно записать пикчу
                        ImageIO.write(img, "PNG", new java.io.File("output.png"));
                    } catch (Exception otvalException) {
                        otvalException.printStackTrace();
                    }

                    repaint(); // Перерисовка канваса
                });
                add(button);
            }
        }
        frame = new JFrame(getClass().getName());
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

    public int width;
    public int height;
    private boolean hasAlphaChannel;
    private int pixelLength;
    private byte[] pixels;

    public FastRGB(BufferedImage image) {
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = 3;
        if (hasAlphaChannel) {
            pixelLength = 4;
        }
    }

    public short[] getRGB(int x, int y) {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        short rgb[] = new short[4];
        if (hasAlphaChannel) {
            rgb[3] = (short) (pixels[pos++] & 0xFF); // Alpha
        }
        rgb[2] = (short) (pixels[pos++] & 0xFF); // Blue
        rgb[1] = (short) (pixels[pos++] & 0xFF); // Green
        rgb[0] = (short) (pixels[pos++] & 0xFF); // Red
        return rgb;
    }
}
