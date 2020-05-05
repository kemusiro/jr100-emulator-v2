Set objWShell = CreateObject("Wscript.Shell")
Set colEnv = objWShell.Environment("Process")
colEnv.Item("PATH") = objWShell.ExpandEnvironmentStrings("%PATH%") & ";" & ".\external\nativelib"
objWShell.run "cmd /c java -jar jr100v2.jar", vbHide
