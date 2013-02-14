@REM
@REM Copyright (c) 2013 mgm technology partners GmbH
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM     http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM

@echo off
@setlocal

set EXIT_CODE=0

cd %~dp0
call setenv.cmd

if not "%JAVA_HOME%" == "" goto gotJavaHome

echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
goto error

:gotJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto run

echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
goto error

:run
set JAVA_CMD="%JAVA_HOME%\bin\java"
set JAVA_OPTS=%JAVA_OPTS% -Djava.library.path=lib -jar .\lib\perfload-perfmon-${project.version}.jar %*

call %JAVA_CMD% %JAVA_OPTS%

if ERRORLEVEL -1 goto error
goto end

:error
set EXIT_CODE=-1

:end
@endlocal

exit /B %EXIT_CODE%