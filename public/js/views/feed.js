define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'], function(require, $, Backbone, _, aggregator){
    aggregator.FeedView = Backbone.View.extend({


        render: function() {
            this.$el.empty();
            for(var i=1; i<8; i++) {
                this.$el.append(new aggregator.FeedItemView({model: {id:i}}));
            }
        }

    });

    aggregator.FeedItemView = Backbone.View.extend({
        render: function() {
            this.$el.html(this.template(this.model.attributes));
        }
    });
});