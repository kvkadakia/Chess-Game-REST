package pl.art.lach.mateusz.javaopenchess;

/*
* {
* "move": {
  "session": "MarkGrechanik1","startSquare": "e2","endSquare": "f3","description": {"chesspiece": [{"bishop": "whitesquares", "attacks": "pawn"},{"pawn": "black", "attackedby": "bishop"}]
  }
}}
* */

/*
* {
    "id": 1,
    "content": "Hello, World!"
}*/

public class Response {
    private  Move move;

    public Response(String session, String startSquare, String endSquare) {
        this.move = new Move(session,startSquare,endSquare);
    }

    public Move getMove() {
        return move;
    }

}

class Move{
    private String session;
    private String startSquare;
    private String endSquare;

    Move(String session, String startSquare, String endSquare) {
        this.session = session;
        this.startSquare=startSquare;
        this.endSquare = endSquare;
    }

    public String getSession() {
        return session;
    }

    public String getStartSquare() {
        return startSquare;
    }

    public String getEndSquare() {
        return endSquare;
    }
}

