import net.glxn.qrgen.javase.QRCode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DataPanel extends JPanel {
    private DataFrame daddyFrame;

    public static final String ERROR_MSG = "A bug occurred. Please file an issue on Github.";

    public static final int TOP_LEFT_CORNER = 0;
    public static final int TOP_RIGHT_CORNER = 1;
    public static final int BOTTOM_LEFT_CORNER = 2;
    public static final int BOTTOM_RIGHT_CORNER = 3;

    private static int count = 0;

    private JButton openFileButton;
    private JButton[] cornerButtons;
    private JTextField enterText;
    private JButton enterButton;
    private JButton restart;

    protected boolean fileOpened;
    protected boolean cornerOpened;
    protected boolean enterOpened;
    protected String text;

    public DataPanel(DataFrame daddyFrame) {
        this.daddyFrame = daddyFrame;

        fileOpened = false;
        cornerOpened = false;
        enterOpened = false;

        setLayout(null);
        setPreferredSize(new Dimension(1000, 1000));

        openFileButton = new JButton("Open File");
        openFileButton.addActionListener(new OpenFileDialog(this));
        add(openFileButton);
        openFileButton.setBounds(150, 50, 250, 25);

        enterText = new JTextField();
        enterText.setBounds(150, 275, 500, 25);
        enterText.setVisible(false);
        add(enterText);
        enterButton = new JButton("Enter");
        enterButton.setBounds(675, 275, 100, 25);
        enterButton.addActionListener(new EnterButtonListener(this, enterText));
        enterButton.setVisible(false);
        add(enterButton);

        restart = new JButton("Click here to stamp another document");
        restart.setBounds(150, 500, 350, 25);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                daddyFrame.setVisible(false);
                new DataFrame();
            }
        });
        restart.setVisible(false);
        add(restart);

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(new Font("Liberation Serif", Font.PLAIN, 20));

        g.drawString("Open a file to upload it and stamp a QR code onto it", 100, 100);

        if (fileOpened) {
            g.drawString("File Successfully Uploaded.", 100, 200);
            g.drawString("Click the button again to choose another file instead", 100, 225);
            g.drawString("Once you're done, enter the text you want in the QR code below", 100, 250);
            cornerButtons = new JButton[4];

            enterButton.setVisible(true);
            enterText.setVisible(true);

            if (enterOpened) {
                g.drawString("Please select the corner on which to stamp the QR code onto the document", 100, 350);

                for (int i = 0; i < 4; i++) {
                    String buttonTxt = "";
                    if (i == 0) buttonTxt = "Top Left";
                    if (i == 1) buttonTxt = "Top Right";
                    if (i == 2) buttonTxt = "Bottom Left";
                    if (i == 3) buttonTxt = "Bottom Right";
                    cornerButtons[i] = new JButton(buttonTxt);
                    cornerButtons[i].addActionListener(new CornerButtonListener(i, this));
                    cornerButtons[i].setBounds(100 + (200 * i), 375, 200, 25);
                    add(cornerButtons[i]);
                    cornerButtons[i].setVisible(true);
                    repaint();
                    revalidate();
                }

                if (cornerOpened) {
                    g.drawString("Successfully stamped to your Documents folder as StampedDocument.pdf. You can close this window now. Or...", 100, 450);
                    restart.setVisible(true);
                    repaint();
                }
            }
        }
    }

    public void createFiles(File pdfFile) {
        if (!fileOpened) return;
        try {
            FileOutputStream fos = new FileOutputStream("originalPDF.pdf");
            FileInputStream fis = new FileInputStream(pdfFile);
            byte[] buf = new byte[1024];
            int hasRead = 0;
            while ((hasRead = fis.read(buf)) > 0) {
                fos.write(buf, 0, hasRead);
            }
            fis.close();
            fos.close();
        } catch (IOException error) {
            System.out.println(ERROR_MSG);
            error.printStackTrace();
        }
    }

    public void stampFiles(int chosenCorner){
        //TODO: stamp the qrCode onto the pdfFile

        //generate QR code and store as qrCode.png
        try {
            ByteArrayOutputStream inputStream = QRCode.from(text).stream();
            FileOutputStream outputStream = new FileOutputStream("qrCode.png");
            outputStream.write(inputStream.toByteArray());
        } catch (IOException error) {
            System.out.println("A bug occurred. Please file an issue on Github.");
        }

        float x = 0, y = 0, width = 0, height = 0;
        if (chosenCorner == BOTTOM_LEFT_CORNER) {
            x = 10;
            y = 10;
            height = 100;
            width = 100;
        } else if (chosenCorner == BOTTOM_RIGHT_CORNER) {
            x = 500;
            y = 10;
            height = 100;
            width = 100;
        } else if (chosenCorner == TOP_LEFT_CORNER) {
            x = 10;
            y = 700;
            height = 100;
            width = 100;
        } else if (chosenCorner == TOP_RIGHT_CORNER) {
            x = 500;
            y = 700;
            height = 100;
            width = 100;
        } else {
            System.out.println(ERROR_MSG);
            return;
        }

        //using the chosenCorner variable and the constants, choose where to stamp the qr code onto the pdf
        try {
            PDDocument doc = PDDocument.load(new File("originalPDF.pdf"));
            PDPage firstPage = doc.getPage(0);
            PDImageXObject qrImg = PDImageXObject.createFromFile("qrCode.png", doc);
            PDPageContentStream contentStream = new PDPageContentStream(doc, firstPage, PDPageContentStream.AppendMode.APPEND, false);
            contentStream.drawImage(qrImg, x, y, height, width);
            contentStream.close();
            String toAdd = "("+count+")";
            if (count++==0) toAdd = "";
            doc.save(FileSystemView.getFileSystemView().getHomeDirectory()+"/Documents/StampedDocument"+toAdd+".pdf");
            doc.close();
            cornerOpened = true;
        } catch (IOException error) {
            System.out.println(ERROR_MSG);
            error.printStackTrace();
        }
    }
}

class OpenFileDialog implements ActionListener {
    private DataPanel daddyPanel;
    public OpenFileDialog(DataPanel daddyPanel) {
        this.daddyPanel = daddyPanel;
    }
    public void actionPerformed(ActionEvent e) {
        new FileDialog(daddyPanel);
    }
}

class CornerButtonListener implements ActionListener {
    private int cornerConstant;
    private DataPanel daddyPanel;
    public CornerButtonListener(int i, DataPanel daddyPanel) {
        cornerConstant = i;
        this.daddyPanel = daddyPanel;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        daddyPanel.stampFiles(cornerConstant);
    }
}

class EnterButtonListener implements ActionListener {
    private DataPanel daddyPanel;
    private JTextField field;
    public EnterButtonListener(DataPanel dad, JTextField field) {
        daddyPanel = dad;
        this.field = field;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        daddyPanel.text = field.getText();
        daddyPanel.enterOpened = true;
        daddyPanel.repaint();
    }
}