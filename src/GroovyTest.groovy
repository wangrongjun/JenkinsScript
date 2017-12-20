stage 'build'
node {
    echo 'build'
}

input message: 'Approval test?', submitter: 'rong,jun', submitterParameter: 'inputId', parameters: [
        [$class: "StringParameterDefinition", name: "s"]
]
echo "inputId: $s"

stage 'test'
node {
    echo 'test'
}
