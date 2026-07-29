@echo off
setlocal
pushd "%~dp0.."
java -cp "dist\MINLPDD.jar;dist\lib\*" data_handler.figures.GenerateFigure1 "results\resultsOnline.csv" "results\generated_figures"
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%