//A class to manage the connection to the database.
//Created by James Vanderhyde, 26 December 2014

package us.vanderhyde;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Maintains the connection to the database and provides access to data.
 * @author James Vanderhyde
 */
public class Database
{
    private final Connection conn;
    private int playerID;
    private final String playerIP;
    
    public Database(String host)
    {
        this.playerIP = host;
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

    void startNewGame()
    {
        try
        {
            PreparedStatement p = conn.prepareStatement(
                    "INSERT INTO StartedGame (IPAddr) "
                  + "VALUES (?)",Statement.RETURN_GENERATED_KEYS);
            p.clearParameters();
            p.setString(1, playerIP);
            p.executeUpdate();
            
            try (ResultSet insertResult = p.getGeneratedKeys())
            {
                if (insertResult.next())
                    this.playerID = insertResult.getInt(1);
                else
                    throw new RuntimeException("Error inserting new game into database.");
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error accessing database intro text.");
        }
    }

    Question getFirstQuestion()
    {
        try
        {
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

            return new Question(0,introMessage,nextQuestionID,null);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error accessing database for intro");
        }
    }

    Question getNextQuestion(int questionID)
    {
        //When the ID is -1, there are no more questions.
        if (questionID == -1)
            return null;
        
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
            
            return new Question(questionID,questionMessage,nextQuestionID,responses);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error accessing database question number "+questionID);
        }
    }
    
    void respondToQuestion(int questionID, int chosenResponse)
    {
        try
        {
            PreparedStatement insertComplete = conn.prepareStatement(
                    "INSERT INTO CompletedQuestion (GameID, CompletedQ, Result) "
                  + "VALUES (?,?,?)");
            insertComplete.clearParameters();
            insertComplete.setInt(1, this.playerID);
            insertComplete.setInt(2, questionID);
            insertComplete.setInt(3, chosenResponse);
            insertComplete.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Error updating database for question response "+questionID);
        }
    }

    Result getResult()
    {
        try
        {
            PreparedStatement points = conn.prepareStatement(
                    "SELECT Result, SUM(Pts) AS TotalPts " +
                    "FROM CompletedQuestion AS C, Question AS Q " +
                    "WHERE C.GameID = ? " +
                    "AND CompletedQ = Q.ID " +
                    "GROUP BY Result " +
                    "ORDER BY TotalPts DESC");
            points.clearParameters();
            points.setInt(1, this.playerID);
            int maxResult;
            try (ResultSet questionResult = points.executeQuery())
            {
                if (questionResult.next())
                    maxResult = questionResult.getInt("Result");
                else
                    throw new RuntimeException("Error accessing database: no results.");
            }
            
            PreparedStatement result = conn.prepareStatement(
                    "SELECT Text, Image "
                  + "FROM Result "
                  + "WHERE ID = ? ");
            result.clearParameters();
            result.setInt(1, maxResult);
            String resultMessage;
            try (ResultSet resultResult = result.executeQuery())
            {
                if (resultResult.next())
                    resultMessage = resultResult.getString("Text");
                else
                    throw new RuntimeException("Error accessing database result "+maxResult);
            }
            
            return new Result(maxResult, resultMessage);
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error accessing database for final result.");
        }
    }
    
    void completeGame(int resultID)
    {
        try
        {
            PreparedStatement gameComplete = conn.prepareStatement(
                    "INSERT INTO CompletedGame (ID, Result) "
                  + "VALUES (?,?)");
            gameComplete.clearParameters();
            gameComplete.setInt(1, this.playerID);
            gameComplete.setInt(2, resultID);
            gameComplete.executeUpdate();

            PreparedStatement deleteIncomplete = conn.prepareStatement(
                    "DELETE FROM CompletedQuestion "
                  + "WHERE GameID = ?");
            deleteIncomplete.clearParameters();
            deleteIncomplete.setInt(1, this.playerID);
            deleteIncomplete.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error updating database for completing game.");
        }
    }

    Question checkForIncompleteGame()
    {
        try
        {
            PreparedStatement incompleteGames = conn.prepareStatement(
                    "SELECT ID "
                  + "FROM StartedGame "
                  + "WHERE IPAddr = ? "
                  + "AND ID NOT IN "
                  + " (SELECT ID "
                  + "  FROM CompletedGame "
                  + "  WHERE IPAddr = ?)");
            incompleteGames.clearParameters();
            incompleteGames.setString(1, playerIP);
            incompleteGames.setString(2, playerIP);
            int gameID;
            try (ResultSet resultResult = incompleteGames.executeQuery())
            {
                if (resultResult.next())
                    gameID = resultResult.getInt("ID");
                else
                    gameID = -1;
            }
            
            if (gameID < 0)
                return null;
            else
                this.playerID = gameID;
            
            PreparedStatement completedQuestions = conn.prepareStatement(
                    "SELECT ID \n" +
                    "FROM Question \n" +
                    "WHERE ID NOT IN \n" +
                    " (SELECT Q.ID \n" +
                    "  FROM CompletedQuestion AS C, Question AS Q \n" +
                    "  WHERE C.GameID = ? \n" +
                    "  AND CompletedQ = Q.ID) \n" +
                    "AND ID IN \n" +
                    " (SELECT Q.Next \n" +
                    "  FROM CompletedQuestion AS C, Question AS Q \n" +
                    "  WHERE C.GameID = ? \n" +
                    "  AND CompletedQ = Q.ID)");
            completedQuestions.clearParameters();
            completedQuestions.setInt(1, gameID);
            completedQuestions.setInt(2, gameID);
            int nextQuestion;
            try (ResultSet resultResult = completedQuestions.executeQuery())
            {
                if (resultResult.next())
                    nextQuestion = resultResult.getInt("ID");
                else
                    nextQuestion = -1;
            }
            
            //An empty result set can mean one of two things:
            //1. All the questions were completed (but the game was not completed for some reason).
            //2. None of the questions were completed.
            //Since #1 should never happen, we will assume it's #2 and just start the game over.

            if (nextQuestion < 0)
                return this.getFirstQuestion();
            else
                return this.getNextQuestion(nextQuestion);
        }
        catch (SQLException e)
        {
            System.out.println(e);
            throw new RuntimeException("Error accessing database for final result.");
        }
    }
}
