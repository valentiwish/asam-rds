@echo off
title consul
consul agent -dev -ui -client 0.0.0.0
pause