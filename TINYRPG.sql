-- phpMyAdmin SQL Dump
-- version 4.3.3
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 01, 2015 at 10:52 PM
-- Server version: 5.6.22
-- PHP Version: 5.5.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `TINYRPG`
--

-- --------------------------------------------------------

--
-- Table structure for table `CompletedGame`
--

CREATE TABLE IF NOT EXISTS `CompletedGame` (
  `ID` int(11) NOT NULL,
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Result` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `CompletedQuestion`
--

CREATE TABLE IF NOT EXISTS `CompletedQuestion` (
  `ID` int(11) NOT NULL,
  `GameID` int(11) NOT NULL,
  `DatePlayed` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CompletedQ` int(11) NOT NULL,
  `Result` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `Question`
--

CREATE TABLE IF NOT EXISTS `Question` (
  `ID` int(11) NOT NULL,
  `Name` varchar(31) NOT NULL,
  `Text` varchar(511) NOT NULL,
  `Pts` int(11) NOT NULL,
  `Next` int(11) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Question`
--

INSERT INTO `Question` (`ID`, `Name`, `Text`, `Pts`, `Next`) VALUES
(1, 'bridge', 'You walk along the path for a ways and come to a river. The bridge is out. What do you do?', 104, 2),
(2, 'minotaur', 'Safely across the river, you continue on your journey. Around the bend, an angry minotaur jumps out to get you. What do you do?', 116, 3),
(3, 'bat', 'Hoping that the minotaur is the nastiest enemy you will face, you continue on your way. You hope there will be a treasure box around the next bend. As you turn the corner, you hear a rustling and flapping. A giant bat is flying directly at your face!', 102, 4),
(4, 'skeletons', 'A close call with the bat. You find a treasure chest, and inside is a coat of mithril mail! Handy! You travel onward until you smell something rotten, and behind you hear rattling. You whirl around and see three skeletons wielding rusty short swords!', 108, 5),
(5, 'dragon', 'Free of the skeletons! In the distance you can see flags flying from a castle. In your haste, you almost don''t see the dragon under a great tree. Too late! The dragon has awakened! You''ll have to use all your skills this time! Where will you begin??', 132, NULL),
(6, 'INTRO', 'You are a young adventurer, fresh out of adventure school. You have some training in basic sword use, and you have some latent elemental magic ability that you are still trying to hone. Can you master the challenges that await you?', 0, 1);

-- --------------------------------------------------------

--
-- Table structure for table `QuestionResponse`
--

CREATE TABLE IF NOT EXISTS `QuestionResponse` (
  `Question` int(11) NOT NULL,
  `Result` int(11) NOT NULL,
  `Text` varchar(511) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `QuestionResponse`
--

INSERT INTO `QuestionResponse` (`Question`, `Result`, `Text`) VALUES
(1, 1, 'Try to repair the bridge with some rope.'),
(1, 2, 'Get a running start and jump across the river. Swim if you don''t make it.'),
(1, 3, 'Wait and hope someone with a boat comes along.'),
(1, 4, 'Speak to the river to persuade it to allow you to pass.'),
(2, 1, 'When it attacks, use its forward momentum to throw it behind you. Escape while it is stunned.'),
(2, 2, 'Attack head on, bringing down your sword with full force.'),
(2, 3, 'Run away as fast as you can!'),
(2, 4, 'Summon a fireball and launch it at the minotaur''s face.'),
(3, 1, 'Duck, spin around, and throw your dagger at the bat as you run away.'),
(3, 2, 'Swing your sword over your head to slice the bat in two.'),
(3, 3, 'Hit the dirt, scramble to your feet, and run away.'),
(3, 4, 'Summon a tornado to whisk the bat away.'),
(4, 1, 'Jump around each one nimbly, confusing them and causing them to attack each other.'),
(4, 2, 'One by one, attack them head on with your sword.'),
(4, 3, 'They don''t look like they can run too fast. Run away!'),
(4, 4, 'Hurl a fireball into their midst.'),
(5, 1, 'You''ve still got some rope. Try to disable him.'),
(5, 2, 'Attack swiftly and surely with your trusty sword.'),
(5, 3, 'Run away. You think you can just make it....'),
(5, 4, 'Fireballs won''t work on a dragon, and a little wind will just fuel his fire. Call on the snow and rain for help!');

-- --------------------------------------------------------

--
-- Table structure for table `Result`
--

CREATE TABLE IF NOT EXISTS `Result` (
  `ID` int(11) NOT NULL,
  `Name` varchar(31) NOT NULL,
  `Text` varchar(511) NOT NULL,
  `Image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Result`
--

INSERT INTO `Result` (`ID`, `Name`, `Text`, `Image`) VALUES
(1, 'Strategist', 'You try every skill you have in your battle against the dragon, and you are successful! Your finesse and strategy have come to full fruition and you manage to lure the dragon into a carefully set trap. The dragon is caught and you are free! Congratulations, Strategist!', 'Strategist.jpg'),
(2, 'Swordsman', 'You try every skill you have in your battle against the dragon, and you are successful! Your strength and sword skills are unmatched, and you are able to deal the deathblow directly to the dragon''s heart. Congratulations, Swordsman!', 'Swordsman.jpg'),
(3, 'GameOver', 'You try every skill you have in your battle against the dragon, but it just wasn''t enough. You have not been brave in the past, and this dragon has you quaking in your boots. You try to get away but the dragon is too fast for you. He swallows you in two bites.', 'GameOver.jpg'),
(4, 'Wizard', 'You try every skill you have in your battle against the dragon, and you are successful! Your magical abilities and arcane knowledge have become the most important skills you possess. The elements come at your command, and the water and ice swirl around the dragon. Soon he is encased in ice, and you are free! Congratulations, Wizard!', 'Wizard.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `StartedGame`
--

CREATE TABLE IF NOT EXISTS `StartedGame` (
  `ID` int(11) NOT NULL,
  `FBID` int(11) DEFAULT NULL,
  `IPAddr` varchar(255) DEFAULT NULL,
  `Date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `CompletedGame`
--
ALTER TABLE `CompletedGame`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `CompletedQuestion`
--
ALTER TABLE `CompletedQuestion`
  ADD PRIMARY KEY (`ID`), ADD KEY `GameID` (`GameID`);

--
-- Indexes for table `Question`
--
ALTER TABLE `Question`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `QuestionResponse`
--
ALTER TABLE `QuestionResponse`
  ADD KEY `Question` (`Question`), ADD KEY `Result` (`Result`);

--
-- Indexes for table `Result`
--
ALTER TABLE `Result`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `StartedGame`
--
ALTER TABLE `StartedGame`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `CompletedQuestion`
--
ALTER TABLE `CompletedQuestion`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=65;
--
-- AUTO_INCREMENT for table `Question`
--
ALTER TABLE `Question`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=8;
--
-- AUTO_INCREMENT for table `Result`
--
ALTER TABLE `Result`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `StartedGame`
--
ALTER TABLE `StartedGame`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=25;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `CompletedGame`
--
ALTER TABLE `CompletedGame`
ADD CONSTRAINT `completedgame_ibfk_1` FOREIGN KEY (`ID`) REFERENCES `StartedGame` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `CompletedQuestion`
--
ALTER TABLE `CompletedQuestion`
ADD CONSTRAINT `completedquestion_ibfk_1` FOREIGN KEY (`GameID`) REFERENCES `StartedGame` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `QuestionResponse`
--
ALTER TABLE `QuestionResponse`
ADD CONSTRAINT `questionresponse_ibfk_1` FOREIGN KEY (`Question`) REFERENCES `Question` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
ADD CONSTRAINT `questionresponse_ibfk_2` FOREIGN KEY (`Result`) REFERENCES `Result` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
