def localBilling = 'Local_Billing'
def billingAutomation = 'Billing Automation for Daily Build_Local'

stage "build $localBilling"
node {
    build localBilling
}

stage 'confirm'
node {
    input "$billingAutomation"
}

stage "build $billingAutomation"
node {
    build billingAutomation
}
