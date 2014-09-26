# nginxwebeditor
Web __editor__ for __nginx__ files using a __git__ repository

### Prepare your environment
Go to your nginx conf folder
```sh
cd /etc/nginx
```

Init this folder as a __git__ repo
```sh
git init
git add * 
git commit .
```

### How to start the app
```sh
mvn exec:java -Dexec.mainClass=main.Main -Dexec.args="test test"
```

### Used libraries
from __https://github.com/brippe/GWT-FileManager.git__  
and __Ace GWT__  
and __jgit__
