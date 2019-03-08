# Music Roulette 
The mobile app will serve as a music “discovery” app where it will play songs based on user-specified generes.

## Developers
- Jimwel Aguinaldo
- Timothy Bui
- Vincent Nguyen 
- Vincent Ta

## API Usage
We will be taking advantage of the Spotify API to access songs based on the user preferred genres.

The list of API methods will be used:
- GET recommendations based on seed. This will return a list of tracks based on any combination of artists, genres, and tracks. One track at random will be chosen from the list of recommendations to be displayed to the user in the “Discover” view.
```
https://api.spotify.com/v1/recommendations
```
- GET available genre seeds. This will return a list of available genres on spotify to pass into the method above. Users will be able to select their prefered genre from this list to use in grabbing recommendations.
```
https://api.spotify.com/v1/recommendations/available-genre-seeds
```

## UI Description
There will be a discovery page where users will be able to view the songs generated from the genres that they selected. This page will contain a cover of the song as well as a button that will allow users to open the song with spotify. The second activity that is available to users will be a settings option. This activity will allow users to select specific genres based on their personal preferences. There will be a drop down menu that lists all of the genres available for users to select. Once the user selects a genre from the list, there will be a notification that appears indicating that the selected genre has been added to their settings. 

At the top of the pages, there will be a title in the navigation bar that indicates which page they are currently on. At the bottom of both the pages, there will be an activity bar that will allow users to toggle between the different pages.
