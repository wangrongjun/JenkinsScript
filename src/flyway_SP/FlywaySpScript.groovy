node {
    def parentJob = 'flyway_SP_parent'
    def commonJob = 'flyway_SP_common'
    def mainJob = 'flyway_SP_main'

    stage "Install $parentJob"
    build parentJob
    // 以下是parentJob构建完成后触发构建的下游Job
    // Triggering a new build of Local_ServicePartner_Common_UT
    // Triggering a new build of Local_SP_Common_JDK8
    // Triggering a new build of Local_SP_JDK8_Main
    // Triggering a new build of Local_ServicePartner_UT

    // 如果希望指定只有某些人有权确认继续构建，可以这样写：
    // input message: 'confirm', submitter = 'wang,rong'// 只允许用户wang和rong进行确认操作
    input "Install $commonJob?"
    stage "Install $commonJob"
    build commonJob
    // 以下是commonJob构建完成后触发构建的下游Job
    // Triggering a new build of Local_SP_JDK8_Main
    // Triggering a new build of Local_ServicePartner_UT

    input "Install $mainJob?"
    stage "Compile $mainJob"
    build mainJob
}
