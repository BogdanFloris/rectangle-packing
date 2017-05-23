import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Job Savelsberg on 7-5-2017.
 */
public class BoxPane extends JPanel {
    private Frame frame;
    private JButton fill;
    private int width;
    private int height;
    private int bottom;

    private Result result = null;
    private double scale = 1;

    private final int fontSize = 16;
    private Font font = new Font("Arial", Font.BOLD, fontSize);

    private int mouseX = 0;
    private int mouseY = 0;

    public boolean showIndexes = false;
    private PackingRectangle hover = null;

    public BoxPane(Frame frame){
        this.frame = frame;
        height = Frame.height-50;
        width = height;
        bottom = height;
        this.setSize(width,height);
        setVisible(true);
        repaint();

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (result != null) {
                    mouseX = e.getX();
                    mouseY = bottom-e.getY();
                    if (mouseX > 0 && mouseX < scale(result.width) && mouseY > 0 && mouseY < scale(result.height)) {
                        for (PackingRectangle r : result.rectangles) {
                            if (mouseX > scale(r.x) && mouseX < scale(r.x + r.getWidth())) {
                                if (mouseY > scale(r.y) && mouseY < scale(r.y + r.getHeight())) {
                                    if (!r.equals(hover)) {
                                        drawRectangles(getGraphics());
                                        drawRectangle(r, getGraphics(), r.getColor().brighter());
                                        hover = r;
                                        frame.setHoverInfo(hover);
                                    }
                                }
                            }
                        }
                    } else {
                        if (hover != null) {
                            hover = null;
                            drawRectangles(getGraphics());
                        }
                    }
                }
            }
        });
    }

    public void drawResult(Result result){
        setResult(result);
        paintComponent(this.getGraphics());
    }



    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,getWidth(),getHeight());
        if(result!=null){
            drawing(g);
        }
    }

    public void drawing(Graphics g){
        int bHeight = scale(result.height);
        int bWidth = scale(result.width);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,bottom-bHeight,bWidth,bHeight);
        g.setColor(Color.WHITE);
        g.drawRect(0,bottom-bHeight,bWidth,bHeight);

        int i = 0;
        for(double x = 0; x < bWidth; x+=scale){

            if(i%5==0){
                g.setColor(Color.WHITE);
                g.fillRect((int)x-1,bottom-bHeight,1,bHeight);
            }else{
                g.setColor(Color.GRAY);
                g.drawLine((int)x,bottom,(int)x,bottom-bHeight);
            }
            i++;
        }
        i = 0;
        for(double y = bottom; y > bottom-bHeight; y-=scale){
            if(i%5==0){
                g.setColor(Color.WHITE);
                g.fillRect(0,(int)y-1,bWidth,1);
            }else{
                g.setColor(Color.GRAY);
                g.drawLine(0,(int)y,bWidth,(int)y);
            }
            i++;
        }

        drawRectangles(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public void setResult(Result result) {
        this.result = result;
        scale = (double)width/(double)result.width;
        if(scale(result.height)>height){
            scale = (double)height/(double)result.height;
        }
    }

    private void drawRectangles(Graphics g){
        for(PackingRectangle r: result.rectangles){
            drawRectangle(r,g, r.getColor());
        }
    }

    public void drawRectangle(PackingRectangle r, Graphics g, Color color){
        int rWidth = scale(r.getWidth());
        int rHeight = scale(r.getHeight());

        g.setColor(color);
        g.fillRect(scale(r.x),bottom-scale(r.y)-rHeight,rWidth,rHeight);
        g.setColor(g.getColor().darker());
        g.drawRect(scale(r.x),bottom-scale(r.y)-rHeight,rWidth,rHeight);
        g.setColor(Color.WHITE);
        g.setFont(font);
        if(showIndexes){
            g.drawString(Integer.toString(r.index),scale(r.x)+rWidth/2-5,bottom-scale(r.y)-rHeight/2+fontSize/2);
        }
    }

    public int scale(int value){
        return (int)(value*scale);
    }

    public void reload(){
        repaint();
    }
}
