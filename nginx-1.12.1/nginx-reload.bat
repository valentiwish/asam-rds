@echo off

cd %cd%
taskkill /f /t /im nginx.exe
start nginx.exe
pause 