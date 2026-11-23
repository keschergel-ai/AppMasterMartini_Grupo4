@echo off
chcp 65001 > nul
title Servidor Backend Spring Boot - Master Martini

echo ======================================================================
echo    INICIANDO SERVIDOR MICROSERVICIO SPRING BOOT - MASTER MARTINI
echo ======================================================================
echo.

cd /d "%~dp0"

echo [1/3] Verificando entorno Java...
if defined JAVA_HOME (
    echo Java encontrado en JAVA_HOME: %JAVA_HOME%
) else (
    echo AVISO: JAVA_HOME no esta definido. Se usara 'java' del PATH del sistema.
)

echo.
echo [2/3] Verificando Maven...
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo Maven encontrado en el sistema. Ejecutando spring-boot:run...
    echo.
    echo ======================================================================
    echo Servidor disponible en: http://localhost:8080/api/clientes
    echo Consola H2 disponible en: http://localhost:8080/h2-console
    echo ======================================================================
    echo.
    mvn spring-boot:run
) else (
    echo Maven no se encuentra en el PATH global.
    echo Intentando ejecutar usando Maven Wrapper si esta presente...
    if exist "mvnw.cmd" (
        call mvnw.cmd spring-boot:run
    ) else (
        echo.
        echo ERROR: No se encontro 'mvn' ni 'mvnw.cmd'.
        echo Por favor instala Maven o agrega Maven/JDK al PATH del sistema.
        echo Tambien puedes ejecutar en la terminal:
        echo    mvn spring-boot:run
    )
)

pause
