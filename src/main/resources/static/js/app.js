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
        $.get('/jvms', function(data) {
            return data._embedded.jVMList;
        });
    }
})