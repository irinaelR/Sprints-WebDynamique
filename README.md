## Sprints Web Dynamique

### Dépendances
Ce projet a été créé avec JDK 19 et testé sur Tomcat 10.1.
Il nécessite l'ajout d'une librairie servlet-api relative à la version de Tomcat utilisée. Pour le développement de ce projet, jakarta.servlet-api-6.1.0-M2.jar est la librairie utilisée. 
Sprint 9 update : gson-2.11.0.jar

### Sprint 0

Création d'un FrontController qui va simplement afficher l'url demandée après "/"
Le fichier compileClasses.bat est utilisé pour compiler la classe dans un fichier jar, puis il est copié vers le projet de test

### Sprint 1

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

Le FrontController peut désormais afficher la méthode (et la classe qui la contient) annotée pour l'url demandée. 
```
package my.test.app.controllers;

import framework.annotations.*;

@Controller
public class TestController {

    @Map(url="liste")
    public void listeEmp() {

    }

    @Map(url="add")
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

Une fois la méthode correspondant à l'URL est trouvée, nous allons l'appeler. Pour l'instant, les méthodes retournent simplement un objet String à afficher. 

### Sprint 4

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
@Map(url="liste")
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

Cette fois-ci, nous allons nous assurer que le projet gère correctement les exceptions. 
- Le package de controller dans `<context-param>` ne doit être ni **vide** ni **inexistant**
- Le type de retour des fonctions annotées Get **doivent** être soit de type String (=> réponse text/plain) soit de type ModelAndView (=> redirection vers la vue choisie)
- Plusieurs fonctions annotées Get ne peuvent pas avoir le même URL même si elles sont dans des controllers différents

### Sprint 6

A présent, les méthodes mappées Get peuvent avoir des paramètres (mais uniquement de type String pour l'instant). Il y a deux manières de faire correspondre les paramètres HTTP et les arguments des fonctions :
- utiliser le nom des paramètres HTTP en guise de nom des arguments
- utiliser la nouvelle annotation Param en lui donnant un 'name' qui correspondra au paramètre HTTP 

Voici un exemple des deux :
```
@Map(url="traitement")
public ModelAndView traitementForm(String message, @Param(name = "message2") String secret) {
    ModelAndView mv = new ModelAndView("display.jsp");

    mv.addObject("displayText", message + "(" + secret + ")");

    return mv;
}
```
Dans notre cas ici, le FrontController va rechercher dans la requête les paramètres de noms "message" et "message2". 

### Sprint 7

Les méthodes mappées dans les Controllers peuvent prendre des types d'arguments non primitifs, *incluant* les objets personnalisés du développeur (il faudra néanmoins des getters standards). Le framework va faire le lien dynamiquement entre les attributs des objets, le nom de l'objet en argument, et les noms des champs de formulaire HTML. Mais, si on préfère donner un alias aux attributs de classe, on peut utiliser l'annotation `@Field`. 

Les règles d'annotation de paramètres des méthodes ont changé : à présent, l'annotation @Param est obligatoire pour définir le nom du paramètre à chercher dans le FormData. 

Voici un exemple :

> **Person.java**
> ```
> package entities;
>
> public class Person {
>   String nom;
>
>   @Field(name="pn")
>   String prenom;
>
>   int age;
> }
> ```

> **formObject.jsp**
> ```
> <!DOCTYPE html>
>    <html lang="en">
>    <head>
>        <meta charset="UTF-8">
>        <meta name="viewport" content="width=device-width, initial-scale=1.0">
>        <title>Form Object</title>
>    </head>
>    <body>
>        <form action="create-object-traitement" method="get">
>           <label>Nom</label>
>            <input type="text" name="p.nom">
>
>            <label>Prenom</label>
>            <input type="text" name="p.pn">
>
>            <label>Age</label>
>            <input type="number" name="p.age">
>
>            <label>Message</label>
>            <input type="text" name="hello">
>
>            <input type="submit" value="OK">
>        </form>
>    </body>
>    </html>
> ```

> **Controller.java**
> ```
>    @Map(url="create-object-traitement")
>    public ModelAndView createObjectForm(@Param(name="p") Person p, @Param(name = "hello") String message) {
>       ModelAndView mv = new ModelAndView("displayObject.jsp");
>
>       mv.addObject("personne", p);
>       mv.addObject("message", message);
>
>       return mv;
>    }
> ```

### Sprint 8

Nous pouvons désormais utiliser des sessions grâce au framework. Un objet `CustomSession` est à mettre en argument aux fonctions qui veulent utiliser la session dans les Controller. Pas besoin de définir cet objet où que ce soit, il doit seulement être présent parmi les paramètres de la méthode. C'est le seul argument qui n'a pas besoin d'annotation `@Param`. 
CustomSession est un Dictionaire, donc elle propose les 4 méthodes : get, add, remove et update pour accéder aux données identifiées par des clés (String). 

```
@Map(url="test-add-session")
public String letsAdd(@Param(name = "message") String m, CustomSession c) {
    c.add("message", m);
    return "Added successfully";
}

@Map(url = "test-get-session")
public String letsGet(CustomSession c) {
    return "Got : " + c.get("message");
}

@Map(url = "test-update-session")
public String letsUpdate(@Param(name = "message") String newMessage, CustomSession c) {
    c.update("message", newMessage);
    return "Updated successfully";
}

@Map(url = "test-delete-session")
public String letsDelete(CustomSession c) {
    c.remove("message");
    return "Removed successfully";
}
```

### Sprint 9

Les méthodes de Controllers peuvent maintenant être annotées pour renvoyer du JSON (d'où la *nécessité* de la librairie gson). Ces fonctions peuvent avoir n'importe quel type de retour à part `void`. Les objets seront automatiquement convertis en JSON et, pour les retours ModelAndView, l'attribut `data` va être converti à la place. 
Tout ceci va se faire par l'annotation de fonction `@RestAPI`. 

### Sprint 10 + 11

Une nouvelle annotation `@Verb` pour les méthodes a été ajoutée. Elle possède un paramètre String qui se nomme method, pour préciser la méthode HTTP à utiliser pour appeler l'url. Si method, ou l'annotation elle-même, est ommis, la valeur par défaut est GET. 
Le framework interdit l'existence de deux méthodes de la même classe ayant le même nom (les différences d'arguments sont caduques), mappées au même URL. Pareillement, on ne peut également pas avoir plusieurs méthodes annotées avec le même VERB. 
Si on essaie d'appeler un URL qui ne "connaît" pas la méthode HTTP, il se produira une erreur 405. 

### Sprint 12

L'upload de fichier peut se faire en utilisant la classe `FileForm` en argument des méthodes de controllers. Elle possède deux arguments : fileName et fileBytes, ainsi qu'une méthode `copyTo` pour écrire le fichier dans le dossier du choix du développeur. 