define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'
    ,'../models/feed-model'],
    function(require, $, Backbone, _, aggregator){
        aggregator.FeedView = Backbone.View.extend({


            render: function() {
                this.$el.empty();
                var thisFeed = this;
                this.collection.each(function(feedItem){
                    thisFeed.$el.append(new aggregator.FeedItemView({model: feedItem}).render().el);
                });
                return this;
            }

        });

        aggregator.FeedItemView = Backbone.View.extend({
            render: function() {
                this.$el.html(this.template(this.model.attributes));
                return this;
            }
        });
    }
);