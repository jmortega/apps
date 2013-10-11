var $, navigator, alert, document;


var Brujula = Brujula || {};

// event handlers for the compass stuff,
// one for updating the header text
// the other for rotating the compass
Brujula.Compass = (function () {
	var lastHeading = -1,
		// cache the jQuery selectors
		$headText = $("header > h1"),
		$compass = $("#compass"),
		// displays the degree
		updateHeadingText = function (event, heading) {
			event.preventDefault();
			$('#footer').html(heading + "&deg;");
			return false;
		},
		// adjusts the rotation of the compass
		updateCompass = function (event, heading) {
			event.preventDefault();
			// to make the compass dial point the right way
			var rotation = 360 - heading;
			rotateDeg = 'rotate(' + rotation + 'deg)';

			$compass.css('-webkit-transform', rotateDeg);
	
			$('#grados').html(heading + "&deg;"+"      "+rotateDeg);
			
			$('#direction').html("Direction: "+convertToText(heading));
			
			return false;
		};
	// bind both of the event handlers to the "newHeading" event
	$("body").bind("newHeading", updateCompass).bind("newHeading", updateHeadingText);
}());



document.addEventListener('deviceready', function () {
    Brujula.Compass.watchId = navigator.compass.watchHeading(function (heading) {
		// only magnetic heading works universally on iOS and Android
		// round off the heading then trigger newHeading event for any listeners
		var newHeading = Math.round(heading.magneticHeading);
		$("body").trigger("newHeading", [newHeading]);
	}, function (error) {

		//Remove the watch since we're having a problem
        navigator.compass.clearWatchFilter(watchID);
        //Then tell the user what happened.
        if(error.code == CompassError.COMPASS_NOT_SUPPORTED) {
            alert("Compass not supported.");
        } else if(error.code == CompassError.COMPASS_INTERNAL_ERR) {
            alert("Compass Internal Error");
        } else {
            alert("Unknown heading error!");
        }
        
	}, {frequency : 100});
});

//Accept the magneticHeading value
//and convert into a text representation
function convertToText(mh) {
var textDirection;
if (typeof mh !== "number") {
textDirection = '';
} else if (mh >= 337.5 || (mh >= 0 && mh <= 22.5)) {
textDirection = 'NORTH';
} else if (mh >= 22.5 && mh <= 67.5) {
textDirection = 'NORTH-EAST';
} else if (mh >= 67.5 && mh <= 112.5) {
textDirection = 'EAST';
} else if (mh >= 112.5 && mh <= 157.5) {
textDirection = 'SOUTH-EAST';
} else if (mh >= 157.5 && mh <= 202.5) {
textDirection = 'SOUTH';
} else if (mh >= 202.5 && mh <= 247.5) {
textDirection = 'SOUTH-WEST';
} else if (mh >= 247.5 && mh <= 292.5) {
textDirection = 'WEST';
} else if (mh >= 292.5 && mh <= 337.5) {
textDirection = 'NORTH-WEST';
} else {
textDirection = textDirection;
}
return textDirection;
}