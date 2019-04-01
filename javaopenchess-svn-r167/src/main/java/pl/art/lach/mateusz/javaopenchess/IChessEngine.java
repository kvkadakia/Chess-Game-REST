package pl.art.lach.mateusz.javaopenchess;


public interface IChessEngine {
    Response newGame(boolean firstMove) throws Exception;
    Response move(String start, String end, String session) throws Exception;
    String quitGame(String session) throws Exception;
}