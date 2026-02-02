@echo off
title 西安航天自动化研发中心 前端打包工具
cd /d %~dp0
goto processPlace

:processPlace
    set /p buildenv=请输入打包环境，可选值为dev、test、pre、pro:
    echo 请稍候...
    if "%buildenv%" =="dev" (
        cmd /c npm run build:%buildenv%
    ) else if "%buildenv%" =="test" (
        cmd /c npm run build:%buildenv%
    ) else if "%buildenv%" =="pre" (
        cmd /c npm run build:%buildenv%
    ) else if "%buildenv%" =="pro" (
        cmd /c npm run build:%buildenv%
    ) else (
        echo 输入参数有误，请重新输入。
        goto processPlace
    )
pause
