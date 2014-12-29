//Question class
//Created by James Vanderhyde, 29 December 2014

package us.vanderhyde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A multiple choice question, with an index to the next question.
 * @author James Vanderhyde
 */
public class Question
{
    private final int id;
    private final String text;
    private final int next;
    private final Map <Integer,String> responses;

    public Question(int id, String text, int next, Map<Integer, String> responses)
    {
        this.id = id;
        this.text = text;
        this.next = next;
        this.responses = responses;
    }

    public int getId()
    {
        return id;
    }

    public String getText()
    {
        return text;
    }

    public int getNext()
    {
        return next;
    }
    
    public boolean hasReponses()
    {
        return (responses != null);
    }
   
    public List<Integer> getResponseKeysShuffled()
    {
        List<Integer> permuted = new ArrayList<>(responses.keySet());
        Collections.shuffle(permuted);
        return permuted;
    }
    
    public String getResponse(Integer key)
    {
        return responses.get(key);
    }
}
