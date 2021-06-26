# cordapp-example 

This repo is the implementation of the tutorial cordapp. 

You can find that tutorial here: 

https://docs.corda.net/docs/corda-os/4.7/quickstart-deploy.html#running-the-example-cordapp

run vaultQuery contractStateType: net.corda.samples.example.states.IOUState
flow start UpdateFlow invoiceID: 1edbf2a8-1339-4e24-b7ed-5ed9f2b17cec,newV: 1,otherParty: "O=PartyB,L=New York,C=US"

flow start ExampleFlow iouValue: 5,otherParty: "O=PartyB,L=New York,C=US"


 flow start ExampleFlow$Initiator iouValue: 50, otherParty: "O=PartyB,L=New York,C=US"
 
 run vaultQuery contractStateType: net.corda.samples.example.states.IOUState

======================================================================================================
flow start ExampleFlow$Initiator iouValue: 50, otherParty: "O=PartyB,L=New York,C=US"

run vaultQuery contractStateType: net.corda.samples.example.states.IOUState

flow start UpdateExampleFlow$Initiator id: 73875c5a-1500-4b3e-8af6-1105167d54f2

cp .\workflows\build\libs\workflows-0.1.jar .\build\nodes\PartyB\cordapps\