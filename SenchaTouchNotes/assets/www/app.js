Ext.application({
    name: "NotesApp",

    models: ["Note"],
    stores: ["Notes"],
    controllers: ["Notes"],
    views: ["NotesList", "NoteEditor"],

    launch: function () {

        var notesListView = {
            xtype: "noteslistview"
        };
        var noteEditorView = {
            xtype: "noteeditorview"
        };

        Ext.Viewport.add([notesListView, noteEditorView]);

    }
});