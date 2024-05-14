package mg.itu.prom16;

import java.io.*;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import framework.annotations.Controller;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * FrontController
 */
public class FrontController extends HttpServlet {
    boolean hasChecked = false;
    List<String> controllerNamesList;

    public boolean isHasChecked() {
        return hasChecked;
    }

    public void setHasChecked(boolean hasChecked) {
        this.hasChecked = hasChecked;
    }

    public List<String> getControllerNamesList() {
        return controllerNamesList;
    }

    public void setControllerNamesList(List<String> controllerNamesList) {
        this.controllerNamesList = controllerNamesList;
    }

    public List<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        String path = "WEB-INF/classes/" + packageName.replace(".", "/");
        String realPath = getServletContext().getRealPath(path);

        File directory = new File(realPath);
        File[] files = directory.listFiles();

        for(File f : files) {
            if(f.isFile() && f.getName().endsWith(".class")) {
                String className = packageName + "." + f.getName().split(".class")[0];
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    public void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        PrintWriter out = resp.getWriter();
        if(!hasChecked) {
            ServletContext context = getServletContext();
            String packageName = context.getInitParameter("controller-package");

            List<String> controllers = this.getControllerNamesList();
            controllers = new ArrayList<>(); // s'assurer que la variable n'est pas null mais au moins vide
            
            try {
                
                Class<? extends Annotation> controllerAnnotation = (Class<? extends Annotation>) Class.forName("framework.annotations.Controller");
                
                // String currentDir = ;
                // String currentDir = System.getProperty("user.dir") + "/webapps" + context.getContextPath() + "/WEB-INF/classes";

                // out.println(currentDir); 

                // String projectPath = System.getProperty("user.dir") + "/"

                List<Class<?>> allClasses = this.findClasses(packageName);

                out.println("All classes");
                for (Class<?> classe : allClasses) {
                    if(classe.isAnnotationPresent(controllerAnnotation)) {
                        controllers.add(classe.getName());
                    }
                    out.println(classe);
                }

                this.setControllerNamesList(controllers);
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }

        out.println("Controllers");
        for (String name : controllerNamesList) {
            out.println("- " + name);
        }
        // // getting the URL requested by the client
        // String requestedURL = req.getRequestURL().toString();
        // String output = "Requested URL: "
        //                 + requestedURL;
        // // printing both
        // try (PrintWriter out = resp.getWriter()) {
        //     out.println(output);
        // }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processRequest(req, resp);
    }

    
}