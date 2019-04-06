## Homework 2 - Karan Kadakia - UIN: 655641760
Implemented the chess game by making use of REST api calls

##Overall idea about the project
- The directory javaopenchess-svn-r167/src/main/java/pl/art/lach/mateusz/javaopenchess contains the file RestCalls.java and rest of the project files as well
- Same directory contains RestCallsController.java which has the code that allows to make REST calls, Response.java is the representation class that returns the JSON format, IChessEngine.java is the interface
- The game involves a local player and a computer
- Local player makes a move using the REST api call and the Computer responds to the local player with a move and again the local player makes a move and so on
- Computer can start first as well


##How to run the project
- Clone the repository 
- Import the java project in Intellij
- If the project shows error then the maven repositories can be imported from pom.xml 
- RestCalls.java is the main file present in javaopenchess-svn-r167/src/main/java/pl/art/lach/mateusz/javaopenchess
- Run RestCalls.java in Intellij, now the project is running and REST calls can be made 
- Usually project will be running on http://localhost:8080

##How to play the chess game with computer
- Either postman or browser can be used to make the REST calls
- I will explain by using browser

###Case 1 - Computer makes the first move
- Put the following GET request in browser: http://localhost:8080/newgame?firstMove=true
- Above call indicates that computer should make the first move so now the computer returns the move made
- Now it is the turn of the local player to make a move which can be done by: http://localhost:8080/move?start=h3,end=h4,session=newmove
- Above call indicates local players move of h3-h4 and session = newmove means it is a new move made and not a new game

###Case 2 - Local player makes the first move
- Put the following GET request in browser: http://localhost:8080/move?start=h3,end=h4,session=newgame
- Above call, session = newgame indicates that it is a new game initiated by the local player along with the start and end blocks specified
- Now computer returns a move and the game goes on 

###Case 3 - Quit the game
- Put the following GET request in browser: http://localhost:8080/quit

