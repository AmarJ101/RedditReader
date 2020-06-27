/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;
import dal.BoardDAL;
import entity.Board;
import entity.Host;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import common.ValidationException;
/**
 *
 * @author AmarJ
 */
public class BoardLogic extends GenericLogic<Board, BoardDAL>{
   public static String ID = "id";
    public static String NAME = "name";
    public static String URL = "url";
    public static String HOST_ID = "hostId";

    BoardLogic() {
        super(new BoardDAL());
    }

    public List<Board> getAll() {
        return get(() -> dal().findAll());
    }

    public Board getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<Board> getBoardsWithHostID(int hostId) {
        return get(() -> dal().findByHostId(hostId));
    }

    public Board getBoardWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    public List<Board> getBoardsWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    public Board createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        //same as if condition below
        //        if (parameterMap == null) {
        //            throw new NullPointerException("parameterMap cannot be null");
        //        }

        //create a new Entity object
        Board entity = new Board();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        if (parameterMap.containsKey(HOST_ID)) {
            try {
                Host host = new Host(Integer.parseInt(parameterMap.get(HOST_ID)[0]));
                entity.setHostid(host);
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String url = parameterMap.get(URL)[0];
        String name = parameterMap.get(NAME)[0];

        //validate the data
        validator.accept(url, 255);
        validator.accept(name, 100);

        //set values on entity
        entity.setUrl(url);
        entity.setName(name);

        return entity;
    }

    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Url", "Name", "Hostid");
    }

    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, NAME, HOST_ID);
    }

    public List<?> extractDataAsList(Board e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getName(), e.getHostid());
    }
 
}
