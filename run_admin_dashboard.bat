@echo off
title Survival Master - Content Studio & Simulator
echo ============================================================
echo   Survival Master Content Studio & Live Mobile Simulator
echo ============================================================
echo Starting local admin server at http://localhost:8080 ...
cd /d "%~dp0admin_dashboard"
python server.py 8080
pause
