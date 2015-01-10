<!DOCTYPE html>
<!--
//World's Tiniest Role-Playing Game (start game)
//Created by James Vanderhyde, 30 December 2014
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>World's Tiniest RPG</title>
        <link rel="stylesheet" type="text/css" media="screen,print" href="oldpaper.css">
    </head>
    <body style="background-color: rgb(255, 255, 255);">
        <div style="width: 450px; margin-left: auto; margin-right: auto;">
            <p style="text-align: center">
                <img src="images/MainCharacter.jpg"/>
            </p>
            
<?php
$playerIP = filter_input(INPUT_SERVER, 'REMOTE_ADDR', FILTER_VALIDATE_IP);

$conn = new mysqli("localhost", "tiny", "N2VnVLPvrnqfGj7x", "TINYRPG");
if ($conn->connect_error)
{
    die("Connection failed: " . $conn->connect_error);
} 

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
    //Found existing incomplete game.
    $row = $dbresult->fetch_assoc();
    $playerid = $row["ID"];
    $stmt->close();
    $gameexists = true;
}
else
{
    //No existing game, so create a new game.
    $stmt->close();
    $gameexists = false;
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "INSERT INTO StartedGame (IPAddr) "
          . "VALUES (?)");
    $stmt->bind_param("s", $playerIP);
    $stmt->execute();
    $playerid = $stmt->insert_id;
    $stmt->close();
}

if ($gameexists)
{
    //Continue existing game
    echo "<p>Welcome back! You may continue on your quest.</p>";
    $buttontext = "Continue";
}
else
{
    //Load the intro from the database
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "SELECT Text "
          . "FROM Question "
          . "WHERE Name = 'INTRO' ");
    $stmt->execute();
    $dbresult = $stmt->get_result();
    if ($dbresult->num_rows > 0)
    {
        $row = $dbresult->fetch_assoc();
        $introText = $row["Text"];
    }
    else
    {
        die("Error in database: missing INTRO text.");
    }

    //Display the introductory text
    echo "<p>" . $introText . "</p>";
    $buttontext = "Start your quest!";
}

$conn->close();

//Display a form to start the next question
?>
            <form action="play.php" method="post">
                <input type="submit" value="<?php echo $buttontext; ?>">
                <input type="hidden" name="player" value="<?php echo $playerid; ?>">
            </form>
        </div>    
    </body>
</html>
