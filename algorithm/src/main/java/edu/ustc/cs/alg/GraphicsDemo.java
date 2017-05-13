package edu.ustc.cs.alg;

import edu.ustc.cs.alg.model.coordinate.Node;
import edu.ustc.cs.alg.model.edge.Edge;
import org.jgrapht.Graph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 使用Graphics类绘图
 *
 * @author 小明
 *
 */
public class GraphicsDemo extends JFrame {


    public static void generatePic(Graph<Node, Edge> graph, int width, int height){
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        for(Edge<Node> edge : graph.edgeSet() ){
            Node v = edge.getSource();
            Node w = edge.getTarget();
            graphics.setColor(Color.black);
            graphics.drawLine((int)v.getX(),(int)v.getY(),(int)w.getX(),(int)w.getY());
            try {
                ImageIO.write(image, "PNG", new File("D:\\graph.png"));//生成图片方法一
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        int imageWidth = 128;//图片的宽度
        int imageHeight = 64;//图片的高度
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();

        try
        {
            Font font=new Font("新宋体",Font.PLAIN,12);
            graphics.setFont(font);
            graphics.fillRect(0, 0, imageWidth, imageHeight);
            graphics.setColor(new Color(0,0,0));//设置黑色字体,同样可以graphics.setColor(Color.black);
            graphics.drawString("产品：深圳雅辉呼叫器", 0, 10);
            graphics.drawString("网址:www.szsyhaf.com", 0, 36);
            ImageIO.write(image, "PNG", new File("D:\\abc.png"));//生成图片方法一
            //ImageIO,可以生成不同格式的图片，比如JPG,PNG,GIF.....
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        //生成图片方法二开始,只知道生成jpg格式的图片,这个方法其他格式的还是不知道怎么弄。
        /*try {
            FileOutputStream fos = new FileOutputStream("D:\\abc.jpg");
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
            encoder.encode(image);
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        //生成图片方法二结束
        graphics.dispose();//释放资源
    }

}
class Picture extends JPanel{


    @Override
    public void paint(Graphics g) {
        g.setColor(Color.RED);
        // 画线段
        g.drawLine(5000, 5, 20, 100);
        // 画点
        g.drawLine(20, 20, 20, 20);

//        // 画普通矩形框
//        g.drawRect(30, 5, 100, 100);
//        // 填充普通矩形
//        g.fillRect(140, 5, 100, 100);
//
//        // 画圆角矩形
//        g.drawRoundRect(250, 5, 100, 100, 30, 30);
//        // 填充圆角矩形
//        g.fillRoundRect(360, 5, 100, 100, 40, 40);
//
//        // 画三维矩形
//        g.draw3DRect(5, 110, 100, 100, false);
//        // 填充三维矩形
//        g.fill3DRect(110, 110, 100, 100, true);
//
//        // 画椭圆形
//        g.drawOval(220, 110, 100, 50);
//        // 填充椭圆形
//        g.fillOval(330, 110, 30, 90);
//
//        // 画圆弧
//        g.drawArc(5, 220, 100, 100, 30, 150);
//        // 填充圆弧
//        g.fillArc(110, 220, 100, 100, 70, 220);
//
//        // 画多边形
//        int px[] = { 210, 220, 270, 250, 240 };
//        int py[] = { 220, 250, 300, 270, 220 };
//        g.drawPolygon(px, py, px.length);
//        // 填充多边形
//        int px1[] = { 310, 320, 370, 400, 340 };
//        int py1[] = { 220, 250, 300, 270, 220 };
//        g.fillPolygon(px1, py1, px.length);
//
//        // 擦除块
//        g.setColor(Color.BLUE);
//        g.fillOval(5, 330, 100, 100);
//        g.clearRect(30, 350, 30, 60);
//
//        // 限定图形显示区域
//        g.clipRect(130, 380, 60, 60);
//        g.clipRect(150, 400, 50, 50);
//        g.fillRect(110, 330, 100, 100);
//        g.setClip(null);

        // 绘制字符串
//        g.setColor(Color.GREEN);
//        g.setFont(new Font("楷体", Font.BOLD, 20));
//        g.drawString("使用画笔绘制的字符串内容", 220, 345);

        // 绘制图像
//        Image img = Toolkit.getDefaultToolkit().getImage("img/monster.gif");
//        g.drawImage(img, 510, 5, 200, 200, Color.LIGHT_GRAY, this);

        // 复制图形
//        g.copyArea(0, 0, 500, 500, 505, 205);
    }
}