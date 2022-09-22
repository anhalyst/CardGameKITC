package kings.server.administration;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class dbController {
    private Connection conn;
    private Statement stm;


    public dbController(String url) {
        try{
            this.conn = DriverManager.getConnection(url);
            this.stm = this.conn.createStatement();
            System.out.println("Connected to Database.");
        } catch (Exception e ){
            e.printStackTrace();
        }
    }

    /** add new entry into the database
     * @param name name of new user, this should be unique
     * @param pass password of new user
     */
    protected void insertNewPlayer(String name, String pass) {
        StringBuilder q = new StringBuilder("INSERT INTO players VALUES");
        q.append("('").append(name).append("','").append(pass).append("', 0);");

        try{
            stm.execute(q.toString());
            System.out.println("Successfully added new member "+ name);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /** delete data of a player from the database
     * @param name name of the player to be deleted
     */
    protected void deletePlayer(String name){
        String query = "DELETE FROM players WHERE name = '"+ name +"';";

        try{
            stm.execute(query);
            System.out.println("Delete successfully player "+ name);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /** check if the given username and password matches with any entry in database
     * @param username username read from input
     * @param password password read from input
     */
    protected boolean passwordCheck(String username, String password){
        StringBuilder q = new StringBuilder("SELECT * FROM players WHERE ");
        q.append("name ='").append(username).append("' AND password='").append(password).append("';");
        boolean match = false;

        try{
            ResultSet r = this.stm.executeQuery(q.toString());
            match = r.next();
        } catch (Exception e){
            e.printStackTrace();
        }
        return match;
    }

    /** check if a player with given name already exists in database
     * @param name username to be checked
     */
    protected boolean nameAlreadyExists(String name){
        String q  = "SELECT name FROM players WHERE name ='" +name+"';";
        boolean nameExisted = false;
        try {
            ResultSet r = stm.executeQuery(q);
            nameExisted = r.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nameExisted;
    }

    /** return n players with the highest scores in the database
     * @param n decides how many records should be returned
     */
    protected List<Map.Entry<String,Integer>> topHighScore(Integer n){
        String query = "SELECT name, score FROM players ORDER BY score DESC LIMIT " + n + ";";
        List<Map.Entry<String, Integer>> topPlayers = new ArrayList<>();
        try{
            ResultSet r = stm.executeQuery(query);
            while (r.next()){
                Map.Entry<String,Integer> entry = new AbstractMap.SimpleEntry<>(r.getString("name"),r.getInt("score"));
                topPlayers.add(entry);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return topPlayers;
    }

    /** return score of a player
     * @param name name of a player
     */
    protected int playerScore(String name){
        String query = "SELECT score FROM players WHERE name = '"+ name + "'";
        int score = -1;
        try{
            ResultSet r = stm.executeQuery(query);
            score = r.getInt("score");
        }catch (Exception e){
            e.printStackTrace();
        }
        return score;
    }

    /** update new score for an existing player
     * @param name name of a player
     * @param score new score of the player
     */
    public void updateScore(String name, Integer score){

        int oldScore = playerScore(name);
        oldScore+=1;

        String query = "UPDATE players SET score = " + oldScore +" WHERE name = '"+ name +"';";
        try{
            stm.execute(query);
            System.out.println("Update score successfully for "+ name);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /** close connection with database
     */
    protected void closeConnection(){
        try{
            conn.close();
            System.out.println("Disconnected from Database.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void updatePassword(String username, String newPwd){
        String q = "UPDATE players SET password = '" + newPwd +"' WHERE name = '"+username+"';";
        try {
            stm.execute(q);
            System.out.println("Update password successfully for "+ username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
