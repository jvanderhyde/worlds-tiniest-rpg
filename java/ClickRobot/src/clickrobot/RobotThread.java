//A class to handle the Robot. Robot catches interrupted exceptions,
//  so we have to run it in its own thread to control flow.
//Created by James Vanderhyde, 5 January 2015

package clickrobot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class RobotThread extends Thread
{
    private final static int startX = 122, startY = 613;
    private Robot rob;
    
    private boolean enterCodeMessage = false;
    private int[] codeDigits;
    private boolean quitMessage = false;
    
    public RobotThread()
    {
        super("Robot thread");

        try
        {
            rob=new Robot();
            rob.setAutoDelay(40);
            rob.setAutoWaitForIdle(true);
        }
        catch (AWTException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void run()
    {
        boolean running = true;
        while (running)
        {
            running = waitForMessage();
        }
    }
    
    public synchronized boolean waitForMessage()
    {
        try
        {
            wait();
        }
        catch (InterruptedException e)
        {
            return false;
        }
        
        if (enterCodeMessage)
        {
            handleEnterCode();
            enterCodeMessage = false;
        }
        return !quitMessage;
    }
    
    private void handleEnterCode()
    {
        for (int cur:codeDigits)
        {
            move(cur);
            click();
        }
        rob.delay(100);
    }
    
    private void move(int digit)
    {
        rob.mouseMove(startX+(digit-1)*3*16, startY);
        rob.delay(25);
    }
    
    private void click()
    {
        rob.mousePress(InputEvent.BUTTON1_MASK);
        rob.delay(10);
        rob.mouseRelease(InputEvent.BUTTON1_MASK);
        rob.delay(10);
    }
    
    public synchronized void sendMessageEnterCode(int[] digits)
    {
        enterCodeMessage = true;
        codeDigits = digits;
        this.notify();
    }
    
    public synchronized void sendMessageQuit()
    {
        quitMessage = true;
        this.notify();
    }
    
}
