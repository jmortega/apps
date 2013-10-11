var Notes = Notes || {};

Notes.NoteModel = function (config) {
    "use strict";
    this.id = config.id;
    this.dateCreated = config.dateCreated;
    this.title = config.title;
    this.narrative = config.narrative;
};

Notes.NoteModel.prototype.isValid = function () {
    "use strict";
    if (this.title && this.title.length > 0) {
        return true;
    }
    return false;
};