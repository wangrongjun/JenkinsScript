package learn_input

def mainVersion = "$main_version"

stage 'build pipeline-a'
node {
    echo "mainVersion: $mainVersion"
    def job = build job: "pipeline-a", parameters: [
            [$class: "StringParameterValue", name: "a_version", defaultValue: '2.0', value: mainVersion, description: 'a version'],
    ]
}

stage 'approval'
echo 'Please call rong to approval'
node {
    def currentUser
    wrap([$class: 'BuildUser']) {
        currentUser = env.BUILD_USER_ID
    }
    if ("rong" == currentUser) {
        input "Build pipeline-b? currentUser=$currentUser"
    } else {
        input "Build pipeline-b? currentUser=$currentUser"
    }
}

stage 'build pipeline-b'
node {
    def job = build job: "pipeline-b", parameters: [
            [$class: "StringParameterValue", name: "b_version", defaultValue: '3.0', value: mainVersion, description: 'b version'],
    ]
}
