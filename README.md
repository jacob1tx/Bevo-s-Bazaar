# Bevo's Bazaar
An auction service written using JavaFX which supports multiple clients.

## Programmer's Perspective
The program is broken into two projects: a server project and a client project. The server
project is run from the main server class, which extends observable. The class has methods that
take input commands from the clients and returns the requested information. Such commands
include logging in, placing a bid, and getting a list of any user’s purchases. To assist in this are a
number of other classes, all contained within the server project. The Client Handler class directly
interacts with the clients and contains the socket for communication. When messages are passed
to the server as strings, the client handler parses them as requests to be passed to the server. Once
the commands have been completed by the server, all observers are notified and the client
handler returns the results to the clients as strings. The Login class maintains a list of all the
usernames and passwords, maintained in a separate text file, and is called on by the server to
validate a user’s login information, returning true or false for successful or unsuccessful
attempts. Finally, the Item class is a specific class that holds information about all of the items
that are for sale. These include an item’s description, its starting and BIN prices, its time limit,
and whether it has been sold. When the server boots up, it reads a list of all items for sale in a
text file and parses that file into a list of Item objects that is maintained by the server. The server
has no UI except the console which is entirely used for debugging. The client project contains all
the files necessary to run the client(s), stemming from the Client class containing the socket and
its static main function. Various .fxml files contain code for the UIs used, and each has its own
controller. The main two UIs/controllers are login and storefront, both of which are started by the
SceneGenerator class which extends Application and runs the JavaFX files. Folders within the
client project contain the .mp3 and .jpg files used by the client as well. When a user inputs a
command into the login or storefront, the Client class processes it and sends it to the server via
the socket. When a message is received from the server, the Client class interprets the string in
the reader thread and calls the appropriate function corresponding to the server’s request. The
Client class uses its GUIs to display error messages, such as improper text formatting, and only
uses the terminal for status updates.

### To run
To properly start the server, first ensure that all login information is included in the
login.txt file. Then verify that all items for sale have been included in the items.txt file, ensuring
also that all relevant information pertaining to each item has been included, that fields are
separated by commas, and that individual items are separated by newlines. Once this is complete,
Server.main can be run with no arguments.

## User's Perspective
When a user wants to log in, the only thing they have to do is run Client.main. This
establishes a connection to the server (the server must be running first) and runs the login GUI.
From there, a user should enter an appropriate username and password or type “guest” into
username to login as a guest. A guest has access to all of the same features as a known user but
their purchase history is not saved in the case that they log out. If an incorrect username or
password is entered, the user is notified via the GUI. Once a user has successfully logged in, the
login GUI closes and the storefront GUI opens. The storefront is clean and symmetrical and
plays relaxing jazz while the user shops. At the top left, the user’s name is displayed. To the right
is a menu with options to pause/play the music, list all of the user’s purchases, logout, or close
the program cleanly. Below is the actual shop. Each item up for sale is contained in a block
consisting of a title, a short description, a timer showing when that item’s bidding closes, the
current highest bid for the item (or its starting bid value if it has no bids), the highest bidder, a
text box within which to place a bid, and options to buy the item, with its BIN price, and list a
history of all bids for that item. If a user wishes to make a bid on an item, they must simply enter
a bid into the text box and press Bid. If an item has not been bid on, they may enter any price
greater than or equal to the starting bid. Otherwise, they must enter a price higher than the listed
bid. Warnings for invalid or low bids are listed at the top of the storefront page. Once a valid bid
has been placed, all users are notified of this new bid. If a user wishes to avoid the hassle of
bidding on an item, they may click the Buy button to stop all bidding and purchase the item for
the price listed under “Buy it Now!” Be quick, however, as all bids have a time limit. Once the
time runs out, the item goes to its highest bidder or no one, if no bids were placed. Either way,
the item can no longer be bid on or bought. At any point, a user may view a history of all bids on
any item by pressing the History button within an item’s block. To get a list view of all purchases
that have been made by the user, click Purchases within the Menu dropdown. At any point, a
user may choose to logout by clicking Logout, after which the storefront will close and the login
screen will appear. The user may log back in under the same account and any purchases will be
preserved. This does not apply to guests.

### Constraint
This software is built on JavaFX, which requires a proper IDE configuration. This may even require setting up a new project and copying the client files over.
