package logic;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public abstract class LogicFactory<T> {
        
    private static String PACKAGE = "logic";
    private static String SUFFIX = "Logic";
    
    private LogicFactory(){
        
    }
  
    public static <T> T getFor(String entityName){
        
        try {
            Class<?> genericClass = Class.forName(PACKAGE + "." + entityName + SUFFIX);
            return getFor((Class<T>) genericClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
   
    public static <T> T getFor(Class<T> type){
        try{
           return type.getDeclaredConstructor().newInstance();    
        } catch(InstantiationException e){
            e.printStackTrace();
        } catch(IllegalAccessException e){
            e.printStackTrace();
        } catch(IllegalArgumentException e){
            e.printStackTrace();
        } catch(InvocationTargetException e){
            e.printStackTrace();
        } catch(NoSuchMethodException e){
            e.printStackTrace();
        } catch(SecurityException e){
            e.printStackTrace();
        }
       return null;
    }
     
 

    //this is just a place holder to keep the code working.
/*    public static AccountLogic getFor(String account) {
        return new AccountLogic();
    }
*/  
}