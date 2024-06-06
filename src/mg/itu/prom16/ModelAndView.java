package mg.itu.prom16;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ModelAndView {
    String url;
    HashMap<String, Object> data;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public ModelAndView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }
    public ModelAndView() {
        this.data = new HashMap<>();
    }

    public void addObject(String key, Object toAdd) {
        this.data.put(key, toAdd);
    }

    public void sendToView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        for(Entry<String, Object> element : data.entrySet()) {
            req.setAttribute(element.getKey(), element.getValue());
        }

        RequestDispatcher dispatcher = req.getRequestDispatcher(this.getUrl());
        dispatcher.forward(req, resp);
    }
}
