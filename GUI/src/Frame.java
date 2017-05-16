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
        getFiles();
        initUI();
    }

    public void getFiles(){
        results.clear();
        for(File f: new File("../Algorithm/src/tests/").listFiles()){
            if(f.toString().contains(".out")){
                if(Result.isResult(f)){
                    results.add(new Result(f));
                }
            }
        }
    }

    private Font font;
    private static JLabel info, info2, hoverInfo, hoverInfo2, generateFixedHeightL,
            generateHeightL,generateWidthL,generateRotationsL, generateMinSizeL,generateMaxSizeL, generateNL, generateNameL;
    private JButton generate;
    private JTextArea codeArea;
    private JTextField generateHeight, generateWidth, generateMinSize, generateMaxSize, generateN, generateIterations, generateName;
    private JComboBox resultPicker, generatePicker, generateRotations, generateFixedHeight;
    private BoxPane boxPane;

    private Generator gen;

    private void initUI() {
        setTitle("Rectangle Packing Algorithm");
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        font = new Font("Arial", Font.PLAIN, 16);
        DecimalFormat df = new DecimalFormat("#.##");
        gen = new Generator();

        //Left box panel
        JPanel p = new JPanel(new BorderLayout());
        boxPane = new BoxPane();
        p.setBounds(0,0,height-60,height);
        p.add(boxPane);
        this.add(p);

        //Middle panel (Picker & Code)
        JPanel p2 = new JPanel(null);
        p2.setBounds(height-50,0,240,height);

        resultPicker = new JComboBox(results.toArray());
        resultPicker.addActionListener(this);
        resultPicker.setBounds(0,10,240,30);
        p2.add(resultPicker);

        codeArea = new JTextArea(30,18);
        JScrollPane scrollPane = new JScrollPane( codeArea );
        scrollPane.setBounds(0, 50, 240, height-50);
        p2.add(scrollPane);

        this.add(p2);

        //Right panel (Info & Generator)
        JPanel p3 = new JPanel(new GridLayout(2,2));
        p3.setBounds(height+200,0,width-(height+200), 300);
        info = new JLabel();
        info.setFont(font);
        p3.add(info);

        info2 = new JLabel();
        info2.setFont(font);
        p3.add(info2);

        hoverInfo = new JLabel();
        hoverInfo.setFont(font);
        p3.add(hoverInfo);

        hoverInfo2 = new JLabel();
        hoverInfo2.setFont(font);
        p3.add(hoverInfo2);

        this.add(p3);
        //Generator form:
        GridLayout gl = new GridLayout(10,2);
        gl.setVgap(20);
        gl.setHgap(10);
        JPanel p4 = new JPanel(gl);
        p4.setBounds(height+200,300,width-(height+220),height-300);


        generate = new JButton();
        generate.setText("Generate");
        generate.addActionListener(this);
        p4.add(generate);

        generatePicker = new JComboBox(new String[]{"Simple","Random","Increasing","oriented equal-perimeter",
        "unoriented double-perimeter"});
        generatePicker.addActionListener(this);
        p4.add(generatePicker);

        generateRotationsL = new JLabel("Rotations allowed: ");
        p4.add(generateRotationsL);
        generateRotations = new JComboBox(new String[]{"no","yes"});
        p4.add(generateRotations);

        generateFixedHeightL = new JLabel("Height fixed: ");
        p4.add(generateFixedHeightL);
        generateFixedHeight = new JComboBox(new String[]{"no","yes"});
        p4.add(generateFixedHeight);

        generateNL = new JLabel("No. rectangles: ");
        p4.add(generateNL);
        generateN = new JTextField("25");
        p4.add(generateN);

        generateHeightL = new JLabel("Height: ");
        p4.add(generateHeightL);
        generateHeight = new JTextField("50");
        p4.add(generateHeight);

        generateWidthL = new JLabel("Width: ");
        p4.add(generateWidthL);
        generateWidth = new JTextField("50");
        p4.add(generateWidth);

        generateMinSizeL = new JLabel("Minimum size: ");
        p4.add(generateMinSizeL);
        generateMinSize = new JTextField("4");
        p4.add(generateMinSize);

        generateMaxSizeL = new JLabel("Maximum size: ");
        p4.add(generateMaxSizeL);
        generateMaxSize = new JTextField("16");
        p4.add(generateMaxSize);

        generateNameL = new JLabel("File name: ");
        p4.add(generateNameL);
        generateName = new JTextField(""+(int)Math.round(Math.random()*99999));
        p4.add(generateName);

        this.add(p4);

        setVisible(true);


        setResult(results.get(0));

        /*Result simple = gen.fillRandom(false,false,40,40, 2, 10,1000000);
        simple.writeFile("simple");

        setResult(simple);


        Test simpleTest = gen.generateRandom(5);
        simpleTest.writeFile("simpleTest");

        //setResult(simple);*/
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
        if(e.getSource()==resultPicker){
            setResult((Result)resultPicker.getSelectedItem());
        }else if(e.getSource()==generate){
            boolean hf = generateFixedHeight.getSelectedItem().equals("yes")?true:false;
            boolean ra = generateRotations.getSelectedItem().equals("yes")?true:false;
            int n = Integer.parseInt(generateN.getText());
            int w = Integer.parseInt(generateWidth.getText());
            int h = Integer.parseInt(generateHeight.getText());
            int minS = Integer.parseInt(generateMinSize.getText());
            int maxS = Integer.parseInt(generateMaxSize.getText());
            String fileName = generateName.getText();

            if(generatePicker.getSelectedItem().equals("Simple")){
                Result simple = gen.fillRandom(hf,ra,w,h, minS, maxS,1000000);
                simple.writeFile("Simple_"+fileName);
                resultPicker.addItem(simple);
                resultPicker.setSelectedItem(simple);
                setResult(simple);
            } else if(generatePicker.getSelectedItem().equals("Increasing")){
                Test increasing = gen.generateIncreasingSquare(hf,ra, n, minS);
                increasing.writeFile("Increasing_"+fileName);
            } else if(generatePicker.getSelectedItem().equals("Random")){
                Test random = gen.generateRandom(hf,ra,n,h,minS,maxS);
                random.writeFile("Random_"+fileName);
            } else if(generatePicker.getSelectedItem().equals("oriented equal-perimeter")){
                Test orientedEqualPerimeter = gen.generateOrientedEqualPerimeter(hf, n);
                orientedEqualPerimeter.writeFile("oriented equal-perimeter_"+fileName);
            } else if(generatePicker.getSelectedItem().equals("unoriented double-perimeter")){
                Test orientedEqualPerimeter = gen.generateUnOrientedDoublePerimeter(hf, n);
                orientedEqualPerimeter.writeFile("unoriented double-perimeter_"+fileName);
            }



    }
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
