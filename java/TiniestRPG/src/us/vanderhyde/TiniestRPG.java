//Command-line java version of the World's Tiniest RPG
//Created by James Vanderhyde, 23 December 2014

package us.vanderhyde;

/**
 * Command-line application for the World's Tiniest RPG.
 * @author James Vanderhyde
 */
public class TiniestRPG
{
    public static void main(String[] args)
    {
        Database db = new Database();
        int question;
        
        question = db.startNewGame(IPAddress.getHostAddress());
        while (question >= 0)
        {
            question = db.askNextQuestion(question);
        }
        
        db.displayResult();
        
        db.close();
    }

}
