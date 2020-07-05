/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Account;
import entity.Board;
import entity.Image;
import logic.AccountLogic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.AccountLogic;
import logic.ImageLogic;
import logic.BoardLogic;
import logic.HostLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;


/**
 *
 * @author AmarJ
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet{
 
    private String errorMessage = null;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String cssTag = "<link rel='stylesheet' type='text/css' href='../../webapp/style/imageview.css'>";
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println(cssTag);
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/imageview.css\">");
            out.println("<title>ImageView</title>");
            out.println("</head>");            
            out.println("<body>");
            
//            ImageLogic imageLogic = new ImageLogic();
            ImageLogic imageLogic = LogicFactory.getFor("image");
            List<Image> imageList = imageLogic.getAll();
            for (Image image : imageList){
            
                
            out.println("<div align=\"center\">");
             out.println("<div align=\"center\" class=\"imageContainer\">");
             out.println("<img class=\"imageThumb\" src=\"image/"+FileUtility.getFileName(image.getUrl())+"\"/>");
             out.println("</div>");
            out.println("</div>");
            
            
            
        }
               out.println("</body>");
            out.println("</html>");
        }
    }
     private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }
     
     ///////////////
     /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       
        String path=System.getProperty("user.home") +"/My Documents/Reddit Images/";
        FileUtility.createDirectory(path);
        ImageLogic imageLogic = LogicFactory.getFor("Image");
        BoardLogic boardLogic = LogicFactory.getFor("Board");       
        Board bEntity = boardLogic.getAll().get(0);
        
        Reddit obj= new Reddit();
          obj.authenticate().buildRedditPagesConfig(bEntity.getName(), 20, Sort.BEST);
         //create a lambda that accepts post
        Consumer<Post> saveImage = (Post post) -> {
            //if post is an image and SFW
            if (post.isImage() && !post.isOver18() && imageLogic.getImageWithUrl(post.getUrl())==null) {
                //get the path for the image which is unique
                String url = post.getUrl();
               //save it in img directory
                FileUtility.downloadAndSaveFile(url, path);
                
                
                 Map<String, String[]> imageMap = new HashMap<>();
                imageMap.put(ImageLogic.TITLE, new String[]{post.getTitle()});
                imageMap.put(ImageLogic.LOCAL_PATH, new String[]{path});
                imageMap.put(ImageLogic.URL, new String[]{url});
                imageMap.put(ImageLogic.DATE, new String[]{imageLogic.convertDate(post.getDate())});
                imageMap.put(ImageLogic.BOARD_ID, new String[]{bEntity.getId().toString()});
                Image image= imageLogic.createEntity(imageMap);

                //Add image to DB
                 imageLogic.add(image);
                
            }
           
        };
        
        
        //Process Request
         obj.requestNextPage().proccessNextPage(saveImage);
         processRequest(request, response);
   }   
    
    
    
    
     //////////////
      /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        

        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return " Image View ";
    }

    private static final boolean DEBUG = true;

    public void log( String msg) {
        if(DEBUG){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log( message);
        }
    }

    public void log( String msg, Throwable t) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log( message, t);
    }
    
}
