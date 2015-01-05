//A robot for clicking buttons in Magicite
//Created by James Vanderhyde, 4 January 2015

package clickrobot;

import java.awt.AWTException;
import java.awt.Frame;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Random;

public class ClickRobot
{
    private static final Random rand = new Random();
    private static boolean keepRunning = false;
    
    public static void launchFrame()
    {
        final Frame f=new Frame("Magicite robot");
        final Thread mainThread = Thread.currentThread();
        f.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                if (keepRunning)
                    mainThread.interrupt();
                f.dispose();
            }
        });
        f.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseReleased(MouseEvent evt)
            {
                if (keepRunning)
                    mainThread.interrupt();
                f.dispose();
            }
        });
        f.setSize(1024, 768);
        f.setVisible(true);
    }
    
    public static void testIterator()
    {
        CodeIterator it = new CodeIterator();
        while (it.hasNext())
        {
            int[] code = it.next();
            System.out.println(""+codeToString(code)+" "+CodeIterator.codeToInt(code));
        }
    }
    
    public static void main(String[] args)
    {
        launchFrame();
        int[] startCode = {1,1,1,1,3,1,1};
        runCodes(CodeIterator.codeToInt(startCode));
    }
    
    public static void enterOneRandomCode()
    {
        RobotThread robt = new RobotThread();
        robt.start();
        
        sleep(3000);
        
        robt.sendMessageEnterCode(randomCode());
        robt.sendMessageQuit();
        
        sleep(5000);
    }
    
    public static void runCodes(int start)
    {
        RobotThread robt = new RobotThread();
        robt.start();
        
        sleep(3000);
        
        CodeIterator it = new CodeIterator(start);
        keepRunning = true;
        while (it.hasNext() && keepRunning)
        {
            robt.sendMessageEnterCode(it.next());
            sleep(300);
        }
        
        robt.sendMessageQuit();
        sleep(5000);
    }
    
    public static void runAllCodes()
    {
        runCodes(0);
    }
    
    private static class CodeIterator implements Iterator<int[]>
    {
        int cur = 0;
        final int max = 4*4*4*4*4*4*4; // 4^7 or 2^14 or 16384
        
        public CodeIterator()
        {
            cur = 0;
        }

        public CodeIterator(int start)
        {
            cur = start;
        }

        @Override
        public boolean hasNext()
        {
            return cur < max;
        }

        @Override
        public int[] next()
        {
            int[] code = intToCode(cur);
            cur++;
            return code;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Remove not supported.");
        }
        
        public static int codeToInt(int[] code)
        {
            int r = 0;
            int m = 1;
            for (int i=0; i<code.length; i++)
            {
                r += m*(code[i]-1);
                m *= 4;
            }
            return r;
        }
        
        public static int[] intToCode(int c)
        {
            int[] code = new int[7];
            int rem = c;
            for (int i=0; i<code.length; i++)
            {
                code[i]=rem%4+1;
                rem /= 4;
            }
            return code;
        }
        
    }
    
    private static int[] randomCode()
    {
        int[] code = new int[7];
        for (int i=0; i<code.length; i++)
            code[i]=rand.nextInt(4)+1;
        return code;
    }

    private static String codeToString(int[] code)
    {
        StringBuilder sb = new StringBuilder();
        for (int digit:code)
            sb.append(digit);
        return sb.toString();
    }
    
    private static void sleep(long millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            keepRunning = false;
        }
    }
    
}
