<!DOCTYPE html>
<!--
//World's Tiniest Role-Playing Game (ask question)
//Created by James Vanderhyde, 1 January 2015
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>World's Tiniest RPG</title>
    </head>
    <body>
        <div style="width: 450px; margin-left: auto; margin-right: auto;">
<?php

//Get form values
$options=array('options'=>array('default'=>-1));
$playerid = filter_input(INPUT_POST, 'player', FILTER_VALIDATE_INT, $options);
$question = filter_input(INPUT_POST, 'question', FILTER_VALIDATE_INT, $options);
$prevquestion = filter_input(INPUT_POST, 'previous', FILTER_VALIDATE_INT, $options);
$response = filter_input(INPUT_POST, 'response', FILTER_VALIDATE_INT, $options);

if ($playerid==-1)
    die("No player specified.");
if ($question==-1)
    die("No question specified.");

$conn = new mysqli("localhost", "tiny", "N2VnVLPvrnqfGj7x", "TINYRPG");
if ($conn->connect_error)
{
    die("Connection failed: " . $conn->connect_error);
} 

if ($prevquestion != -1 && $response != -1)
{
    //Record response in database
    $stmt = $conn->stmt_init();
    $stmt->prepare(
            "INSERT INTO CompletedQuestion (GameID, CompletedQ, Result) "
          . "VALUES (?,?,?)");
    $stmt->bind_param("iii", $playerid, $prevquestion, $response);
    $stmt->execute();
    $stmt->close();
}

//Load question from database
$stmt1 = $conn->stmt_init();
$stmt1->prepare(
        "SELECT Text, Next "
      . "FROM Question "
      . "WHERE ID = ? ");
$stmt1->bind_param("i", $question);
$stmt1->execute();
$dbresult1 = $stmt1->get_result();
if ($dbresult1->num_rows > 0)
{
    $row = $dbresult1->fetch_assoc();
    $questionMessage = $row["Text"];
    $nextquestion = $row["Next"];
}
else
{
    die("Error accessing database question " . $question);
}
$stmt1->close();

//Load responses from database
$responses = array();
$stmt2 = $conn->stmt_init();
$stmt2->prepare(
        "SELECT Result, Text "
      . "FROM QuestionResponse "
      . "WHERE Question = ? ");
$stmt2->bind_param("i", $question);
$stmt2->execute();
$dbresult2 = $stmt2->get_result();
while ($row = $dbresult2->fetch_assoc())
{
    $responses[$row["Result"]] = $row["Text"];
}
$stmt2->close();

$conn->close();

//Display question text
echo "<p>" . $questionMessage . "</p>";

//Set up the right form, depending on whether this is the last question
if ($nextquestion === NULL)
    echo '<form action="finish.php" method="post">';
else
    echo '<form action="play.php" method="post">';

//Randomize and display the responses
$keys = array_keys($responses);
shuffle($keys);
foreach ($keys as $index)
{
    echo '<input type="radio" name="response" value="';
    echo $index;
    echo '">';
    echo $responses[$index];
    echo '<br>';
}

?>
                <input type="submit" value="Select">
                <input type="hidden" name="player" value="<?php echo $playerid; ?>">
                <input type="hidden" name="previous" value="<?php echo $question; ?>">
                <input type="hidden" name="question" value="<?php echo $nextquestion; ?>">
            </form>
        </div>
    </body>
</html>
