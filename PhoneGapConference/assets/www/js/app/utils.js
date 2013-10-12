define(function(require, exports, module) {
    
    var transformProperty;
    ['', 'webkit', 'moz', 'ms', 'o'].every( function(prefix) {
        var prop = prefix + (prefix ? 'Transform' : 'transform');
        if( prop in document.body.style ) {
            transformProperty = prop;
            return false; 
        }
        return true;
    });
    
    var setTransform = function(elem, transform) {
        elem.style[transformProperty] = transform;
    };
    
    return {
        setTransform: setTransform
    };
});
