To run the program, you first need to have RMI server. 
For me, installing the RMI extension for 
Intellij was enough and that made it to work. It's quite odd.


You might want to take a look and optionally run ```seed.CreateUsers``` to fill
the "database" with users. You can see the raw data in ```users.txt```.

Then run ```server_side.ApplicationServer``` and
lastly ```client_side.Client```.