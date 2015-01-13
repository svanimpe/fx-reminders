/*
 * The value for these first two variables will be injected by Java.
 * hasLocation will be the visibleProperty of the delete button.
 * existingLocation will be the Location object to use.
 */
var hasLocation;
var existingLocation;

var map;
var marker;

function loadMap() {
    if (existingLocation) {
        loadExistingLocation();
    } else if (navigator.geolocation) {
        // Note: geolocation doesn't seem to work in WebView.
        navigator.geolocation.getCurrentPosition(loadCurrentLocation);
    } else {
        loadDefaultLocation();
    }
}

/*
 * Center the map on the existing location and place a marker there.
 */
function loadExistingLocation() {
    var position = new google.maps.LatLng(existingLocation.getLatitude(), existingLocation.getLongitude());
    
    var mapOptions = {
        center: position,
        zoom: 14,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map"), mapOptions);
    
    setMarker(position);
    
    google.maps.event.addListener(map, "click", function(event) {
        setMarker(event.latLng);
    });
}

/*
 * Center the map on the current location, as determined by geolocation.
 */
function loadCurrentLocation(position) {
    var mapOptions = {
        center: new google.maps.LatLng(position.coords.latitude, position.coords.longitude),
        zoom: 14,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map"), mapOptions);
    
    google.maps.event.addListener(map, "click", function(event) {
        setMarker(event.latLng);
    });
}

/*
 * Center the map on a default location, in case geolocation isn't available.
 * This default location is Ghent, Belgium (where I live).
 */
function loadDefaultLocation() {
    var mapOptions = {
        center: new google.maps.LatLng(51.05, 3.72),
        zoom: 12,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById("map"), mapOptions);
    
    google.maps.event.addListener(map, "click", function(event) {
        setMarker(event.latLng);
    });
}

/*
 * Move the marker to the given position.
 * Create a marker if there is none.
 */
function setMarker(position) {
    if (marker) {
        marker.setPosition(position);
    } else {
        var markerOptions = {
            map: map,
            position: position
        };
        marker = new google.maps.Marker(markerOptions);
    }

    // This updates the visibleProperty of the delete button.
    hasLocation.set(true);
}

function clearMarker() {
    if (marker) {
        marker.setMap(null);
        marker = null;
        
        hasLocation.set(false);
    }
}

/*
 * Saves the location of the marker in the Location object.
 */
function saveMarker() {
    if (marker) {
        existingLocation.setLatitude(marker.getPosition().lat());
        existingLocation.setLongitude(marker.getPosition().lng());
    }
}