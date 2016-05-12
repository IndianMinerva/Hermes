App = Ember.Application.create();
App.Router.map(function() {
    this.resource('about');
});

App.IndexRoute = Ember.Route.extend({
    redirect: function () {
        this.transitionTo('about');
    }
});