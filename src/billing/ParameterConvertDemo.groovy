package billing
// test-main 的构建化参数 MainVersion 设置默认值为1.0
// 以下是 test-main [pipeline] job 的脚本
def mainVersion = "$MainVersion"

stage 'build a'
node {
    build job: "test-a", parameters: [
            [$class      : "StringParameterValue", name: "version",
             defaultValue: '4.0', value: mainVersion, description: 'a version'],
    ]
    // 说明：即使有 defaultValue 也不会改变 test-a 已有的构件化参数的默认值
}

stage 'confirm'
input 'build b ?'

stage 'build b'
node {
    build job: "test-b", parameters: [
            [$class: "StringParameterValue", name: "version",
             value : mainVersion, description: 'b version'],
    ]
}

// ------------------------------------------------------

// test-a 的构建化参数 version 设置默认值为2.0
// 以下是 test-a [project] job 的bat脚本
echo 'building a, version: %version%'

// ------------------------------------------------------

// test-b 的构建化参数 version 设置默认值为3.0
// 以下是 test-b [project] job 的bat脚本
echo 'building b, version: %version%'

// ------------------------------------------------------

/*
输出结果（构建test-main，MainVersion=1.0）
test-a:
'building a, version: 1.0'

test-b:
'building b, version: 1.0'
 */

/*
输出结果（构建test-main后再次构建test-a, test-b）
test-a:
'building a, version: 2.0'

test-b:
'building b, version: 3.0'
 */

/*
结论
1. test-a, test-b 构建时可以正常读取到默认值（2.0，3.0）
2. test-main 构建时可以传入MainVersion的值（1.0）到test-a, test-b（即覆盖test-a, test-b 原有的默认值）
3. 再次构建test-a, test-b 时，参数version都还在，且默认值不受test-main的影响（还是2.0, 3.0）
 */
