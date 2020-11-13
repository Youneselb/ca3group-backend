package webscraper;

import dtos.TagDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

class GetTagDTO implements Callable<TagDTO> {
    String url;   
    GetTagDTO(String url) {
        this.url  = url;
    }

    @Override
    public TagDTO call() throws Exception {
        TagCounter tc = new TagCounter(url);
        tc.doWork();
        return new TagDTO(tc);
    }
}

public class Webscraper {

    public static List<TagCounter> runSequental() {
        List<TagCounter> urls = new ArrayList();
        urls.add(new TagCounter("https://www.fck.dk"));
        urls.add(new TagCounter("https://www.google.com"));
        urls.add(new TagCounter("https://www.politiken.dk"));
        urls.add(new TagCounter("https://www.cphbusiness.dk"));
        urls.add(new TagCounter("https://www.youtube.com"));
        for (TagCounter tc : urls) {
            tc.doWork();
        }
        return urls;
    }

    public static List<TagCounter> runParallel() throws InterruptedException {
        List<TagCounter> urls = new ArrayList();
        urls.add(new TagCounter("https://www.fck.dk"));
        urls.add(new TagCounter("https://www.google.com"));
        urls.add(new TagCounter("https://www.politiken.dk"));
        urls.add(new TagCounter("https://www.cphbusiness.dk"));
        urls.add(new TagCounter("https://www.youtube.com"));
        
        // Brug den her hvis du  ved hvor mange treads der er Executors.newFixedThreadPool("Antallet af threads);
        ExecutorService worker = Executors.newCachedThreadPool();
        urls.forEach(tagCounter -> {
            Runnable task = () -> {
              tagCounter.doWork();
            };
            // Hvis du bruger fixed threads, skal du ændre .execute til .submit
            worker.execute(task);
        });
        worker.shutdown();
        worker.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        return urls;
    }
    
    public static List<TagDTO> runFastAndCheapParallel() throws NotImplementedException, InterruptedException, ExecutionException {
        //Skal bruge callables, tage en url, returnere Tagcounter
        //callable skal kunne lave tagcounters og lægge dem ind i futures
        // skabelon, simplefuturecallable
        // Implementer executor!!
        ExecutorService executor = Executors.newCachedThreadPool();
        String[] urls = {"https://www.fck.dk","https://www.google.com","https://www.politiken.dk","https://www.cphbusiness.dk","https://www.youtube.com"};
        List<TagDTO> tagDTOs = new ArrayList<>();
        List<Future<TagDTO>> futures = new ArrayList<>();
        
        for(String url : urls) {
            Future<TagDTO> future = executor.submit(new GetTagDTO(url));
            futures.add(future); 
        }
        for (Future<TagDTO> future : futures) {
            tagDTOs.add(future.get());
        }
        return tagDTOs;
    }

    public static void main(String[] args) throws Exception {
        long timeSequental;
        long start = System.nanoTime();

        List<TagCounter> fetchedData = new Webscraper().runSequental();
        long end = System.nanoTime();
        timeSequental = end - start;
        System.out.println("Time Sequential: " + ((timeSequental) / 1_000_000) + " ms.");

        for (TagCounter tc : fetchedData) {
            System.out.println("Title: " + tc.getTitle());
            System.out.println("Div's: " + tc.getDivCount());
            System.out.println("Body's: " + tc.getBodyCount());
            System.out.println("----------------------------------");
        }

        start = System.nanoTime();
        //TODO Add your parrallel calculation here     
        long timeParallel = System.nanoTime() - start;
        System.out.println("Time Parallel: " + ((timeParallel) / 1_000_000) + " ms.");
        System.out.println("Paralle was " + timeSequental / timeParallel + " times faster");

    }
}
