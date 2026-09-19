@echo off
setlocal
pushd "%~dp0.."
java -cp "dist\MINLPDD.jar;dist\lib\*" data_handler.figures.GenerateFigure1 "results\resultsOnline.csv" "results\generated_figures"
if errorlevel 1 goto done
java -cp "dist\MINLPDD.jar;dist\lib\*" data_handler.figures.GeneratePaperFigures "results\resultsOffline.csv" "results\generated_figures"
:done
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%