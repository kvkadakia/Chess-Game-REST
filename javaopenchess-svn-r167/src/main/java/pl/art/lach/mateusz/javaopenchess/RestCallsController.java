package pl.art.lach.mateusz.javaopenchess;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.ai.AI;
import pl.art.lach.mateusz.javaopenchess.core.ai.AIFactory;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RestCallsController implements IChessEngine{

    protected Chessboard chessboard;
    protected Settings settings;
    protected Player activePlayer;
    protected Game game;

    @GetMapping("/newgame")
    @ResponseBody
    public Response newGame(@RequestParam(name="firstMove", defaultValue="false") boolean firstMove) throws Exception{
        List<Square> fromTo = new ArrayList<>();

        //game starts and white makes the first move
        if(firstMove==true) {
            game = new Game();
            Settings sett = game.getSettings();
            String whiteName = "VAP 1";
            String blackName = "VAP 2";
            PlayerType whiteType = PlayerType.COMPUTER;
            PlayerType blackType = PlayerType.LOCAL_USER;

            Player playerWhite = PlayerFactory.getInstance(whiteName, Colors.WHITE, whiteType);
            Player playerBlack = PlayerFactory.getInstance(blackName, Colors.BLACK, blackType);

            sett.setGameMode(GameModes.NEW_GAME);
            sett.setGameType(GameTypes.LOCAL);
            sett.setPlayerWhite(playerWhite);
            sett.setPlayerBlack(playerBlack);

            game.setActivePlayer(playerWhite);
            game.newGame();
            Game activeGame = game;
            AI ai = AIFactory.getAI(1);
            activeGame.setAi(ai);

            if (shouldDoComputerMove(activeGame)) {
                fromTo = activeGame.doComputerMove();
                System.out.println("Comp");
            }

            System.out.println(fromTo.get(0).toString().substring(39));
            System.out.println(fromTo.get(1).toString().substring(39));

            return new Response("NEW GAME - WHITE COMPUTER, BLACK LOCAL PLAYER - COMPUTER MADE MOVE, LOCAL PLAYER TURN NOW", fromTo.get(0).getAlgebraicNotation(), fromTo.get(1).getAlgebraicNotation());
        }

        else{
            throw new Exception("!!!!!!!!!!!--------Parameter firstMove has not been specified in the URL or it has been mentioned as false--------!!!!!!!!!!!!!!!!");
        }
    }

    @GetMapping("/move")
    @ResponseBody
    public Response move(@RequestParam(name="start")String start, @RequestParam(name="end")String end, @RequestParam(name="session")String session) throws Exception{

        game = new Game();
        Game activeGame = game;
        List<Square> fromTo = new ArrayList<>();

        //new game in which the local user is the white player so white plays first and then computer responds with a move
        //Now game is going on and the local user will make moves
        if(session.equalsIgnoreCase("newgame")) {
            Settings sett = game.getSettings();
            String whiteName = "VAP 1";
            String blackName = "VAP 2";
            PlayerType whiteType = PlayerType.LOCAL_USER;
            PlayerType blackType = PlayerType.COMPUTER;

            Player playerWhite = PlayerFactory.getInstance(whiteName, Colors.WHITE, whiteType);
            Player playerBlack = PlayerFactory.getInstance(blackName, Colors.BLACK, blackType);

            sett.setGameMode(GameModes.NEW_GAME);
            sett.setGameType(GameTypes.LOCAL);
            sett.setPlayerWhite(playerWhite);
            sett.setPlayerBlack(playerBlack);

            game.setActivePlayer(playerWhite);
            game.newGame();

            AI ai = AIFactory.getAI(1);
            activeGame.setAi(ai);

            //local player makes the move and sends the response
            chessboard = game.getChessboard();
            settings = game.getSettings();
            invokeMoveAction(getSquareFromAlgebraic(game,start),getSquareFromAlgebraic(game,end));

            fromTo = activeGame.doComputerMove();
            System.out.println("Computer made the move");

            //String temp = fromTo.get(0).toString().substring(39);
            //System.out.println(temp);

            return new Response("NEW GAME - BLACK COMPUTER, WHITE LOCAL PLAYER - COMPUTER MADE MOVE, LOCAL PLAYER TURN NOW", fromTo.get(0).getAlgebraicNotation(), fromTo.get(1).getAlgebraicNotation());
        }

        //a local move and a computer move
        else if(session.equalsIgnoreCase("newmove")){
            game.invokeMoveAction(getSquareFromAlgebraic(game,end));

            if (shouldDoComputerMove(activeGame)) {
                fromTo = activeGame.doComputerMove();
            }

            return new Response("NEW MOVE BY COMPUTER - LOCAL PLAYER TURN NOW", fromTo.get(0).getPiece().getSymbol(), fromTo.get(1).getPiece().getSymbol());
        }

        else{
            return null;
        }
    }

    @GetMapping("/quit")
    @ResponseBody
    public String quitGame(String session){
        getGame().endGame("Stalemate! Draw!");
        return "Game has been ended";
    }

    private static Square getSquareFromAlgebraic(Game g, String notation)   {
        int pozX = (int)notation.charAt(0) - Square.ASCII_OFFSET;
        int pozY = Chessboard.LAST_SQUARE - Character.getNumericValue(notation.charAt(1))+1;
        Square sq = g.getChessboard().getSquare(pozX, pozY);
        return sq;
    }

    private static boolean shouldDoComputerMove(Game activeGame)
    {
        return activeGame.getSettings().isGameVersusComputer()
                && activeGame.getSettings().getPlayerWhite().getPlayerType() == PlayerType.COMPUTER;
    }


    public Chessboard getChessboard()
    {
        return chessboard;
    }

    public final Settings getSettings()
    {
        return settings;
    }

    public Game getGame(){
        return game;
    }

    public void invokeMoveAction(Square start, Square end)
    {
        getChessboard().move(start, end);

        // switch player
        getGame().nextMove();

        // checkmate or stalemate
        King king;
        if (getSettings().getPlayerWhite() == activePlayer)
        {
            king = getChessboard().getKingWhite();
        } else
        {
            king = getChessboard().getKingBlack();
        }

        switch (king.getKingState())
        {
            case CHECKMATED:
                getGame().endGame(String.format("Checkmate! %s player lose!", king.getPlayer().getColor().toString()));
                break;
            case STEALMATED:
                getGame().endGame("Stalemate! Draw!");
                break;
            case FINE:
                break;
        }
    }

}