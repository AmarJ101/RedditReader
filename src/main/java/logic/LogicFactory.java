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
        
        String[] parts = entityName.split("_");
        String className = "";
        for (String part : parts) {
            className = className + part.substring(0, 1).toUpperCase() +
                    part.substring(1).toLowerCase();
        }
        System.out.println("Class name: " + className);
        
        try {
            Class<?> genericClass = Class.forName(PACKAGE + "." + className + SUFFIX);
            return getFor((Class<T>) genericClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(IllegalArgumentException ex){
            ex.printStackTrace();
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