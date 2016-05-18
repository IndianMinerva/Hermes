App = Ember.Application.create();
App.Router.map(function() {
    this.resource('jvms');
});

App.IndexRoute = Ember.Route.extend({
    redirect: function () {
        this.transitionTo('jvms');
    }
});

App.JvmsRoute = Ember.Route.extend({
    model: function() {
        var self = this;
        var arr = [];
        $.get('/jvms').done(function(data) {
            arr = data._embedded.jVMList;
        });
        return arr;
    }
});