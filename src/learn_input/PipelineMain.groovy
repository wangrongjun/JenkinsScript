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
