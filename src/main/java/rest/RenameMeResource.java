package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utils.EMF_Creator;
import facades.FacadeExample;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import webscraper.TagCounter;
import dtos.TagDTO;
import webscraper.Webscraper;

//Todo Remove or change relevant parts before ACTUAL use
@Path("xxx")
public class RenameMeResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final FacadeExample FACADE =  FacadeExample.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @Path("expensive")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsFastAndExpensive() throws NotImplementedException, InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        List<TagCounter> dataFetched = Webscraper.runParallel();
        long endTime = System.nanoTime() - startTime;
        return TagDTO.getTagsAsJson("Parallel fetching", dataFetched, endTime);
    }
    
    @Path("sequental")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsSequental() {
        long startTime = System.nanoTime();
        List<TagCounter> dataFeched = Webscraper.runSequental();
        long endTime = System.nanoTime() - startTime;
        return TagDTO.getTagsAsJson("Sequental fetching", dataFeched, endTime);
    }

    @Path("cheap")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTagsFastAndCheap() throws NotImplementedException, InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        List<TagDTO> dataFetched = Webscraper.runFastAndCheapParallel();
        long endTime = System.nanoTime() - startTime;
        return TagDTO.getTagsAsJson2("Parallel fetching", dataFetched, endTime);
    }
    
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getRenameMeCount() {
        long count = FACADE.getRenameMeCount();
        //System.out.println("--------------->"+count);
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }
}
