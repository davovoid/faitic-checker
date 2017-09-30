Set object = WScript.CreateObject("WScript.Shell")
Set shortcut = object.CreateShortcut("%2")

shortcut.TargetPath = "javaw"
shortcut.Arguments = "-jar ""%0"""
shortcut.Description = ""
shortcut.IconLocation = "%1"

shortcut.Save
Set shortcut = Nothing