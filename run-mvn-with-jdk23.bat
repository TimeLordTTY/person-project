@echo off  
set "JAVA_HOME=D:\Soft\Java\jdk-23"  
set "PATH=D:\Soft\Java\jdk1.8.0_201\bin;D:\Soft\Java\jdk-23\bin;D:\Soft\Java\jdk1.8.0_201\bin;"D:\Soft\Java\jdk1.8.0_201\jre\bin;";d:\Soft\cursor\resources\app\bin;D:\Soft\Thunder\Program\;C:\Program Files (x86)\Microsoft\Edge\Application;C:\Program Files\Common Files\Oracle\Java\javapath;D:\Soft\Python\Scripts\;D:\Soft\Python\;C:\Python312\Scripts\;C:\Python312\;D:\Soft\VanDyke Software\Clients\;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\windows\system32;C:\windows;C:\windows\System32\Wbem;C:\windows\System32\WindowsPowerShell\v1.0\;C:\windows\System32\OpenSSH\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\WINDOWS\System32\OpenSSH\;C:\Program Files\Bandizip\;D:\Soft\TortoiseSVN\bin;C:\Program Files\dotnet\;C:\ProgramData\chocolatey\bin;D:\Soft\Nodejs\;D:\Soft\Nodejs\node_global;C:\Program Files\HP\HP One Agent;C:\Program Files\Pandoc\;E:\Apache\apache-maven-3.6.3\bin;D:\Soft\Git\cmd;C:\Program Files (x86)\OpenOffice 4\program;C:\Program Files\NVIDIA Corporation\NVIDIA app\NvDLISR;d:\Soft\cursor\resources\app\bin;C:\Users\TimeL\AppData\Local\Microsoft\WindowsApps;;D:\Soft\IntelliJ IDEA 2023.3.2\bin;;D:\Soft\Microsoft VS Code\bin;C:\Users\TimeL\AppData\Roaming\npm;C:\Users\TimeL\.dotnet\tools;C:\Users\TimeL\AppData\Local\Programs\Ollama;C:\Users\TimeL\.lmstudio\bin;C:\Users\TimeL\go\bin;D:\Soft\cursor\resources\app\bin"  
"E:\Apache\apache-maven-3.9.9\bin\mvn" -f "person-web/person-web-service/pom.xml" clean compile  
