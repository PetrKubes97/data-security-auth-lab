To run the program, you first need to have RMI server. 
For me, installing the RMI extension for 
Intellij was enough and that made it to work. It's quite odd.


You might want to take a look and optionally run ```scripts.CreateUsers``` to fill
the database with users. The database file can be browsed using any SQLite browser (I use
the one built into intellij). If you want to test code without going through the pain
of resetting server and client, take a look at ```scripts.Tests```.

Then run ```server_side.ApplicationServer``` and
lastly ```client_side.Client```.