# Modified Solitaire 
The goal of this project is to create a similar virtual modified version of the card game Solitaire. Game rules are similar to ones of [Kings In The Corner](https://www.jaxgames.com/kings-in-the-corner-game-instructions-instrucciones/) from JaxGame.

This project is meant to serve only educational purpose, not to be redistributed or commercialized in any way.

## Game play
- Player logs in or register an account before starting to play
- In Log-In windows, the field `Host IP` is meant for the IP address of the Server. If Server and Client run on the same machine, the address here should be `localhost`. If the players are not on the same network with each other (and therefore not with the host), they can try out VPN option like Hamachi. More about Hamachi can be found [here](https://www.vpn.net/) und [here](https://support.logmeininc.com/central/help/resolving-hamachi-request-timed-out-for-windows-firewall)
- Players can chat to other players in every room that they join
- After logging in, when entering or creating a new game room, player has 2 options: Either playing with machine (bots) or with other players in the same network.
- Players can choose number of bots that they want to play with. The bots have 2 mode: either Hard or Easy.
- Game ends when someone reachs the total points of 100.

## Instruction on how to execute the software
Clone the project to local machine:
- In src/main/java/kings/server/administration/: Run Server.main() 
- In src/main/java/kings/gui/: Run Launcher.main() aus

### DEUTSCH: Anleitung zur Ausführung der Software

Klonen das Projekt auf lokalen Rechner.
- In src/main/java/kings/server/administration/: Führen Server.main() aus.
- In src/main/java/kings/gui/: Führen Launcher.main() aus.

Im Anmeldungsfenster ist das Feld `Host IP` für die IP-Adresse des Servers gedacht. Wenn Server und Client auf demselben Rechner laufen, sollte hier "localhost" eingegeben werden. Wenn die Spieler nicht auf demselbem LAN sind, können die mithilfe von Hamachi miteinander verbinden. Mehr über das Setting-Up von Hamachi erfährt man [hier](https://www.vpn.net/) und [hier](https://support.logmeininc.com/central/help/resolving-hamachi-request-timed-out-for-windows-firewall) 