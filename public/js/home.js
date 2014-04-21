require(['jquery','underscorejs','backbonejs', './aggregator'
        ,'bootstrap', './views/feed', './views/shell', './models/feed-model'
        ], function($, _, Backbone, aggregator){

    aggregator.Router = Backbone.Router.extend({

        routes: {
            "":                 "feed",
            "feed":             "feed",
            "notifications":    "notifications",
            "options":          "options"
        },

        initialize: function () {
            aggregator.shellView = new aggregator.ShellView();
            $('body').html(aggregator.shellView.render().el);
            this.$content = $("#content");
        },

        feed: function () {
            // Since the home view never changes, we instantiate it and render it only once
            if (!aggregator.feedView) {
                aggregator.feedView = new aggregator.FeedView({collection: new aggregator.Feed({})});
                aggregator.feedView.render();
            }
            this.$content.html(aggregator.feedView.el);
            aggregator.shellView.selectMenuItem('menu-feed');
        },

        notifications: function() {
            this.$content.html('');
            aggregator.shellView.selectMenuItem('menu-notifications');
        },

        options: function() {
            this.$content.html('');
            aggregator.shellView.selectMenuItem('menu-options');
        }
    });

    $(document).ready(function () {
        aggregator.loadTemplates(["Shell", "Feed", "FeedItem"],
            function () {
                aggregator.router = new aggregator.Router();
                Backbone.history.start();
            });
    });

});
