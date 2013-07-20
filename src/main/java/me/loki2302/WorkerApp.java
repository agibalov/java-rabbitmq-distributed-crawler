package me.loki2302;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import me.loki2302.tasks.ProcessAbcTask;
import me.loki2302.tasks.ProcessLetterTask;
import me.loki2302.tasks.ProcessPlayerTask;
import me.loki2302.tasks.Task;
import me.loki2302.tasks.TaskVisitor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WorkerApp implements TaskVisitor {
    private final WorkerService workerService;
    
    public WorkerApp(WorkerService workerService) {
        this.workerService = workerService;
    }
    
    public void run() {
        while(true) {
            Task task = workerService.consumeTask();            
            while(true) {
                try {
                    task.accept(this);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }                    
            }
        }
    }

    @Override
    public void visitAbcTask(ProcessAbcTask task) throws IOException {
        Document doc = Jsoup.connect(task.url).get();
        Elements elements = doc.select("#playerSearch > .lastInitial > a");
        for(Element e : elements) {
            ProcessLetterTask processLetterTask = new ProcessLetterTask();
            processLetterTask.url = e.attr("abs:href");
            processLetterTask.processPagination = true;
            workerService.submitTask(processLetterTask);
        }
    }

    @Override
    public void visitLetterTask(ProcessLetterTask task) throws IOException {
        Document doc = Jsoup.connect(task.url).get();
        Elements elements = doc.select("table.data > tbody > tr a[href*=player]");
        for(Element e : elements) {
            ProcessPlayerTask processPlayerTask = new ProcessPlayerTask();
            processPlayerTask.url = e.attr("abs:href");
            workerService.submitTask(processPlayerTask);
        }
        
        if(!task.processPagination) {
            return;
        }
        
        elements = doc.select(".pageNumbers > a");
        Set<String> urls = new HashSet<String>();
        for(Element e : elements) {
            urls.add(e.attr("abs:href"));               
        }
        
        for(String url : urls) {
            ProcessLetterTask processLetterTask = new ProcessLetterTask();
            processLetterTask.url = url;
            processLetterTask.processPagination = false;
            workerService.submitTask(processLetterTask);
        }
    }

    @Override
    public void visitPlayerTask(ProcessPlayerTask task) throws IOException {
        Document doc = Jsoup.connect(task.url).get();        
        Element nameElement = doc.select("#tombstone h1 *").first();
        String playerName = fixString(nameElement.ownText());
        workerService.submitResult(playerName);
    }
    
    private static String fixString(String s) {
        return s.replace(String.valueOf((char)160), " ").trim();
    }
}