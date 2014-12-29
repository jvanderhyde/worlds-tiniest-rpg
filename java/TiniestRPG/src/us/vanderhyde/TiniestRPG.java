//Command-line java version of the World's Tiniest RPG
//Created by James Vanderhyde, 23 December 2014

package us.vanderhyde;

import java.util.List;
import java.util.Scanner;

/**
 * Command-line application for the World's Tiniest RPG.
 * @author James Vanderhyde
 */
public class TiniestRPG
{
    public static void main(String[] args)
    {
        Database db = new Database(IPAddress.getHostAddress());
        
        Question question = startGame(db);
        while (question != null)
        {
            question = playGame(db,question);
        }
        finishGame(db);
        
        db.close();
    }

    private static Question startGame(Database db)
    {
        Question question = db.checkForIncompleteGame();
        if (question == null)
        {
            db.startNewGame();
            question = db.getFirstQuestion();
        }
        return question;
    }
    
    private static Question playGame(Database db, Question question)
    {
        System.out.println(question.getText());
        
        if (question.hasReponses())
        {
            List<Integer> permuted = question.getResponseKeysShuffled();
            char option = 'A';
            for (int index : permuted)
            {
                System.out.println(""+option+". "+question.getResponse(index));
                option++;
            }
            int chosenResponse = permuted.get((int)(readUserChar(permuted.size())-'A'));
            db.respondToQuestion(question.getId(),chosenResponse);
        }

        return db.getNextQuestion(question.getNext());
    }

    private static void finishGame(Database db)
    {
        Result result = db.getResult();
        System.out.println(result.getText());
        db.completeGame(result.getId());
    }
    
    private static char readUserChar(int numChoices)
    {
        Scanner in = new Scanner(System.in);
        String userEntry = in.nextLine();
        while (userEntry.length()==0) 
            userEntry = in.nextLine();
        char userOption = Character.toUpperCase(userEntry.charAt(0));
        if ((userOption>='A') && (userOption<'A'+numChoices))
            return userOption;
        else
        {
            System.out.println("Please enter a choice between A and "+('A'+numChoices-1));
            return readUserChar(numChoices);
        }
    }

}
