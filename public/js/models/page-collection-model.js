define(['require', 'jquery', 'backbonejs', 'underscorejs', '../aggregator', 'moment'], function (require, $, Backbone, _, aggregator, moment) {
    var BaseCollection = Backbone.Collection.extend({
        subscribe: function(options){
            _.each(options.callbacks, function(val, key) {
                  this.on(key, val)
            }, this);
        }

    });

    return BaseCollection;
});