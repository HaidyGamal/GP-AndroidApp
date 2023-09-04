<h1 align="center" id="title">Tawsila</h1>

<p id="description">Native Android Application that helps Great Cairo citizens to explore it using the public transportation means like Underground Bus &amp; Microbus. The App has two modes (Normal Mode &amp; Blind Mode)</p>

<h2>🚀 Demo</h2>

[https://www.youtube.com/watch?v=ogHvqx3w7lY](https://www.youtube.com/watch?v=ogHvqx3w7lY)

  
  
<h2>🧐 Features</h2>

Here're some of the project's best features:

*   1- Provides different routes passing by different intermediate points
*   2- Provides necessary information like costdistance & time for each route
*   3- Allows the user to specfiy his preferred sorting criteria
*   4- Allows the user to share his location with any relative/friend he gives a permission
*   5- Allows the user to suggest adding new route between to points by taking some information from him
*   6- Allows the user to give a rate for any sub Route he passed by during his trip
*   7- Allows the user to track himself on google Map
*   8- Allows the blinds to use the application by using voice Commands created for them

  
  
<h2>💻 Built with</h2>

Technologies used in the project:
CodeBase is mainly in **Java** with some **Kotlin** & UI is build in **XML**
*   1- **Firebase**: for user's authentication & sharing realTime location between relevants
*   2- **Room**: for storing local data like user information
*   3- **Retrofit**: for dealing with RESTful APIs that returns shortestPaths according to various criteria with some information
*   4- **Text-To-Speech & Speech-To-Text APIs**: to make a communication with blinds using Voice Commands
*   5- **Google Map**: for displaying the user's location on it
*   6- **Places Autocomplete API**: for feeding our autocomplete text Views to be dynamic
*   7- **Compute Routes API**: for retrieving a realtime expected time for the trip by summing the total sub times in the route taken from API
*   8- **GeoContext API**: for retrieving details for any place that is used to provide better user experience
*   9- **Shared Preferences**: for storing simple preferences like current app Mode
*   10- **Neo4j**: graph database for building the projects database in form of Nodes (Stops) & Relationships (sub route taken by a mean)
*   11- **Background Service**: for continously providing the user with his current place
