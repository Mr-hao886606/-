@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows
@REM Downloads and runs Maven automatically
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

set MAVEN_VERSION=3.9.6
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_DIR=%USERPROFILE%\.m2\wrapper\dists
set MAVEN_ZIP=%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%-bin.zip

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Maven %MAVEN_VERSION% ...
    if not exist "%MAVEN_DIR%" mkdir "%MAVEN_DIR%"
    if not exist "%MAVEN_ZIP%" (
        powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%MAVEN_ZIP%' -UseBasicParsing"
    )
    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_DIR%' -Force"
    if exist "%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%" (
        move "%MAVEN_DIR%\apache-maven-%MAVEN_VERSION%" "%MAVEN_HOME%" 2>nul
    )
)

"%MAVEN_HOME%\bin\mvn.cmd" %*
