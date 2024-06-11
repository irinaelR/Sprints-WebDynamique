@echo off
setlocal

rem Set the project test path
set PROJECT_TEST_PATH=C:\Users\climi\Documents\SprintsWebDynamique\TestProject-Sprints

rem Set the source directory
set SOURCE_DIR=src

rem Set the destination JAR file
set JAR_FILE=FrameWorkS4.jar

rem Set the lib directory containing dependencies
set LIB_DIR=lib

:: Compilation des fichiers .java dans src avec les options suivantes
:: Note: Assurez-vous que le chemin vers le compilateur Java (javac) est correctement configuré dans votre variable d'environnement PATH.
:: Créer une liste de tous les fichiers .java dans le répertoire src et ses sous-répertoires
dir /s /B "%SOURCE_DIR%\*.java" > sources.txt
:: Créer une liste de tous les fichiers .jar dans le répertoire lib et ses sous-répertoires
dir /s /B "%LIB_DIR%\*.jar" > libs.txt
:: Construire le classpath
set "classpath="
for /F "delims=" %%i in (libs.txt) do set "classpath=!classpath!%%i;"

@REM echo "%classpath%"

:: Exécuter la commande javac
javac -parameters -d bin -cp @libs.txt @sources.txt
:: Supprimer les fichiers sources.txt et libs.txt après la compilation
del sources.txt
del libs.txt

echo Compilation terminée

rem Create JAR file
echo Creating JAR file...
cd bin
jar cf ../%JAR_FILE% *
cd ..

echo JAR file created: %JAR_FILE%

rem Copy JAR file to project test path's lib directory
echo Copying JAR file to project test path's lib directory...
copy /y %JAR_FILE% "%PROJECT_TEST_PATH%\lib\%JAR_FILE%

del %JAR_FILE%

endlocal
pause