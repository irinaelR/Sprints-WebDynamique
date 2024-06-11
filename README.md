## Sprints Web Dynamique

### Versions de Java
Ce projet a été créé avec JDK 19 et testé sur Tomcat 10.1.
Il nécessite l'ajout d'une librairie servlet-api relative à la version de Tomcat utilisée. Pour le développement de ce projet, jakarta.servlet-api-6.1.0-M2.jar est la librairie utilisée. 

### Sprint 0
07/05/2024
Création d'un FrontController qui va simplement afficher l'url demandée après "/"
Le fichier compileClasses.bat est utilisé pour compiler la classe dans un fichier jar, puis il est copié vers le projet de test

### Sprint 1
14/05/2024
A présent le FrontController accessible depuis "/" va lister les classes annotées comme Controllers dans le projet de test de l'utilisateur. 
Dans le projet, dans le fichier web.xml, il faudra déclarer une variable comme suit pour indiquer dans quel package se trouvent les Controllers : 
```
<context-param>
    <param-name>controller-package</param-name>
    <param-value>votre.package.exemple</param-value>
</context-param>
```
A noter que le param-value *doit absolument* être "controller-package" pour que le système fonctionne. 

### Sprint 2
17/05/2024
Le FrontController peut désormais afficher la méthode (et la classe qui la contient) annotée pour l'url demandée. 
```
package my.test.app.controllers;

import framework.annotations.*;

@Controller
public class TestController {

    @Get(url="liste")
    public void listeEmp() {

    }

    @Get(url="add")
    public void insertEmp() {
        
    }
}

```
Par exemple, ici, si l'on tape l'url http://localhost:8080/Votre-Projet/liste, le FrontController va nous afficher :
```
Controllers
- my.test.app.controllers.TestController
The controller my.test.app.controllers.TestController will call the method listeEmp
```

\* IMPORTANT : L'url à mettre dans l'annotation Get ne devrait pas contenir de slash '/'

### Sprint 3
28/05/2024

Une fois la méthode correspondant à l'URL est trouvée, nous allons l'appeler. Pour l'instant, les méthodes retournent simplement un objet String à afficher. 

### Sprint 4
31/05/2024

A présent, nous allons manipuler deux types de retour pour les fonctions annotées Get dans les controllers du projet de test. La nouvelle classe **ModelAndView** possède un attribut url, qui sera la vue vers laquelle on va être redirigé. On peut également ajouter des attributs à la requête à travers sa méthode *addObject(String key, Object value)*. Ces objects seront accessibles à la vue non pas comme paramètres mais comme attributs. 
Ci-après un exemple de code. 

#### Structure du projet
Modifiez vos URLs en fonction de votre structure

```
Tomcat
|_ webapps
    |_ Projet
        |_ META-INF
        |_ WEB-INF
        |_ liste.jsp

```

#### Côté controller

```
@Get(url="liste")
public ModelAndView listeEmp() {
    ModelAndView mv = new ModelAndView("liste.jsp");

    List<String> listeEmp = List.of("John Doe", "Jane Doe", "Jason Todd");
    mv.addObject("listeEmp", listeEmp);

    return mv;
}
```

#### Côté web

```
<%@ page import = "java.util.List" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste</title>
</head>
<body>
    <ul>
        <%
        
        List<String> listeEmp = (List<String>) request.getAttribute("listeEmp");
        for(String str : listeEmp) {
            out.println("<li>" + str + "</li>");
        }
        
        %>
    </ul>
</body>
</html>
```

### Sprint 5
04/06/2024

Cette fois-ci, nous allons nous assurer que le projet gère correctement les exceptions. 
- Le package de controller dans `<context-param>` ne doit être ni **vide** ni **inexistant**
- Le type de retour des fonctions annotées Get **doivent** être soit de type String (=> réponse text/plain) soit de type ModelAndView (=> redirection vers la vue choisie)
- Plusieurs fonctions annotées Get ne peuvent pas avoir le même URL même si elles sont dans des controllers différents

### Sprint 6
11/06/2024

A présent, les méthodes mappées Get peuvent avoir des paramètres (mais uniquement de type String pour l'instant). Il y a deux manières de faire correspondre les paramètres HTTP et les arguments des fonctions :
- utiliser le nom des paramètres HTTP en guise de nom des arguments
- utiliser la nouvelle annotation Param en lui donnant un 'name' qui correspondra au paramètre HTTP 

Voici un exemple des deux :
```
@Get(url="traitement")
public ModelAndView traitementForm(String message, @Param(name = "message2") String secret) {
    ModelAndView mv = new ModelAndView("display.jsp");

    mv.addObject("displayText", message + "(" + secret + ")");

    return mv;
}
```
Dans notre cas ici, le FrontController va rechercher dans la requête les paramètres de noms "message" et "message2". 