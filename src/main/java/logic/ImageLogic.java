/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import dal.ImageDAL;
import entity.Image;
import entity.Board;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import common.ValidationException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author AmarJ
 */
public class ImageLogic  extends GenericLogic<Image, ImageDAL> {
     public static SimpleDateFormat FORMATTER = 
            new SimpleDateFormat("YYYY,MM,DD hh:mm:ss");
    public static String ID = "id";
    public static String URL = "url";
    public static String TITLE = "title";
    public static String DATE = "date";
    public static String LOCAL_PATH = "localPath";
    public static String BOARD_ID = "boardId";
    
    ImageLogic(){
        super(new ImageDAL());
    }
    
    public List<Image> getAll(){
        return get(() -> dal().findAll());
    }
    
    public Image getWithId(int id){
        return get(() -> dal().findById(id));
    }
    
    public List<Image> getImagesWithBoardId(int boardID) {
        return get(() -> dal().findByBoardId(boardID));
    }
    
    public List<Image> getImagesWithTitle(String title){
        return get(() -> dal().findByTitle(title));
    }
    
    public Image getImageWithUrl(String url){
        return get(() -> dal().findByUrl(url));
    }
    
    public Image getImageWithLocalPath(String path){
        return get(() -> dal().findByLocalPath(path));
    }
    
    public List<Image> getImagesWithDate(Date date){
        return get(() -> dal().findByDate(date));
    }
    
    public String convertDate(Date date){
        String dateString = FORMATTER.format(date);
        return dateString; 
    }
    
    public Image createEntity(Map<String, String[]> parameterMap){
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
       

        //create a new Entity object
        Image entity = new Image();

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

        if (parameterMap.containsKey(BOARD_ID)) {
            try {
                Board board = new Board(Integer.parseInt(parameterMap.get(BOARD_ID)[0]));
                entity.setBoard(board);
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }
        
        if (parameterMap.containsKey(DATE)) {
            try {
                String dateString = parameterMap.get(DATE)[0];
                entity.setDate(FORMATTER.parse(dateString));
            } catch (ParseException ex) {
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
        String title = parameterMap.get(TITLE)[0];
        String url = parameterMap.get(URL)[0];
        String localPath = parameterMap.get(LOCAL_PATH)[0];

        //validate the data
        validator.accept(title, 255);
        validator.accept(url, 100);
        validator.accept(localPath, 100);
        
        //set values on entity
        entity.setTitle(title);
        entity.setUrl(url);
        entity.setLocalPath(localPath);

        return entity;
    }
    
    public Image updateEntity(Map<String, String[]> parameterMap){
        return null;
    }
 
    
    public List<String> getColumnNames(){
        return Arrays.asList("ID", "Title", "Url", "LocalPath", "Date", "BoardID");
    }
    
    public List<String> getColumnCodes(){
        return Arrays.asList(ID, TITLE, URL, LOCAL_PATH, DATE, BOARD_ID);
    }
    
    public List<?> extractDataAsList(Image e){
        return Arrays.asList(e.getId(), e.getTitle(), e.getUrl(), e.getLocalPath(), e.getDate(), e.getBoard());
    }
}
