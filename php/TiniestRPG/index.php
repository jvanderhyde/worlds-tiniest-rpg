<!DOCTYPE html>
<!--
//World's Tiniest Role-Playing Game (start game)
//Created by James Vanderhyde, 30 December 2014
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>World's Tiniest RPG</title>
    </head>
    <body>
<?php
$playerIP = filter_input(INPUT_SERVER, 'REMOTE_ADDR', FILTER_VALIDATE_IP);
echo $playerIP . "<br/>";

$conn = new mysqli("localhost", "tiny", "N2VnVLPvrnqfGj7x", "TINYRPG");
if ($conn->connect_error)
{
    die("Connection failed: " . $conn->connect_error);
} 
echo "Connection to database established." . "<br/>";

//Check for existing game from this IP.
$stmt = $conn->stmt_init();
$stmt->prepare(
        "SELECT ID "
      . "FROM StartedGame "
      . "WHERE IPAddr = ? "
      . "AND ID NOT IN "
      . " (SELECT ID "
      . "  FROM CompletedGame "
      . "  WHERE IPAddr = ?)");
$stmt->bind_param("ss", $playerIP, $playerIP);
$stmt->execute();
$dbresult = $stmt->get_result();
if ($dbresult->num_rows > 0)
{
    //Found existing incomplete game. Look up next unanswered question.
    $row = $dbresult->fetch_assoc();
    $playerid = $row["ID"];
    
    $stmt->close();
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "SELECT ID " .
            "FROM Question " .
            "WHERE ID NOT IN " .
            " (SELECT Q.ID " .
            "  FROM CompletedQuestion AS C, Question AS Q " .
            "  WHERE C.GameID = ? " .
            "  AND CompletedQ = Q.ID) " .
            "AND ID IN " .
            " (SELECT Q.Next " .
            "  FROM CompletedQuestion AS C, Question AS Q " .
            "  WHERE C.GameID = ? " .
            "  AND CompletedQ = Q.ID)");
    $stmt->bind_param("ii", $playerid, $playerid);
    $stmt->execute();
    $dbresult = $stmt->get_result();
    if ($dbresult->num_rows > 0)
    {
        $row = $dbresult->fetch_assoc();
        $nextquestion = $row["ID"];
    }
    else
    {
        //An empty result set can mean one of two things:
        //1. All the questions were completed (but the game was not completed for some reason).
        //2. None of the questions were completed.
        //Since #1 should never happen, we will assume it's #2 and just start with the first question.
        $nextquestion = -1;
    }
    $stmt->close();
}
else
{
    //No existing game, so create a new game.
    $stmt->close();
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "INSERT INTO StartedGame (IPAddr) "
          . "VALUES (?)");
    $stmt->bind_param("s", $playerIP);
    $stmt->execute();
    $playerid = $stmt->insert_id;
    $stmt->close();
    
    //Start with first question
    $nextquestion = -1;
}

if ($nextquestion == -1)
{
    //Load the first question from the database
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "SELECT Text, Next "
          . "FROM Question "
          . "WHERE Name = 'INTRO' ");
    $stmt->execute();
    $dbresult = $stmt->get_result();
    if ($dbresult->num_rows > 0)
    {
        $row = $dbresult->fetch_assoc();
        $introText = $row["Text"];
        $nextquestion = $row["Next"];
    }
    else
    {
        die("Error in database: missing INTRO text.");
    }

    //Display the introductory text
    echo "<p>" . $introText . "</p>";
}
else
{
    //Continue existing game
    echo "<p>Welcome back! You may continue on your quest.</p>";
}

$conn->close();
echo "Database connection closed." . "<br/>";

//Display a form to start the next question
?>
        <form action="play.php" method="post">
            <input type="submit" value="Start your quest!">
            <input type="hidden" name="player" value="<?php echo $playerid; ?>">
            <input type="hidden" name="question" value="<?php echo $nextquestion; ?>">
        </form>
        
    </body>
</html>
