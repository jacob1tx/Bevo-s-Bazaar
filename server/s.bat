@echo off

set "gson=gson-2.10.1.jar"

cd src

if not exist "%gson%" (
    echo Downloading %gson%...
    curl -o "%gson%" "https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/%gson%"
    if %errorlevel% equ 0 (
        echo %gson% download successful
    ) else (
        echo %gson% download failed
        exit /b 1
    )
)

javac -cp %gson% src/*.java
java -cp .;%gson% src.Server