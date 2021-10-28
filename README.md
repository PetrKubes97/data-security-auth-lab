To run the program, you first need to have RMI server. 
For me, installing the RMI extension for 
Intellij was enough and that made it to work. It's quite odd.

Install ``bouncycastle.bcprov.jdk15on2`` library. In intellij, you can go to project
structure and add it through maven. Or you can download the jar. 

Then run ```server_side.ApplicationServer``` and
lastly ```client_side.Client```.