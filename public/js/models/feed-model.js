define(['require', 'jquery', 'backbonejs','underscorejs','../aggregator'], function(require, $, Backbone, _, aggregator){
    aggregator.FeedItem = Backbone.Model.extend ({

    });

    aggregator.Feed = Backbone.Collection.extend ({
       model: aggregator.FeedItem,
       url: aggregator.baseUrl() + '/feed'
    });
});