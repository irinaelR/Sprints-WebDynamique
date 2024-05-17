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