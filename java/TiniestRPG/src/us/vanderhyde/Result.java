//Result class
//Created by James Vanderhyde, 29 December 2014

package us.vanderhyde;

import java.awt.Image;

/**
 * A quiz result.
 * @author James Vanderhyde
 */
public class Result
{
    private final int id;
    private final String text;
    private final Image image;

    public Result(int id, String text)
    {
        this.id = id;
        this.text = text;
        this.image = null;
    }

    public int getId()
    {
        return id;
    }

    public String getText()
    {
        return text;
    }
    
}
