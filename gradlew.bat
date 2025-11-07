@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass any JVM options to Gradle and Java processes.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to get the absolute path.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem For Cygwin, switch paths to Windows format before running java
if not "%CLASSPATH%" == "" call :cygpath "%CLASSPATH%"
if not "%JAVA_HOME%" == "" call :cygpath "%JAVA_HOME%"

@rem Detect Cygwin and convert path if necessary
if exist "%SystemRoot%\System32\cygpath.exe" (
    call :cygpath "%APP_HOME%"
)

@rem Locate the wrapper JAR.
if not exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" (
    echo.
    echo ERROR: The wrapper has been unable to locate a valid installation of the wrapper JAR in this build's cache.
    echo.
    goto end
)
set WRAPPER_JAR="%APP_HOME%\gradle\wrapper\gradle-wrapper.jar"

@rem Discover the purpose of the wrapper and trigger the appropriate action.
if exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.properties" (
    for /f "tokens=1,2 delims==" %%a in ('findstr /b "distributionUrl" "%APP_HOME%\gradle\wrapper\gradle-wrapper.properties"') do (
        set DISTRIBUTION_URL=%%b
    )
    if not defined DISTRIBUTION_URL (
        echo.
        echo ERROR: Your build is currently configured to use Gradle, which is not supported by the wrapper.
        echo Please update the wrapper to one that supports this Gradle version.
        echo.
        goto end
    )
) else (
    echo.
    echo ERROR: Your build is currently configured to use Gradle, which is not supported by the wrapper.
    echo Please update the wrapper to one that supports this Gradle version.
    echo.
    goto end
)

@rem Determine the Java command to use to start the JVM.
if defined JAVA_HOME (
    set JAVACMD="%JAVA_HOME%\bin\java.exe"
) else (
    set JAVACMD=java.exe
)

@rem Execute Gradle
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath %WRAPPER_JAR% org.gradle.wrapper.GradleWrapperMain %*
goto end

:cygpath
    set _CYGPATH=%1
    if not defined _CYGPATH goto :eof
    if "%_CYGPATH:~-1%"==";" set _CYGPATH=%_CYGPATH:~0,-1%
    if exist "%SystemRoot%\System32\cygpath.exe" (
        for /f "usebackq" %%p in (`cygpath -w "%_CYGPATH%"`) do set %1=%%p
    )
    goto :eof

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
@rem Set variable ERRORLEVEL to ensure exit code is non-zero
verify.
goto mainEnd

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
