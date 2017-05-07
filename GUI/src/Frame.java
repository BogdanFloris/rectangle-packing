import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Created by Job Savelsberg on 7-5-2017.
 */
public class Frame extends JFrame implements ActionListener {
    public static ArrayList<Result> results = new ArrayList<Result>();

    public static int width = 1300;
    public static int height = 800;

    public Frame() {
        for(File f: new File("../Algorithm/src/tests/").listFiles()){
            if(f.toString().contains(".out")){
                if(Result.isResult(f)){
                    results.add(new Result(f));
                }
            }
        }
        initUI();
    }

    private Font font;
    private static JLabel info, info2, hoverInfo, hoverInfo2;
    private JTextArea codeArea;
    private JComboBox resultPicker;
    private BoxPane boxPane;


    private void initUI() {
        setTitle("Rectangle Packing Algorithm");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        font = new Font("Arial", Font.PLAIN, 20);
        DecimalFormat df = new DecimalFormat("#.##");

        boxPane = new BoxPane();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty =1;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NORTHWEST;
        this.add(boxPane,c);

        info = new JLabel();
        info.setFont(font);
        c.gridheight = 1;
        c.gridx=2;
        c.gridy=1;
        c.weightx = 1;
        c.weighty = 1;
        this.add(info,c);

        info2 = new JLabel();
        info2.setFont(font);
        c.gridx=3;
        c.gridy=1;
        c.weightx = 1;
        this.add(info2,c);

        hoverInfo = new JLabel();
        hoverInfo.setFont(font);
        c.gridy=2;
        c.gridx = 2;
        this.add(hoverInfo,c);

        hoverInfo2 = new JLabel();
        hoverInfo2.setFont(font);
        c.gridy=2;
        c.gridx = 3;
        this.add(hoverInfo2,c);

        resultPicker = new JComboBox(results.toArray());
        resultPicker.addActionListener(this);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 1;
        c.ipadx = 40;
        c.anchor = GridBagConstraints.NORTHWEST;
        this.add(resultPicker,c);

        codeArea = new JTextArea(30,18);
        c.gridx=1;
        c.gridwidth=1;
        c.gridy = 2;
        c.ipadx = 0;
        JScrollPane scrollPane = new JScrollPane( codeArea );
        this.add(scrollPane, c);

        setVisible(true);


        setResult(results.get(0));
    }

    public void setResult(Result result){
        boxPane.drawResult(result);

        info.setText("<html>"
                +"<br> Number of rectangles: "
                +"<br> Container Height: "
                +"<br> Rotations Allowed: "
                +"<br> Bounding Rectangle: "
                +"<br> Wasted Space: ");
        info2.setText("<html>"
                +"<br>"+Integer.toString(result.n)
                +"<br>" + (result.heightFixed?"fixed "+result.height:"free")
                +"<br>"+ (result.rotations?"yes":"no")
                +"<br>" + result.width + " x " +result.height
                +"<br>"+ String.format( "%.2f %%", result.wastePercentage )+"</html>");
        codeArea.setText(result.getOriginal());
    }

    public static void main(String[] args) {
        new Frame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setResult((Result)resultPicker.getSelectedItem());
    }

    public static void setHoverInfo(PackingRectangle hover) {
        hoverInfo.setText("<html>"+"Index: "
                +"<br> Dimensions: "
                +"<br> Area: "
                +"<br> Position: "
                +"<br> Rotated: "+
                "</html>");
        hoverInfo2.setText("<html>"+hover.index
                +"<br>"+hover.width+" x "+hover.height
                +"<br>"+hover.area
                +"<br>"+ hover.x + ", "+hover.y
                +"<br>"+(hover.rotated?"yes":"no")+
                "</html>");
    }
}
