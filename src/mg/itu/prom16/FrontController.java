package mg.itu.prom16;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import framework.annotations.Get;
import framework.utilities.Mapping;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * FrontController
 */
public class FrontController extends HttpServlet {
    List<String> controllerNamesList;
    HashMap<String, Mapping> urlToMethods;

    public HashMap<String, Mapping> getUrlToMethods() {
        return urlToMethods;
    }

    public void setUrlToMethods(HashMap<String, Mapping> urlToMethods) {
        this.urlToMethods = urlToMethods;
    }

    public List<String> getControllerNamesList() {
        return controllerNamesList;
    }

    public void setControllerNamesList(List<String> controllerNamesList) {
        this.controllerNamesList = controllerNamesList;
    }

    /*
     * findClasses will fetch all classes inside the package 'packageName'
     * and return them as a List
     */
    public List<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        // making sure the path to the controller package is correct
        String path = "WEB-INF/classes/" + packageName.replace(".", "/");
        String realPath = getServletContext().getRealPath(path);

        File directory = new File(realPath);
        File[] files = directory.listFiles();

        for(File f : files) {
            // filtering class files
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

        // simply printing the controllers inside the controller package
        // out.println("Controllers");
        // for (String name : controllerNamesList) {
        //     out.println("- " + name);
        // }
        
        // out.println();

        // getting the URL requested by the client
        String requestedURL = req.getRequestURL().toString();
        String[] partedReq = requestedURL.split("/");
        String urlToSearch = partedReq[partedReq.length - 1];
        
        // searching for that URL inside of our HashMap
        if(urlToMethods.containsKey(urlToSearch)) {
            Mapping m = urlToMethods.get(urlToSearch);
            // String output = "The controller " + m.getClassName() + " will call the method " + m.getMethodName();
            // out.println(output);
            // out.println();
            try {
                String result = (String) m.invoke();
                out.println(result);
            } catch (Exception e) {
                out.println(e.getMessage());
            }
            
        } else {
            out.println("No method matching '" + urlToSearch + "' to call");
        }

        out.flush();
        out.close();
        
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();

        // fetching the controller package's value from web.xml
        ServletContext context = getServletContext();
        String packageName = context.getInitParameter("controller-package");

        List<String> controllers = this.getControllerNamesList();
        controllers = new ArrayList<>(); // making sure the variable isn't null and emptying it everytime

        HashMap<String, Mapping> urls = this.getUrlToMethods();
        urls = new HashMap<>(); // making sure the variable isn't null and emptying it everytime
        
        try {
            // annotation classes
            Class<? extends Annotation> controllerAnnotation = (Class<? extends Annotation>) Class.forName("framework.annotations.Controller");
            Class<? extends Annotation> getAnnotation = (Class<? extends Annotation>) Class.forName("framework.annotations.Get");

            // fetching all classes in the controllers package
            List<Class<?>> allClasses = this.findClasses(packageName);

            for (Class<?> classe : allClasses) {
                // checking which of these classes are controllers
                if(classe.isAnnotationPresent(controllerAnnotation)) {
                    controllers.add(classe.getName());

                    // iterating through all the methods of the controller classes to check which ones are annotated with Get
                    Method[] allMethods = classe.getMethods();
                    for (Method m : allMethods) {
                        if (m.isAnnotationPresent(getAnnotation)) {
                            // when a method is annotated with Get, we fetch its url value and create a new couple in the urlsToMethods Map
                            Get mGetAnnotation = (Get) m.getAnnotation(getAnnotation);
                            urls.put(mGetAnnotation.url(), new Mapping(classe.getName(), m.getName()));
                        }
                    }

                }
            }

            // setting the values of the attributes
            this.setControllerNamesList(controllers);
            this.setUrlToMethods(urls);
        } catch (Exception e) {
            
        }
    }

    
}