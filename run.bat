@echo off
rem NE Simulator start script
rem Converted to Maven project

rem Run with fat JAR (build first: mvn package -DskipTests)
java -Dfile.encoding=UTF-8 -jar target\NESimulator-1.0.0-jar-with-dependencies.jar
