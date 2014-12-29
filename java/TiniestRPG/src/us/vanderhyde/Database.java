//A class to manage the connection to the database.
//Created by James Vanderhyde, 26 December 2014

package us.vanderhyde;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Maintains the connection to the database.
 * @author James Vanderhyde
 */
public class Database
{
    private Connection conn;
    private int playerID;
    private String playerIP;
    
    public Database()
    {
        try
        {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/TINYRPG", "tiny", "N2VnVLPvrnqfGj7x");
            System.out.println("Connection to database established.");
        }
        catch (SQLException e)
        {
            System.err.println("SQL exception: " + e.getMessage());
            System.err.println("SQL state: " + e.getSQLState());
            System.err.println("Error code: " + e.getErrorCode());
            throw new RuntimeException(e);
        }
    }
    
    public void close()
    {
        try
        {
            conn.close();
            System.out.println("Database connection closed.");
        }
        catch (SQLException e)
        {
            System.err.println("SQL exception: " + e.getMessage());
            System.err.println("SQL state: " + e.getSQLState());
            System.err.println("Error code: " + e.getErrorCode());
        }
    }

    int startNewGame(String ip)
    {
        this.playerIP = ip;
        try
        {
            PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO IncompleteGame (IPAddr) "
                  + "VALUES (?)",Statement.RETURN_GENERATED_KEYS);
            p.clearParameters();
            p.setString(1, ip);
            p.executeUpdate();
            
            try (ResultSet insertResult = p.getGeneratedKeys())
            {
                if (insertResult.next())
                    this.playerID = insertResult.getInt(1);
                else
                    throw new RuntimeException("Error inserting new game into database.");
            }
            
            PreparedStatement intro = conn.prepareStatement(
                    "SELECT Text, Next "
                  + "FROM Question "
                  + "WHERE Name = 'INTRO' ");
            int nextQuestionID;
            String introMessage;
            try (ResultSet introResult = intro.executeQuery())
            {
                if (introResult.next())
                {
                    introMessage = introResult.getString("Text");
                    nextQuestionID = introResult.getInt("Next");
                }
                else
                {
                    System.out.println(intro.toString());
                    throw new RuntimeException("Error accessing database intro text.");
                }
            }
            
            System.out.println(introMessage);
            return nextQuestionID;
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error accessing database intro text.");
        }
    }

    int askNextQuestion(int questionID)
    {
        try
        {
            PreparedStatement question = conn.prepareStatement(
                    "SELECT Text, Next, Pts "
                  + "FROM Question "
                  + "WHERE ID = ? ");
            question.clearParameters();
            question.setInt(1, questionID);
            int nextQuestionID;
            String questionMessage;
            try (ResultSet questionResult = question.executeQuery())
            {
                if (questionResult.next())
                {
                    questionMessage = questionResult.getString("Text");
                    nextQuestionID = questionResult.getInt("Next");
                    if (questionResult.wasNull())
                        nextQuestionID = -1;
                }
                else
                    throw new RuntimeException("Error accessing database question "+questionID);
            }

            Map<Integer,String> responses = new HashMap<>();
            PreparedStatement p = conn.prepareStatement(
                    "SELECT Result, Text "
                  + "FROM QuestionResponse "
                  + "WHERE Question = ?");
            p.clearParameters();
            p.setInt(1, questionID);
            try (ResultSet questionResult = p.executeQuery())
            {
                while (questionResult.next())
                {
                    responses.put(questionResult.getInt("Result"), questionResult.getString("Text"));
                }
            }
            
            System.out.println(questionMessage);
            List<Integer> permuted = new ArrayList<>(responses.keySet());
            Collections.shuffle(permuted);
            char option = 'A';
            for (int index : permuted)
            {
                System.out.println(""+option+". "+responses.get(index));
                option++;
            }
            int chosenResponse = permuted.get((int)(readUserChar(permuted)-'A'));
            
            PreparedStatement insertComplete = conn.prepareStatement(
                    "INSERT INTO CompletedQuestion (GameID, CompletedQ, Result) "
                  + "VALUES (?,?,?)");
            insertComplete.clearParameters();
            insertComplete.setInt(1, this.playerID);
            insertComplete.setInt(2, questionID);
            insertComplete.setInt(3, chosenResponse);
            insertComplete.executeUpdate();
            
            return nextQuestionID;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error accessing database question number "+questionID);
        }
    }
    
    private char readUserChar(List<Integer> permuted)
    {
        Scanner in = new Scanner(System.in);
        String userEntry = in.nextLine();
        while (userEntry.length()==0) 
            userEntry = in.nextLine();
        char userOption = Character.toUpperCase(userEntry.charAt(0));
        if ((userOption>='A') && (userOption<'A'+permuted.size()))
            return userOption;
        else
        {
            System.out.println("Please enter a choice between A and "+('A'+permuted.size()-1));
            return readUserChar(permuted);
        }
    }

    void displayResult()
    {
        try
        {
            PreparedStatement p = conn.prepareStatement(
                    "SELECT Result, SUM(Pts) AS TotalPts " +
                    "FROM CompletedQuestion AS C, Question AS Q " +
                    "WHERE C.GameID = ? " +
                    "AND CompletedQ = Q.ID " +
                    "GROUP BY Result");
            p.clearParameters();
            p.setInt(1, this.playerID);
            Map<Integer,Integer> results = new HashMap<>();
            try (ResultSet questionResult = p.executeQuery())
            {
                while (questionResult.next())
                {
                    results.put(questionResult.getInt("Result"), questionResult.getInt("TotalPts"));
                }
            }
            
            int max = 0;
            Integer maxkey = null;
            for (Integer key : results.keySet())
            {
                int val = results.get(key);
                if (val > max)
                {
                    max = val;
                    maxkey = key;
                }
            }
            
            PreparedStatement result = conn.prepareStatement(
                    "SELECT Text, Image "
                  + "FROM Result "
                  + "WHERE ID = ? ");
            result.clearParameters();
            result.setInt(1, maxkey);
            String resultMessage;
            try (ResultSet resultResult = result.executeQuery())
            {
                if (resultResult.next())
                {
                    resultMessage = resultResult.getString("Text");
                }
                else
                    throw new RuntimeException("Error accessing database result "+maxkey);
            }
            System.out.println(resultMessage);
            
            PreparedStatement gameComplete = conn.prepareStatement(
                    "INSERT INTO CompletedGame (ID, IPAddr, Result) "
                  + "VALUES (?,?,?)");
            gameComplete.clearParameters();
            gameComplete.setInt(1, this.playerID);
            gameComplete.setString(2, this.playerIP);
            gameComplete.setInt(3, maxkey);
            gameComplete.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error accessing database for final result.");
        }
    }

    int checkForIncompleteGame(String host)
    {
        return -1;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
