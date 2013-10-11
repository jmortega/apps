var Notes = Notes || {};

Notes.dataContext = (function ($) {
    "use strict";
    var notesList = [],
    notesListStorageKey;

    function getRandomInt(min, max) {
        return Math.floor(Math.random() * (max - min + 1)) + min;
    };

    var createBlankNote = function () {

        var dateCreated = new Date();
        var id = dateCreated.getTime().toString() + (getRandomInt(0, 100)).toString();
        var noteModel = new Notes.NoteModel({
            id: id,
            dateCreated: dateCreated,
            title: "",
            narrative: ""
        });

        return noteModel;
    };

    var loadNotesFromLocalStorage = function () {

        var storedNotes = $.jStorage.get(notesListStorageKey);

        if (storedNotes !== null) {
            notesList = storedNotes;
        }

    };

    var saveNotesToLocalStorage = function () {
        $.jStorage.set(notesListStorageKey, notesList);
    };

    var saveNote = function (noteModel) {

        var found = false;
        var i;

        for (i = 0; i < notesList.length; i += 1) {
            if (notesList[i].id === noteModel.id) {
                notesList[i] = noteModel;
                found = true;
                i = notesList.length;
            }
        }

        if (!found) {
            notesList.splice(0, 0, noteModel);
        }

        saveNotesToLocalStorage();
    };

    var deleteNote = function (noteModel) {

        var i;
        for (i = 0; i < notesList.length; i += 1) {
            if (notesList[i].id === noteModel.id) {
                notesList.splice(i, 1);
                i = notesList.length;
            }
        }

        saveNotesToLocalStorage();
    };

    var getNotesList = function () {
        return notesList;
    };

    var init = function (storageKey) {
        notesListStorageKey = storageKey;
        loadNotesFromLocalStorage();
    };

    var pub = {
        init: init,
        createBlankNote: createBlankNote,
        getNotesList: getNotesList,
        saveNote: saveNote,
        deleteNote: deleteNote
    };

    return pub;

} (jQuery));