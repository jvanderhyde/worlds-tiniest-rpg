<!DOCTYPE html>
<!--
//World's Tiniest Role-Playing Game (finish game)
//Created by James Vanderhyde, 1 January 2015
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title>World's Tiniest RPG</title>
    </head>
    <body>
<?php

//Get form values
$options=array('options'=>array('default'=>-1));
$playerid = filter_input(INPUT_POST, 'player', FILTER_VALIDATE_INT, $options);
$prevquestion = filter_input(INPUT_POST, 'previous', FILTER_VALIDATE_INT, $options);
$response = filter_input(INPUT_POST, 'response', FILTER_VALIDATE_INT, $options);

if ($playerid==-1)
    die("No player specified.");

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

//Calculate result
$stmt1 = $conn->stmt_init();
$stmt1->prepare(
        "SELECT Result, SUM(Pts) AS TotalPts " .
        "FROM CompletedQuestion AS C, Question AS Q " .
        "WHERE C.GameID = ? " .
        "AND CompletedQ = Q.ID " .
        "GROUP BY Result " .
        "ORDER BY TotalPts DESC");
$stmt1->bind_param("i", $playerid);
$stmt1->execute();
$dbresult1 = $stmt1->get_result();
if ($dbresult1->num_rows > 0)
{
    $row = $dbresult1->fetch_assoc();
    $playerresult = $row["Result"];
}
else
{
    die("Error accessing database: no results.");
}
$stmt1->close();

//Look up result
$stmt2 = $conn->stmt_init();
$stmt2->prepare(
        "SELECT Text, Image " .
        "FROM Result " .
        "WHERE ID = ? ");
$stmt2->bind_param("i", $playerresult);
$stmt2->execute();
$dbresult2 = $stmt2->get_result();
if ($dbresult2->num_rows > 0)
{
    $row = $dbresult2->fetch_assoc();
    $resulttext = $row["Text"];
    $resultimage = $row["Image"];
}
else
{
    die("Error accessing database result " . $playerresult);
}
$stmt2->close();

//Record completed game
$stmt3 = $conn->stmt_init();
$stmt3->prepare(
        "INSERT INTO CompletedGame (ID, Result) "
      . "VALUES (?,?)");
$stmt3->bind_param("ii", $playerid, $playerresult);
$stmt3->execute();
$stmt3->close();
$stmt4 = $conn->stmt_init();
$stmt4->prepare(
        "DELETE FROM CompletedQuestion "
      . "WHERE GameID = ?");
$stmt4->bind_param("i", $playerid);
$stmt4->execute();
$stmt4->close();

//Display result
echo "<p>" . $resulttext . "</p>";
echo '<img src="images/' . $resultimage . '"/>';

$conn->close();
?>
    </body>
</html>
