import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

public class FileDialog {
    private JFileChooser jFileChooser;
    private DataPanel daddyPanel;
     
    public FileDialog(DataPanel daddyPanel) {
        this.daddyPanel = daddyPanel;
         
        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
        jFileChooser.setFileFilter(new PdfFileFilter());
         
        int result = jFileChooser.showOpenDialog(new JFrame());
     
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();

            //perform necessary actions on the data
            daddyPanel.createFiles(selectedFile);

            daddyPanel.fileOpened = true;
            daddyPanel.repaint();
        }
    }
}

class PdfFileFilter extends FileFilter {
    public String fileExt;
    String txtExt = ".pdf";

    public PdfFileFilter() {
        this(".pdf");  //default file type extension.
    }

    public PdfFileFilter(String extension) {
        fileExt = extension;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;
        return  (f.getName().toLowerCase().endsWith(fileExt));
    }

    public String getDescription() {
        if(fileExt.equals(txtExt ))
            return  "PDF Files (*" + fileExt + ")";
        else
            return ("Other File");
    }
}