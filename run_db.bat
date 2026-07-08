@echo off
chcp 65001 > nul
echo =======================================================================
echo    DANG THIET LAP CO SO DU LIEU AURELIA BOOKS...
echo =======================================================================
echo.
sqlcmd -S localhost -U sa -P 123456 -i "src\main\java\com.mycompany.aureliabooks.context\setup.sql" -f 65001 -C
echo.
echo =======================================================================
echo    KHOI TAO CO SO DU LIEU HOAN TAT!
echo =======================================================================
echo.
pause
