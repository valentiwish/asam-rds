@echo off
chcp 65001 > nul
title service_system - 权限服务

:: ========================================
::   强制指定Java 8环境
:: ========================================
set JAVA_HOME=D:\Program Files\Java\jdk1.8.0_151
set PATH=%JAVA_HOME%\bin;%PATH%
set JAR_FILE=service_system-0.0.1-SNAPSHOT.jar


:: ========================================
::   环境检查与启动流程
:: ========================================
echo ========================================
echo    Service System 启动脚本
echo ========================================
echo 正在启动权限服务...
echo.

echo 检查Java版本...
java -version
if %errorlevel% neq 0 (
    echo ❌ 错误: 指定的Java路径可能不正确或Java环境未正确安装。
    echo 请检查JAVA_HOME路径: %JAVA_HOME%
    pause
    exit /b 1
)
echo.

:: ========================================
::   设置适用于Java 8的JVM参数
:: ========================================
set JAVA_OPTS=-Xms256m -Xmx512m -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heap_dump.hprof -XX:+PrintGCDetails -Xloggc:./gc.log

:: ========================================
::   应用程序启动流程
:: ========================================
if not exist "%JAR_FILE%" (
    echo ❌ 错误: 未找到 "%JAR_FILE%"
    echo 请确保JAR文件与脚本在同一目录下
    pause
    exit /b 1
)

echo ✅ JAR文件检查通过
echo 📋 使用的JVM参数:
echo %JAVA_OPTS%
echo.

echo 🚀 正在启动应用程序...
java %JAVA_OPTS% -jar %JAR_FILE%

if %errorlevel% neq 0 (
    echo ❌ 应用程序启动失败，退出代码: %errorlevel%
) else (
    echo ✅ 应用程序已正常退出
)

pause