[TOC]

## Jenkins中各job并行后再触发job

（1）A、B两个job并行执行用例
（2）执行完后合并A、B的结果，生成报告

[Jenkins中各job并行后再触发job](http://blog.csdn.net/ouyanggengcheng/article/details/76150932)


## 构建前定义参数

方式1：在job的配置页面选择“参数化构建过程”，并定义参数

```groovy
// 方式2：在脚本中定义参数
properties([parameters([
        string(name: 'version', defaultValue: '1.5', description: 'version number'),
])])

// 读取参数
stage 'build'
node {
    println "version: ${params.version}"
}
```


## 同一个jenkins下的job之间的 传参触发

[Jenkins job 之间实现带参数触发](http://blog.csdn.net/wanglin_lin/article/details/73991559)

1. 安装 jenkins 参数化插件：Parameterized Trigger Plugin

2. 构建自由风格的软件项目JobA。

3. 选择参数化构建过程的String Parameter，添加参数 a_version

4. 在 "构建" 添加运行Windows batch command的窗口，填写：
   echo 'JobA building, version=%a_version%'

5. 在 “构建后操作” 添加 "Trigger parameterized build on other projects"
   操作，然后填写项目名JobB。下面的Add Parameter选择
   Predefine parameters。最后在文本区域填写 b_version=${a_version}

6. 构建自由风格的软件项目JobB。

7. 选择参数化构建过程的String Parameter，添加参数 b_version

8. 在 "构建" 添加运行Windows batch command的窗口，填写：
   echo 'JobB building, version=%b_version%'

9. 构建JobA，就会发现JobA构建完成后会构建JobB，并且参数传递成功。


## Jenkins获取登录用户名

[jenkins插件-Build User Vars Plugin简单说明](http://blog.csdn.net/liaojianqiu0115/article/details/78410265)

	Build User Vars Plugin是jenkins用户相关变量插件，使得在构建过程中可以使用用户相关环境变量

	BUILD_USER             全名
	BUILD_USER_FIRST_NAME  名字
	BUILD_USER_LAST_NAME   姓
	BUILD_USER_ID          jinkins用户ID
	BUILD_USER_EMAIL       用户邮箱

	缺陷：当job是定时执行的时候，获取不到jenkins登录用户名。
	解决方案：可以通过分析job的历史任务，得到没个job的首次执行登录用户名，和末次执行的登录用户名，进行job的归属者。

【注意】在配置项目的时候一定要勾选Set jenkins user build variables。这一项只在自由风格的job才有。

使用方式：

	1、shell  $变量名
	2、cmd  %变量名%
	3、项目填写框 ${变量名}

如果想在Pipeline中获取用户名，需要这样写：

```groovy
node {
    wrap([$class: 'BuildUser']) {
        echo "$env.BUILD_USER_ID"
    }
}
```

[Get user name from Jenkins Workflow (Pipeline) Plugin](https://stackoverflow.com/questions/35902664/get-user-name-from-jenkins-workflow-pipeline-plugin#35902865)


## Jenkins权限管理，实现不同用户组显示对应视图views中不同的jobs

1. 首先安装插件：Role-based Authorization Strategy
2. 系统管理 ->全局安全配置 -> 激活Role-Based Strategy
3. 系统管理 -> Manage and Assign Roles

说明：Project roles的Pattern是匹配项目名的表达式。
      比如匹配JobA,JobB，Pattern="Job.*"。注意不是"Job*"


## Jenkins使用Groovy脚本控制项目的流程（确认，传参）

```groovy
// PipelineMain Job
def mainVersion = "$main_version"

stage 'build pipeline-a'
node {
    echo "mainVersion: $mainVersion"
    def job = build job: "pipeline-a", parameters: [
            [$class      : "StringParameterValue", name: "a_version",
             defaultValue: '2.0', value: mainVersion, description: 'a version'],
    ]
}

stage 'approval'
echo 'Please call rong to approval'
// 只有rong,jun这两个用户有权进行确认。
input message: 'Build pipeline-b?', submitter = 'rong,jun'

stage 'build pipeline-b'
node {
    def job = build job: "pipeline-b", parameters: [
            [$class      : "StringParameterValue", name: "b_version",
             defaultValue: '3.0', value: mainVersion, description: 'b version'],
    ]
}

// ---------------------------------------------------------------

// PipelineA Job
stage 'build'
node {
    echo "a_version=$a_version"
}

// ---------------------------------------------------------------

// PipelineB Job
stage 'build'
node {
    echo "b_version=$b_version"
}
```


## 设置maven repositories

选项1：Default

	~\.m2\repository（写实了这个。不会读取maven的settings的localRepository）

选项2：Local to the executor

	.jenkins\maven-repositories\0

选项3：Local to the workspace

	.jenkins\workspace\maven-a\.repository

自定义maven repositories

	job名-->configure-->Build-->Goals and options:
	clean package -Dmaven.repo.local=D:\dev\maven3.1.1\m2repository
	在这里使用-Dmaven.repo.local,强制指定本地仓库的路径.

	现在发现上面的方法也不保险，因为Jenkins会指定两次。如下是Jenkins执行的mvn命令：
	Executing Maven:  
	-B -f C:\Users\robin.wang\.jenkins\workspace\maven-a\pom.xml 
	-Dmaven.repo.local=C:\Users\robin.wang\.jenkins\workspace\maven-a\.repository 
	clean test -Dmaven.repo.local=E:\IDE\Maven\repository
