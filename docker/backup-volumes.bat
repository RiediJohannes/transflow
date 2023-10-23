@echo off
setlocal enabledelayedexpansion

REM Set source and destination directories
set "source=.\data"
set "backupDir=.\backup"

REM Create destination directory if it doesn't exist
if not exist "%backupDir%" mkdir "%backupDir%"

REM Create a timestamp for the backup
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set "timestamp=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%_%datetime:~8,2%-%datetime:~10,2%-%datetime:~12,2%"

REM Use 7-Zip to create a zip archive from the source directory
7z a -r "%backupDir%\%timestamp%.zip" "%source%\*"

REM Clean up old backups (keep only the newest three)
for /f "tokens=*" %%F in ('dir /B /O-N "%backupDir%\*.zip" ^| more +3') do del "%backupDir%\%%F"

echo Backup completed!