define(['require', 'jquery', 'backbonejs', 'underscorejs', '../aggregator', 'moment','./page-collection-model'
       ],
    function (require, $, Backbone, _, aggregator, moment, BaseCollection) {
    aggregator.FeedItem = Backbone.Model.extend({

        initialize: function () {
            this.attributes.timestamp = this.formatRelativeDate(this.attributes.time);
        },

        getDate: function () {
            if (this.attributes.time) {
                return new Date(this.attributes.time)
            }
            return null;
        },

        formatRelativeDate: function(date) {
            moment.lang('en', {
                calendar: {
                    lastDay: '[Yesterday at] LT',
                    sameDay: 'LT',
                    nextDay: '[Tomorrow at] LT',
                    lastWeek: '[last] dddd [at] LT',
                    nextWeek: 'dddd [at] LT',
                    sameElse: 'L'
                }
            });
            return moment(date).calendar();
        }
    });

    aggregator.Feed = BaseCollection.extend({
        model: aggregator.FeedItem,
        url: aggregator.baseUrl() + '/feed',
        initialize: function(models, options){
            this.subscribe(options || {});
        }

    });
});