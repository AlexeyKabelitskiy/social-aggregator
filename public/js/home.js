require(['jquery','underscorejs','backbonejs', './aggregator', './wsmultiplexer'
        ,'bootstrap', './views/shell', './views/feed', './models/feed-model', './views/notifications', './models/notification-model'
        ], function($, _, Backbone, aggregator, Multiplexer){

    aggregator.Router = Backbone.Router.extend({

        routes: {
            "":                 "feed",
            "feed":             "feed",
            "notifications":    "notifications",
            "options":          "options"
        },

        initialize: function () {
            aggregator.connection = new Multiplexer(constants.ws);
            aggregator.shellView = new aggregator.ShellView();
            $('body').append(aggregator.shellView.render().el);
            this.$content = aggregator.shellView.content();
            this.blockUI = { callbacks: aggregator.shellView.blockPageEvents };
        },

        feed: function () {
            // Since the home view never changes, we instantiate it and render it only once
            if (!aggregator.feedView) {
                aggregator.feedView = new aggregator.FeedView({collection: new aggregator.Feed([],this.blockUI)});
                aggregator.feedView.render();
            }
            this.$content.html(aggregator.feedView.el);
            aggregator.shellView.selectMenuItem('menu-feed');
            aggregator.feedView.fetch();
        },

        notifications: function() {
            if (!aggregator.notifications) {
                aggregator.notifications = new aggregator.NotificationsView({collection: new aggregator.Notifications([],this.blockUI)});
                aggregator.notifications.render();
            }
            this.$content.html(aggregator.notifications.el);
            aggregator.shellView.selectMenuItem('menu-notifications');
            aggregator.notifications.fetch();
        },

        options: function() {
            this.$content.html('');
            aggregator.shellView.selectMenuItem('menu-options');
        }
    });

    $(document).ready(function () {
        aggregator.loadTemplates([
     /* views */          "Shell", "Feed", "FeedItem", "Notifications", "Notification",
     /* just templates */ "Alert"],
            function () {
                aggregator.router = new aggregator.Router();
                Backbone.history.start();
            });
    });

});
