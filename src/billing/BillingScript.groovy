package billing

node {
    def localBilling = 'Local_Billing'
    def billingAutomation = 'Billing Automation for Daily Build_Local'

    stage "build $localBilling"
    build localBilling

    input "$billingAutomation"
    stage 'confirm'

    stage "build $billingAutomation"
    build billingAutomation
}
