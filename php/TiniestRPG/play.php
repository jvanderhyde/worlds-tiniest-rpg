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
<?php

//Get form values
$options=array('options'=>array('default'=>-1));
$playerID = filter_input(INPUT_POST, 'player', FILTER_VALIDATE_INT, $options);
$question = filter_input(INPUT_POST, 'question', FILTER_VALIDATE_INT, $options);
$prevquestion = filter_input(INPUT_POST, 'previous', FILTER_VALIDATE_INT, $options);
$response = filter_input(INPUT_POST, 'response', FILTER_VALIDATE_INT, $options);

if ($playerID==-1)
    die("No player specified.");
if ($question==-1)
    die("No question specified.");

$conn = new mysqli("localhost", "tiny", "N2VnVLPvrnqfGj7x", "TINYRPG");
if ($conn->connect_error)
{
    die("Connection failed: " . $conn->connect_error);
} 
echo "Connection to database established." . "<br/>";

if ($prevquestion != -1 && $response != -1)
{
    //Record response in database
}

//Load question and responses from database

//Display question and responses
//If no next question, use a different form (finish instead of play).

$conn->close();
echo "Database connection closed." . "<br/>";
?>
    </body>
</html>
