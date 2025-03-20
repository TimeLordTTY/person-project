@echo off
echo 创建必要的目录结构...
mkdir target\classes 2>nul

echo 编译Java文件...
javac -d target\classes -cp "libs/*" src\main\java\com\person\web\*.java src\main\java\com\person\web\config\*.java src\main\java\com\person\web\controller\*.java src\main\java\com\person\web\dto\*.java src\main\java\com\person\web\model\*.java src\main\java\com\person\web\repository\*.java src\main\java\com\person\web\service\*.java src\main\java\com\person\web\service\impl\*.java

if %ERRORLEVEL% NEQ 0 (
  echo 编译失败!
  exit /b 1
)

echo 复制资源文件...
xcopy /Y src\main\resources\*.* target\classes\ 2>nul

echo 项目构建成功!
echo 要运行应用程序: java -cp "target\classes;libs/*" com.person.web.PersonWebServiceApplication 