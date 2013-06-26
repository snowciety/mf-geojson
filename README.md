mf-geojson
==========

This project contains the following:

* A library/implementation for GeoJSON tiles in [mapsforge 0.4.0](http://code.google.com/p/mapsforge/)
* An Android application showing how the GeoJSON library can be used


# The GeoJSON library
This implementation is based on the ["rewrite"](http://code.google.com/p/mapsforge/source/browse/?name=rewrite) branch of mapsforge. 
It adds the ability to use GeoJSON tiles together with mapsforge. The implementation is a bit rough around the edges and is more of a proof of concept.
Also, includes the following;
* Persistent caching of tiles on disk
* Ability to create custom "Painters" for GeoJSON layers

# The Android Demo Application
The demo application is using OpenStreetMap.us' vector tiles as described here: http://openstreetmap.us/~migurski/vector-datasource/.
Requires Android SDK API 11 or higher to compile.
![screenshot] (https://dl.dropboxusercontent.com/u/9220166/mf-geojson.png)

# Usage
## Requirements
* Android Development Tools (ADT)
* Android SDK (only tested with API 17)
* mapsforge-map 0.4.0 ("rewrite" branch) [build instructions here](http://code.google.com/p/mapsforge/wiki/GettingStartedDevelopers#Build_the_project)
* [Apache Commons IO 2.4](http://commons.apache.org/proper/commons-io/download_io.cgi)

## Importing project into Eclipse
* Clone this repo
* Import into your workspace using File->New->Other->"Android project from existing code"
* Select this repo's top folder as the root folder

## Importing the test project
As above, but instead of the repo's root folder, choose the "tests" folder.

# To-do
* Time based disk caching? Invalidate tiles if they have been stored for too long?
* Integrate nicely with mapsforge?
* ...

