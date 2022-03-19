@rem put this project path into PATH_FROM
setlocal
set PATH_FROM=C:\Users\Yifeng\Desktop\MyMindustry\MinerTools
@rem put your mindustry local path into PATH_TO
setlocal
set PATH_TO=C:\Users\Yifeng\AppData\Roaming\Mindustry

if exist %PATH_TO%\mods\MinerToolsDesktop.jar del %PATH_TO%\mods\MinerToolsDesktop.jar
move %PATH_FROM%\build\libs\MinerToolsDesktop.jar %PATH_TO%\mods\

java -jar C:\Users\Yifeng\Desktop\Mindustry.jar